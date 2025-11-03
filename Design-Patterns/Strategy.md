Problem Setup

- Your Vehicle Rental app supports multiple payment methods:
  - Credit Card
  - UPI
  - PayPal
  - Wallet

- You don’t want a messy if-else or switch block like this 👇

> if (paymentType.equals("CREDIT_CARD")) { ... }
> else if (paymentType.equals("UPI")) { ... }
> else if (paymentType.equals("PAYPAL")) { ... }


** That violates Open-Closed Principle — every time a new payment method comes, you modify existing code.
