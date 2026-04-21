package com.example.learningservice.pattern.strategy;

import java.util.List;

/**
 * ===================================================================
 * STRATEGY PATTERN
 * ===================================================================
 * Intent: Define a family of algorithms, encapsulate each one, and make
 *         them interchangeable. Strategy lets the algorithm vary
 *         independently from the clients that use it.
 *
 * When to use:
 *   - Multiple sorting algorithms (user picks which one)
 *   - Payment methods (credit card, paypal, crypto)
 *   - Compression algorithms (ZIP, GZIP, RAR)
 *   - Pricing strategies (regular, discount, premium)
 *
 * Key points for interview:
 *   - Eliminates complex if-else / switch chains
 *   - Open/Closed Principle: add new strategies without changing context
 *   - Strategy is selected at RUNTIME (unlike Template Method at compile time)
 *   - In Spring: different @Service beans implement the same interface
 *
 * This demo: Pricing calculator with different discount strategies
 * ===================================================================
 */
public class StrategyDemo {

    // =====================================================
    // STEP 1: Define the Strategy interface
    // =====================================================
    public interface PricingStrategy {
        String getName();
        double calculatePrice(double basePrice, int quantity);
        String getDescription();
    }

    // =====================================================
    // STEP 2: Implement Concrete Strategies
    // =====================================================

    /** Regular pricing — no discount */
    public static class RegularPricing implements PricingStrategy {
        @Override
        public String getName() { return "REGULAR"; }

        @Override
        public double calculatePrice(double basePrice, int quantity) {
            // Simple: price × quantity, no discount
            return basePrice * quantity;
        }

        @Override
        public String getDescription() { return "Full price, no discounts applied"; }
    }

    /** Premium member pricing — 20% off everything */
    public static class PremiumPricing implements PricingStrategy {
        private static final double DISCOUNT_RATE = 0.20; // 20% off

        @Override
        public String getName() { return "PREMIUM"; }

        @Override
        public double calculatePrice(double basePrice, int quantity) {
            // Apply 20% discount: price × quantity × (1 - 0.20)
            double total = basePrice * quantity;
            return total * (1 - DISCOUNT_RATE);
        }

        @Override
        public String getDescription() { return "Premium member: 20% discount on all items"; }
    }

    /** Bulk pricing — tiered discount based on quantity */
    public static class BulkPricing implements PricingStrategy {
        @Override
        public String getName() { return "BULK"; }

        @Override
        public double calculatePrice(double basePrice, int quantity) {
            // Tiered discount:
            //   1-9 items   → no discount
            //   10-49 items → 10% off
            //   50+ items   → 25% off
            double total = basePrice * quantity;
            if (quantity >= 50) {
                return total * 0.75;  // 25% off
            } else if (quantity >= 10) {
                return total * 0.90;  // 10% off
            }
            return total; // no discount
        }

        @Override
        public String getDescription() { return "Bulk: 10% off for 10+, 25% off for 50+ items"; }
    }

    /** Seasonal sale pricing — flat $5 off per item */
    public static class SeasonalPricing implements PricingStrategy {
        private static final double FLAT_DISCOUNT = 5.0;

        @Override
        public String getName() { return "SEASONAL"; }

        @Override
        public double calculatePrice(double basePrice, int quantity) {
            // Flat $5 off per item, but price can't go below $1
            double discountedPrice = Math.max(basePrice - FLAT_DISCOUNT, 1.0);
            return discountedPrice * quantity;
        }

        @Override
        public String getDescription() { return "Seasonal sale: $5 off per item (min $1)"; }
    }

    // =====================================================
    // STEP 3: The Context class that USES the strategy
    // =====================================================
    // ShoppingCart doesn't know HOW the price is calculated.
    // It delegates to whatever PricingStrategy is set.
    public static class ShoppingCart {
        private PricingStrategy pricingStrategy;

        // Constructor injection — strategy can be set at creation time
        public ShoppingCart(PricingStrategy pricingStrategy) {
            this.pricingStrategy = pricingStrategy;
        }

        // Strategy can also be CHANGED at runtime!
        public void setPricingStrategy(PricingStrategy pricingStrategy) {
            this.pricingStrategy = pricingStrategy;
        }

        public String checkout(double itemPrice, int quantity) {
            double total = pricingStrategy.calculatePrice(itemPrice, quantity);
            return String.format("Strategy: %s | Price: $%.2f × %d = $%.2f | %s",
                    pricingStrategy.getName(), itemPrice, quantity, total,
                    pricingStrategy.getDescription());
        }
    }

    /**
     * Demo method — same cart, different strategies produce different prices
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Strategy Pattern: Pricing Calculator ===\n\n");

        double itemPrice = 100.0;
        int quantity = 15;
        sb.append(String.format("Item price: $%.2f | Quantity: %d\n\n", itemPrice, quantity));

        // Create cart with REGULAR pricing
        ShoppingCart cart = new ShoppingCart(new RegularPricing());
        sb.append(cart.checkout(itemPrice, quantity)).append("\n");

        // SWITCH strategy at runtime to PREMIUM
        cart.setPricingStrategy(new PremiumPricing());
        sb.append(cart.checkout(itemPrice, quantity)).append("\n");

        // SWITCH strategy at runtime to BULK
        cart.setPricingStrategy(new BulkPricing());
        sb.append(cart.checkout(itemPrice, quantity)).append("\n");

        // SWITCH strategy at runtime to SEASONAL
        cart.setPricingStrategy(new SeasonalPricing());
        sb.append(cart.checkout(itemPrice, quantity)).append("\n");

        sb.append("\n--- Key Takeaway ---\n");
        sb.append("Same ShoppingCart, same data, but DIFFERENT strategies → DIFFERENT results.\n");
        sb.append("Adding a new strategy (e.g., COUPON) requires ZERO changes to ShoppingCart.\n");

        return sb.toString();
    }
}
