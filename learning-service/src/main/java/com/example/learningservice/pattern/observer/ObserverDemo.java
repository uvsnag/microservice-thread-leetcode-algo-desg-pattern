package com.example.learningservice.pattern.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * ===================================================================
 * OBSERVER PATTERN
 * ===================================================================
 * Intent: Define a one-to-many dependency between objects so that when
 *         one object (Subject) changes state, all its dependents
 *         (Observers) are notified and updated automatically.
 *
 * When to use:
 *   - Event handling systems (GUI button clicks)
 *   - Message brokers (Kafka/RabbitMQ are Observer pattern at scale!)
 *   - Stock price monitoring
 *   - Social media notifications (user posts → followers get notified)
 *
 * Key points for interview:
 *   - Loose coupling: Subject doesn't know observer details
 *   - Push vs Pull model
 *   - Java has built-in Observer (deprecated in Java 9)
 *   - Spring's ApplicationEvent/ApplicationListener IS observer pattern
 *   - Kafka/RabbitMQ = distributed observer pattern
 *
 * This demo: Stock price monitor notifying investors
 * ===================================================================
 */
public class ObserverDemo {

    // =====================================================
    // STEP 1: Define the Observer interface
    // =====================================================
    // Anyone who wants to receive updates must implement this
    public interface StockObserver {
        void update(String stockSymbol, double oldPrice, double newPrice);
        String getName();
    }

    // =====================================================
    // STEP 2: Define the Subject (Observable)
    // =====================================================
    // The subject maintains a list of observers and notifies them on changes
    public static class StockMarket {
        // List of all registered observers
        private final List<StockObserver> observers = new ArrayList<>();
        private String stockSymbol;
        private double currentPrice;
        private final List<String> eventLog = new ArrayList<>();

        public StockMarket(String stockSymbol, double initialPrice) {
            this.stockSymbol = stockSymbol;
            this.currentPrice = initialPrice;
        }

        // SUBSCRIBE — add an observer to the notification list
        public void subscribe(StockObserver observer) {
            observers.add(observer);
            eventLog.add("+ " + observer.getName() + " subscribed to " + stockSymbol);
        }

        // UNSUBSCRIBE — remove an observer
        public void unsubscribe(StockObserver observer) {
            observers.remove(observer);
            eventLog.add("- " + observer.getName() + " unsubscribed from " + stockSymbol);
        }

        // When price changes, NOTIFY ALL observers
        public void setPrice(double newPrice) {
            double oldPrice = this.currentPrice;
            this.currentPrice = newPrice;
            eventLog.add(String.format(">>> %s price changed: $%.2f → $%.2f", stockSymbol, oldPrice, newPrice));
            notifyObservers(oldPrice, newPrice);
        }

        // Loop through all observers and call their update() method
        private void notifyObservers(double oldPrice, double newPrice) {
            for (StockObserver observer : observers) {
                observer.update(stockSymbol, oldPrice, newPrice);
            }
        }

        public List<String> getEventLog() {
            return eventLog;
        }
    }

    // =====================================================
    // STEP 3: Implement Concrete Observers
    // =====================================================

    /** Mobile app investor — gets push notification */
    public static class MobileInvestor implements StockObserver {
        private final String name;
        private final List<String> notifications = new ArrayList<>();

        public MobileInvestor(String name) { this.name = name; }

        @Override
        public void update(String stockSymbol, double oldPrice, double newPrice) {
            // React to the price change
            String direction = newPrice > oldPrice ? "📈 UP" : "📉 DOWN";
            double change = ((newPrice - oldPrice) / oldPrice) * 100;
            String msg = String.format("[MOBILE] %s: %s %s %.1f%% ($%.2f → $%.2f)",
                    name, stockSymbol, direction, Math.abs(change), oldPrice, newPrice);
            notifications.add(msg);
        }

        @Override
        public String getName() { return name + " (Mobile)"; }

        public List<String> getNotifications() { return notifications; }
    }

    /** Email investor — gets email alert */
    public static class EmailInvestor implements StockObserver {
        private final String name;
        private final List<String> notifications = new ArrayList<>();

        public EmailInvestor(String name) { this.name = name; }

        @Override
        public void update(String stockSymbol, double oldPrice, double newPrice) {
            // Only alert if price change > 5%
            double changePercent = Math.abs((newPrice - oldPrice) / oldPrice) * 100;
            if (changePercent > 5.0) {
                String msg = String.format("[EMAIL] %s: ALERT! %s moved %.1f%% ($%.2f → $%.2f)",
                        name, stockSymbol, changePercent, oldPrice, newPrice);
                notifications.add(msg);
            } else {
                notifications.add(String.format("[EMAIL] %s: %s change %.1f%% (below threshold, no email)",
                        name, stockSymbol, changePercent));
            }
        }

        @Override
        public String getName() { return name + " (Email)"; }

        public List<String> getNotifications() { return notifications; }
    }

    /**
     * Demo showing how observers react to subject changes
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Observer Pattern: Stock Price Monitor ===\n\n");

        // Create the subject (stock market for AAPL)
        StockMarket apple = new StockMarket("AAPL", 150.00);

        // Create observers
        MobileInvestor alice = new MobileInvestor("Alice");
        MobileInvestor bob = new MobileInvestor("Bob");
        EmailInvestor charlie = new EmailInvestor("Charlie");

        // Subscribe observers
        apple.subscribe(alice);
        apple.subscribe(bob);
        apple.subscribe(charlie);

        // Price changes — all 3 observers are notified
        apple.setPrice(155.00);  // +3.3%
        apple.setPrice(162.00);  // +4.5%

        // Bob unsubscribes
        apple.unsubscribe(bob);

        // Only Alice and Charlie are notified
        apple.setPrice(140.00);  // -13.6% (big drop!)

        // Print event log from subject
        sb.append("--- Stock Market Event Log ---\n");
        for (String log : apple.getEventLog()) {
            sb.append(log).append("\n");
        }

        // Print what each observer received
        sb.append("\n--- Alice's Mobile Notifications ---\n");
        for (String n : alice.getNotifications()) sb.append(n).append("\n");

        sb.append("\n--- Bob's Mobile Notifications (unsubscribed before last change) ---\n");
        for (String n : bob.getNotifications()) sb.append(n).append("\n");

        sb.append("\n--- Charlie's Email Alerts (only >5% changes) ---\n");
        for (String n : charlie.getNotifications()) sb.append(n).append("\n");

        sb.append("\n--- Key Takeaway ---\n");
        sb.append("Subject doesn't know observer implementation details.\n");
        sb.append("Observers can filter/react differently to the same event.\n");
        sb.append("Kafka and RabbitMQ implement this pattern at distributed scale.\n");

        return sb.toString();
    }
}
