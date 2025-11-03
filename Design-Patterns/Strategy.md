### Problem Setup

- Your Vehicle Rental app supports multiple payment methods:
  - Credit Card
  - UPI
  - PayPal
  - Wallet

- You don’t want a messy if-else or switch block like this 👇

```
if (paymentType.equals("CREDIT_CARD")) { ... }
else if (paymentType.equals("UPI")) { ... }
else if (paymentType.equals("PAYPAL")) { ... }
```

#### That violates Open-Closed Principle — every time a new payment method comes, you modify existing code.

### Deftinition 
The Strategy pattern lets you define a family of algorithms (behaviors), encapsulate each one, and make them interchangeable at runtime.


#### Payment Strategy 
```
public interface PaymentStrategy {
    Payment pay(UserPaymentInfo userInfo, Booking booking);
}
```

#### VisaPaymentStrategy

```
public class VisaPaymentStrategy implements PaymentStrategy {
    @Override
    public Payment processPayment(UserPaymentInfo userPaymentInfo, Booking booking) {
        String transactionId = UUID.randomUUID().toString(); // assuming the corresponding 3rd party service returned this
        return new Payment(
                UUID.randomUUID().toString(),
                PaymentMethod.VISA,
                transactionId, booking.getId(),
                booking.getAmount());
    }
}
```

#### MasterCardPaymentStrategy
```
public class MasterCardPaymentStrategy implements PaymentStrategy {

    @Override
    public Payment processPayment(UserPaymentInfo userPaymentInfo, Booking booking) {
        String transactionId = UUID.randomUUID().toString(); // assuming the corresponding 3rd party service returned this
        return new Payment(
                UUID.randomUUID().toString(),
                PaymentMethod.MASTER_CARD,
                transactionId, booking.getId(),
                booking.getAmount());
    }
}
```

#### PaymentProcessor
```
public class PaymentProcessor {
    private PaymentStrategy paymentStrategy;

    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public Payment processPayment(UserPaymentInfo userInfo, Booking booking) {
        return paymentStrategy.pay(userInfo, booking);
    }

    // Allow switching strategy dynamically
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
}
```


```
public class PaymentProcessorFactory {

    public static PaymentProcessor getPaymentProcessor(PaymentMethod method) {
        switch (method) {
            case MASTER_CARD -> {
                return new PaymentProcessor(new MasterCardPaymentStrategy());
            }
            case VISA -> {
                return new PaymentProcessor(new VisaPaymentStrategy());
            }
            case UPI -> {
                return new PaymentProcessor(new UPIPaymentStrategy());
            }
        }
        return null; // use the default payment strategy here
    }
}
```


### Usage

```
   PaymentProcessor paymentProcessor = PaymentProcessorFactory.getPaymentProcessor(PaymentMethod.VISA);
        Payment payment = paymentProcessor.processPayment(null, null);

```

OR

```
Booking booking = new Booking("B001", 1850.0, "CAR");
        UserPaymentInfo userInfo = new UserPaymentInfo("U123", "UPI", "yoga@upi");

        PaymentProcessor processor = new PaymentProcessor(new UPIPaymentStrategy());
        Payment payment = processor.processPayment(userInfo, booking);
        System.out.println(payment);

        // Switch to another strategy at runtime
        processor.setPaymentStrategy(new CreditCardPaymentStrategy());
        Payment payment2 = processor.processPayment(
                new UserPaymentInfo("U123", "CREDIT_CARD", "1234-5678-9012"),
                booking);
        System.out.println(payment2);
```



