package com.example.learningservice.pattern.adapter;

/**
 * ===================================================================
 * ADAPTER PATTERN
 * ===================================================================
 * Intent: Convert the interface of a class into another interface that
 *         clients expect. Adapter lets classes work together that
 *         couldn't otherwise because of incompatible interfaces.
 *
 * When to use:
 *   - Integrating with legacy code or third-party libraries
 *   - Converting XML service response to JSON
 *   - Wrapping different payment gateway APIs to a common interface
 *   - JDBC is an adapter — uniform API over different databases
 *
 * Key points for interview:
 *   - "Wrapper" pattern — wraps an incompatible class
 *   - Two flavors: Object Adapter (composition) vs Class Adapter (inheritance)
 *   - Object Adapter is preferred (composition over inheritance)
 *   - In Spring: you often adapt external service clients to your domain interface
 *
 * This demo: Multiple payment gateways adapted to a common interface
 * ===================================================================
 */
public class AdapterDemo {

    // =====================================================
    // STEP 1: The Target interface (what our code expects)
    // =====================================================
    public interface PaymentProcessor {
        String charge(String customerId, double amount, String currency);
        String refund(String transactionId, double amount);
        String getName();
    }

    // =====================================================
    // STEP 2: "Legacy" / third-party classes with INCOMPATIBLE interfaces
    // =====================================================

    /** Stripe SDK — uses different method names and parameter types */
    public static class StripeApi {
        // Stripe uses cents (integer), not dollars (double)
        public String createCharge(int amountInCents, String currency, String source) {
            return String.format("STRIPE-TXN-%d", System.nanoTime() % 100000);
        }

        public boolean reverseCharge(String chargeId) {
            return true; // Simulated
        }
    }

    /** PayPal SDK — completely different API structure */
    public static class PayPalSdk {
        // PayPal uses Order objects, not simple parameters
        public String createOrder(double total, String currencyCode) {
            return String.format("PAYPAL-ORD-%d", System.nanoTime() % 100000);
        }

        public String capturePayment(String orderId) {
            return "CAPTURED";
        }

        public String refundPayment(String captureId, double amount) {
            return String.format("PAYPAL-REF-%d", System.nanoTime() % 100000);
        }
    }

    /** Old bank transfer system — uses XML-style requests */
    public static class LegacyBankSystem {
        public int processTransfer(String accountNumber, long amountInMinorUnits, int currencyCode) {
            // Returns: 0=success, 1=insufficient funds, 2=invalid account
            return 0; // Simulated success
        }

        public int reverseTransfer(int transactionRef) {
            return 0; // Simulated success
        }
    }

    // =====================================================
    // STEP 3: Adapters — bridge the incompatible interfaces
    // =====================================================

    /** Adapts Stripe API to our PaymentProcessor interface */
    public static class StripeAdapter implements PaymentProcessor {
        private final StripeApi stripeApi; // Composition: HAS-A StripeApi

        public StripeAdapter(StripeApi stripeApi) {
            this.stripeApi = stripeApi;
        }

        @Override
        public String charge(String customerId, double amount, String currency) {
            // ADAPT: Convert dollars to cents (Stripe expects cents)
            int amountInCents = (int) (amount * 100);
            String txnId = stripeApi.createCharge(amountInCents, currency, customerId);
            return String.format("Stripe charge: $%.2f %s → txn: %s", amount, currency, txnId);
        }

        @Override
        public String refund(String transactionId, double amount) {
            boolean success = stripeApi.reverseCharge(transactionId);
            return String.format("Stripe refund: %s → %s", transactionId, success ? "SUCCESS" : "FAILED");
        }

        @Override
        public String getName() { return "Stripe"; }
    }

    /** Adapts PayPal SDK to our PaymentProcessor interface */
    public static class PayPalAdapter implements PaymentProcessor {
        private final PayPalSdk payPalSdk;

        public PayPalAdapter(PayPalSdk payPalSdk) {
            this.payPalSdk = payPalSdk;
        }

        @Override
        public String charge(String customerId, double amount, String currency) {
            // ADAPT: PayPal needs order creation then capture
            String orderId = payPalSdk.createOrder(amount, currency);
            String status = payPalSdk.capturePayment(orderId);
            return String.format("PayPal charge: $%.2f %s → order: %s (%s)", amount, currency, orderId, status);
        }

        @Override
        public String refund(String transactionId, double amount) {
            String refundId = payPalSdk.refundPayment(transactionId, amount);
            return String.format("PayPal refund: %s → refund: %s", transactionId, refundId);
        }

        @Override
        public String getName() { return "PayPal"; }
    }

    /** Adapts legacy bank system to our PaymentProcessor interface */
    public static class BankAdapter implements PaymentProcessor {
        private final LegacyBankSystem bankSystem;

        public BankAdapter(LegacyBankSystem bankSystem) {
            this.bankSystem = bankSystem;
        }

        @Override
        public String charge(String customerId, double amount, String currency) {
            // ADAPT: Convert to minor units and currency code
            long amountInMinorUnits = (long) (amount * 100);
            int currencyCode = "USD".equals(currency) ? 840 : 978; // ISO 4217
            int result = bankSystem.processTransfer(customerId, amountInMinorUnits, currencyCode);
            String status = result == 0 ? "SUCCESS" : "FAILED(code=" + result + ")";
            return String.format("Bank transfer: $%.2f %s → %s", amount, currency, status);
        }

        @Override
        public String refund(String transactionId, double amount) {
            int result = bankSystem.reverseTransfer(Integer.parseInt(transactionId.replaceAll("\\D", "0").substring(0, Math.min(9, transactionId.length()))));
            return String.format("Bank refund: %s → %s", transactionId, result == 0 ? "SUCCESS" : "FAILED");
        }

        @Override
        public String getName() { return "Bank Transfer"; }
    }

    /**
     * Demo — same client code works with ALL payment providers
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Adapter Pattern: Payment Gateway Integration ===\n\n");

        // Create adapters for each payment system
        PaymentProcessor[] processors = {
                new StripeAdapter(new StripeApi()),
                new PayPalAdapter(new PayPalSdk()),
                new BankAdapter(new LegacyBankSystem())
        };

        // SAME client code for ALL providers — that's the power of Adapter!
        for (PaymentProcessor processor : processors) {
            sb.append("--- ").append(processor.getName()).append(" ---\n");
            sb.append(processor.charge("CUST-001", 99.99, "USD")).append("\n");
            sb.append(processor.refund("TXN-12345", 49.99)).append("\n\n");
        }

        sb.append("--- Key Takeaway ---\n");
        sb.append("Three INCOMPATIBLE APIs (Stripe cents, PayPal orders, Bank minor units)\n");
        sb.append("All adapted to ONE common interface: PaymentProcessor.\n");
        sb.append("Client code doesn't know or care which provider is used.\n");
        sb.append("JDBC is a real-world adapter: same API for MySQL, PostgreSQL, Oracle.\n");

        return sb.toString();
    }
}
