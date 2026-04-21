package com.example.learningservice.pattern.singleton;

/**
 * ===================================================================
 * SINGLETON PATTERN
 * ===================================================================
 * Intent: Ensure a class has only ONE instance and provide a global access point.
 *
 * When to use:
 *   - Database connection pools
 *   - Configuration managers
 *   - Logger instances
 *   - Thread pools
 *
 * Key points for interview:
 *   - Private constructor prevents external instantiation
 *   - Thread-safe: use double-checked locking or enum or static holder
 *   - Lazy initialization vs Eager initialization
 *   - Enum singleton is the BEST approach (prevents reflection & serialization attacks)
 *
 * This demo shows THREE ways to implement Singleton.
 * ===================================================================
 */
public class SingletonDemo {

    // =====================================================
    // APPROACH 1: Double-Checked Locking (Thread-Safe, Lazy)
    // =====================================================
    // "volatile" ensures the instance is visible to all threads immediately
    // after it's assigned (prevents instruction reordering)
    private static volatile SingletonDemo instance;

    // Private constructor — no one outside can do "new SingletonDemo()"
    private String configValue;

    private SingletonDemo() {
        this.configValue = "Initialized at " + System.currentTimeMillis();
    }

    /**
     * Double-checked locking:
     * 1st check (without lock) — fast path, avoids synchronization overhead
     * 2nd check (with lock) — ensures only one thread creates the instance
     */
    public static SingletonDemo getInstance() {
        if (instance == null) {                    // 1st check (no lock)
            synchronized (SingletonDemo.class) {   // Lock only when instance is null
                if (instance == null) {             // 2nd check (with lock)
                    instance = new SingletonDemo();
                }
            }
        }
        return instance;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String value) {
        this.configValue = value;
    }

    // =====================================================
    // APPROACH 2: Static Inner Class (Bill Pugh Singleton)
    // =====================================================
    // The inner class is NOT loaded until getInstance2() is called
    // JVM guarantees thread-safe class loading → no synchronization needed!
    public static class BillPughSingleton {
        // Holder class is loaded ONLY when BillPughSingleton.getInstance() is called
        private static class Holder {
            private static final BillPughSingleton INSTANCE = new BillPughSingleton();
        }

        private final long createdAt;

        private BillPughSingleton() {
            this.createdAt = System.currentTimeMillis();
        }

        public static BillPughSingleton getInstance() {
            return Holder.INSTANCE; // Thread-safe, lazy, no synchronization
        }

        public long getCreatedAt() {
            return createdAt;
        }
    }

    // =====================================================
    // APPROACH 3: Enum Singleton (Joshua Bloch's recommendation)
    // =====================================================
    // WHY ENUM IS BEST:
    //   - JVM guarantees single instance
    //   - Prevents reflection attacks (can't use reflection to create enum)
    //   - Handles serialization automatically (no duplicate on deserialization)
    //   - Thread-safe by default
    public enum EnumSingleton {
        INSTANCE;

        private int counter = 0;

        public int incrementAndGet() {
            return ++counter;
        }

        public int getCounter() {
            return counter;
        }
    }

    /**
     * Demo method to show all three approaches
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();

        // Approach 1: Double-Checked Locking
        SingletonDemo s1 = SingletonDemo.getInstance();
        SingletonDemo s2 = SingletonDemo.getInstance();
        sb.append("=== Double-Checked Locking ===\n");
        sb.append("Same instance? ").append(s1 == s2).append("\n"); // true
        sb.append("Config: ").append(s1.getConfigValue()).append("\n\n");

        // Approach 2: Bill Pugh
        BillPughSingleton bp1 = BillPughSingleton.getInstance();
        BillPughSingleton bp2 = BillPughSingleton.getInstance();
        sb.append("=== Bill Pugh (Static Holder) ===\n");
        sb.append("Same instance? ").append(bp1 == bp2).append("\n"); // true
        sb.append("Created at: ").append(bp1.getCreatedAt()).append("\n\n");

        // Approach 3: Enum
        EnumSingleton e1 = EnumSingleton.INSTANCE;
        EnumSingleton e2 = EnumSingleton.INSTANCE;
        sb.append("=== Enum Singleton ===\n");
        sb.append("Same instance? ").append(e1 == e2).append("\n"); // true
        sb.append("Counter: ").append(e1.incrementAndGet()).append("\n");

        return sb.toString();
    }
}
