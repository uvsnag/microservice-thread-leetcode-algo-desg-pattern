package com.example.learningservice.pattern.factory;

/**
 * ===================================================================
 * FACTORY METHOD PATTERN
 * ===================================================================
 * Intent: Define an interface for creating objects, but let subclasses
 *         decide which class to instantiate.
 *
 * When to use:
 *   - When you don't know the exact type of object to create at compile time
 *   - Payment processing (CreditCard, PayPal, BankTransfer)
 *   - Notification sending (Email, SMS, Push)
 *   - Database drivers (MySQL, PostgreSQL, Oracle)
 *
 * Key points for interview:
 *   - Factory encapsulates object creation logic
 *   - Client code depends on the interface, not concrete classes
 *   - Open/Closed Principle: add new types without modifying existing code
 *   - Simple Factory vs Factory Method vs Abstract Factory
 *
 * This demo: Notification system with Email, SMS, and Push notifications
 * ===================================================================
 */
public class FactoryDemo {

    // =====================================================
    // STEP 1: Define the Product interface
    // =====================================================
    // All notifications must implement this interface
    public interface Notification {
        String getType();
        String send(String recipient, String message);
    }

    // =====================================================
    // STEP 2: Create Concrete Products
    // =====================================================

    /** Email notification — sends via SMTP */
    public static class EmailNotification implements Notification {
        @Override
        public String getType() {
            return "EMAIL";
        }

        @Override
        public String send(String recipient, String message) {
            // In real app: use JavaMailSender or AWS SES
            return String.format("[EMAIL] To: %s | Subject: %s | Status: SENT via SMTP", recipient, message);
        }
    }

    /** SMS notification — sends via SMS gateway */
    public static class SmsNotification implements Notification {
        @Override
        public String getType() {
            return "SMS";
        }

        @Override
        public String send(String recipient, String message) {
            // In real app: use Twilio or AWS SNS
            return String.format("[SMS] To: %s | Body: %s | Status: SENT via SMS Gateway", recipient, message);
        }
    }

    /** Push notification — sends via mobile push service */
    public static class PushNotification implements Notification {
        @Override
        public String getType() {
            return "PUSH";
        }

        @Override
        public String send(String recipient, String message) {
            // In real app: use Firebase Cloud Messaging (FCM) or APNs
            return String.format("[PUSH] To device: %s | Alert: %s | Status: SENT via FCM", recipient, message);
        }
    }

    // =====================================================
    // STEP 3: Create the Factory
    // =====================================================
    // The factory decides WHICH class to instantiate based on the input type.
    // Client code never uses "new EmailNotification()" directly.
    public static class NotificationFactory {

        /**
         * Factory Method — creates the right notification based on type string.
         *
         * WHY THIS IS BETTER THAN DIRECT INSTANTIATION:
         * - Adding a new type (e.g., SLACK) only changes THIS method
         * - Client code stays unchanged
         * - Can add validation, caching, pooling here
         */
        public static Notification createNotification(String type) {
            if (type == null) {
                throw new IllegalArgumentException("Notification type cannot be null");
            }
            return switch (type.toUpperCase()) {
                case "EMAIL" -> new EmailNotification();
                case "SMS"   -> new SmsNotification();
                case "PUSH"  -> new PushNotification();
                default -> throw new IllegalArgumentException("Unknown notification type: " + type);
            };
        }
    }

    /**
     * Demo method showing how the factory is used
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Factory Method Pattern: Notification System ===\n\n");

        // Client code only knows about the Notification interface
        // It doesn't know or care about EmailNotification, SmsNotification, etc.
        String[] types = {"EMAIL", "SMS", "PUSH"};
        for (String type : types) {
            // Factory creates the right object based on the type string
            Notification notification = NotificationFactory.createNotification(type);
            sb.append("Created: ").append(notification.getType()).append("\n");
            sb.append(notification.send("user@example.com", "Hello from Factory!")).append("\n\n");
        }

        sb.append("--- Key Takeaway ---\n");
        sb.append("Client code uses NotificationFactory.createNotification(type)\n");
        sb.append("It never does 'new EmailNotification()' directly.\n");
        sb.append("To add SLACK notification: just add a new class and one case in the factory.\n");

        return sb.toString();
    }
}
