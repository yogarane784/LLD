### Deftinition
- Saga Pattern manages distributed transactions where multiple services must execute a sequence of steps
- And if one fails, previous successful steps are compensated (rolled back logically).
- It ensures data consistency across services without a distributed transaction (2PC).

- Let’s say we have a Booking Service that coordinates:
    - BookingService → Create booking entry
    - PaymentService → Process payment
    - BookingService → Update booking status (Confirmed/Failed)
    - NotificationService → Send confirmation or failure email

 Each service performs its local transaction, then publishes an event for the next service to act upon.

 | Step | Service              | Action                                        | Compensation                   |
| ---- | -------------------- | --------------------------------------------- | ------------------------------ |
| 1    | Booking Service      | Create booking record with status = “PENDING” | Delete the booking entry       |
| 2    | Payment Service      | Charge customer                               | Refund the payment             |
| 3    | Booking Service      | Mark booking as “CONFIRMED”                   | Set status back to “CANCELLED” |
| 4    | Notification Service | Send confirmation                             | Send cancellation notification |


## There are two main ways to implement Sagas:

### Choreography-based Saga — event-driven, decentralized coordination
    - Each service listens for specific events and emits the next event. I.e Each service does its job and publishes an event for the next step
    - Why is it called “Choreography-based : In a dance choreography, there’s no central leader — 
          - each dancer knows their role, when to move, and how to respond to others’ movements.
          - The coordination happens through rules and signals, not a conductor.

```
Booking Service

public void createBooking(Booking booking) {
booking.setStatus("PENDING");
bookingRepository.save(booking);
eventBus.publish(new BookingCreatedEvent(booking.getId()));
}

@EventListener
public void handlePaymentEvent(PaymentSuccessfulEvent event) {
    bookingRepository.updateStatus(event.getBookingId(), "CONFIRMED");
    eventBus.publish(new BookingConfirmedEvent(event.getBookingId()));
}

@EventListener
public void handlePaymentFailed(PaymentFailedEvent event) {
    bookingRepository.updateStatus(event.getBookingId(), "CANCELLED");
    eventBus.publish(new BookingCancelledEvent(event.getBookingId()));
}
```

```
PaymentService

@EventListener
public void handleBookingCreated(BookingCreatedEvent event) {
    boolean success = paymentGateway.charge(event.getBookingId());
    if (success) eventBus.publish(new PaymentSuccessfulEvent(event.getBookingId()));
    else eventBus.publish(new PaymentFailedEvent(event.getBookingId()));
}



```
### Orchestration-based Saga — a central controller (Saga Orchestrator) coordinates all steps

```
public class BookingSagaOrchestrator {

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public BookingSagaOrchestrator(BookingService bookingService,
                                   PaymentService paymentService,
                                   NotificationService notificationService) {
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    public void startBooking(String userId, String hotelId, double amount) {
        SagaContext ctx = new SagaContext();

        try {
            System.out.println("\n🚀 Starting Saga: " + ctx.getSagaId());

            // Step 1: Create booking
            String bookingId = bookingService.createBooking(userId, hotelId);
            ctx.setBookingId(bookingId);
            ctx.markStepCompleted("CreateBooking");

            // Step 2: Make payment
            paymentService.makePayment(bookingId, amount);
            ctx.markStepCompleted("MakePayment");

            // Step 3: Update booking status
            bookingService.updateStatus(bookingId, "CONFIRMED");
            ctx.markStepCompleted("UpdateStatus");

            // Step 4: Send confirmation
            notificationService.sendConfirmation(bookingId);
            ctx.markStepCompleted("SendNotification");

            ctx.setSagaStatus(SagaStatus.COMPLETED);
            System.out.println("✅ Saga completed successfully: " + ctx.getSagaId());

        } catch (Exception e) {
            ctx.setSagaStatus(SagaStatus.FAILED);
            System.out.println("\n⚠️ Saga failed: " + e.getMessage());
            compensate(ctx);
        }
    }

    private void compensate(SagaContext ctx) {
        System.out.println("🚑 Starting compensation for " + ctx.getSagaId());

        if (ctx.isStepCompleted("MakePayment")) {
            paymentService.refundPayment(ctx.getBookingId());
        }

        if (ctx.isStepCompleted("CreateBooking")) {
            bookingService.cancelBooking(ctx.getBookingId());
        }

        notificationService.sendFailure(ctx.getBookingId());

        System.out.println("🧾 Compensation completed for " + ctx.getSagaId());
    }
}

```


```

public class Main {
    public static void main(String[] args) {
        BookingService bookingService = new BookingService();
        PaymentService paymentService = new PaymentService();
        NotificationService notificationService = new NotificationService();

        BookingSagaOrchestrator orchestrator =
                new BookingSagaOrchestrator(bookingService, paymentService, notificationService);

        // Case 1: Successful booking
        orchestrator.startBooking("user1", "hotel123", 2000);

        // Case 2: Payment failure triggers compensation
        orchestrator.startBooking("user2", "hotel456", 10000);

        // Case 3: Booking creation fails
        orchestrator.startBooking("user3", null, 1000);
    }
}

```
