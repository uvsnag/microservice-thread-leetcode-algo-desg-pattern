package com.example.learningservice.pattern.decorator;

/**
 * ===================================================================
 * DECORATOR PATTERN
 * ===================================================================
 * Intent: Attach additional responsibilities to an object dynamically.
 *         Decorators provide a flexible alternative to subclassing for
 *         extending functionality.
 *
 * When to use:
 *   - Java I/O streams: BufferedInputStream(FileInputStream(new File()))
 *   - Adding logging, caching, encryption to existing services
 *   - Spring's @Transactional, @Cacheable are decorator-like (AOP)
 *   - Middleware in web frameworks (Express.js, Django)
 *
 * Key points for interview:
 *   - Wraps existing objects, adding behavior before/after
 *   - Can stack multiple decorators (like Russian dolls)
 *   - Follows Open/Closed Principle
 *   - Decorator HAS-A component AND IS-A component (same interface)
 *
 * This demo: Coffee shop — base coffee + add-ons (milk, sugar, whipped cream)
 * ===================================================================
 */
public class DecoratorDemo {

    // =====================================================
    // STEP 1: The Component interface
    // =====================================================
    public interface Coffee {
        double getCost();
        String getDescription();
    }

    // =====================================================
    // STEP 2: Concrete Component (base object)
    // =====================================================

    /** Simple black coffee — the base */
    public static class SimpleCoffee implements Coffee {
        @Override
        public double getCost() { return 2.00; }

        @Override
        public String getDescription() { return "Simple Coffee"; }
    }

    /** Espresso — another base option */
    public static class Espresso implements Coffee {
        @Override
        public double getCost() { return 3.00; }

        @Override
        public String getDescription() { return "Espresso"; }
    }

    // =====================================================
    // STEP 3: Abstract Decorator (implements AND wraps Coffee)
    // =====================================================
    // KEY INSIGHT: CoffeeDecorator IS a Coffee AND HAS a Coffee
    // This allows stacking: new Milk(new Sugar(new SimpleCoffee()))
    public static abstract class CoffeeDecorator implements Coffee {
        protected final Coffee decoratedCoffee; // The wrapped coffee

        public CoffeeDecorator(Coffee coffee) {
            this.decoratedCoffee = coffee;
        }

        @Override
        public double getCost() {
            return decoratedCoffee.getCost(); // Delegate to wrapped object
        }

        @Override
        public String getDescription() {
            return decoratedCoffee.getDescription(); // Delegate to wrapped object
        }
    }

    // =====================================================
    // STEP 4: Concrete Decorators — each adds behavior
    // =====================================================

    /** Adds milk — +$0.50 */
    public static class MilkDecorator extends CoffeeDecorator {
        public MilkDecorator(Coffee coffee) { super(coffee); }

        @Override
        public double getCost() {
            return super.getCost() + 0.50; // Add milk cost to wrapped coffee
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " + Milk"; // Append to description
        }
    }

    /** Adds sugar — +$0.25 */
    public static class SugarDecorator extends CoffeeDecorator {
        public SugarDecorator(Coffee coffee) { super(coffee); }

        @Override
        public double getCost() {
            return super.getCost() + 0.25;
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " + Sugar";
        }
    }

    /** Adds whipped cream — +$0.75 */
    public static class WhippedCreamDecorator extends CoffeeDecorator {
        public WhippedCreamDecorator(Coffee coffee) { super(coffee); }

        @Override
        public double getCost() {
            return super.getCost() + 0.75;
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " + Whipped Cream";
        }
    }

    /** Adds vanilla syrup — +$0.60 */
    public static class VanillaDecorator extends CoffeeDecorator {
        public VanillaDecorator(Coffee coffee) { super(coffee); }

        @Override
        public double getCost() {
            return super.getCost() + 0.60;
        }

        @Override
        public String getDescription() {
            return super.getDescription() + " + Vanilla";
        }
    }

    /**
     * Demo showing how decorators stack
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Decorator Pattern: Coffee Shop ===\n\n");

        // 1. Plain coffee
        Coffee coffee1 = new SimpleCoffee();
        sb.append(formatOrder(coffee1));

        // 2. Coffee + Milk
        Coffee coffee2 = new MilkDecorator(new SimpleCoffee());
        sb.append(formatOrder(coffee2));

        // 3. Coffee + Milk + Sugar (stacking decorators!)
        Coffee coffee3 = new SugarDecorator(new MilkDecorator(new SimpleCoffee()));
        sb.append(formatOrder(coffee3));

        // 4. Espresso + Everything (maximum stacking)
        Coffee coffee4 = new VanillaDecorator(
                new WhippedCreamDecorator(
                        new SugarDecorator(
                                new MilkDecorator(
                                        new Espresso()))));
        sb.append(formatOrder(coffee4));

        // 5. Double milk espresso
        Coffee coffee5 = new MilkDecorator(new MilkDecorator(new Espresso()));
        sb.append(formatOrder(coffee5));

        sb.append("--- Key Takeaway ---\n");
        sb.append("Each decorator wraps the previous one, adding behavior.\n");
        sb.append("Like Java I/O: new BufferedReader(new InputStreamReader(new FileInputStream(f)))\n");
        sb.append("Spring AOP (@Transactional, @Cacheable) works similarly — wrapping methods.\n");

        return sb.toString();
    }

    private static String formatOrder(Coffee coffee) {
        return String.format("Order: %-50s → $%.2f\n", coffee.getDescription(), coffee.getCost());
    }
}
