package com.example.learningservice.multithread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ===================================================================
 * SYNCHRONIZATION — Protecting Shared Data
 * ===================================================================
 *
 * Problem: Multiple threads accessing shared data → RACE CONDITION
 *
 * Solutions:
 *   1. synchronized keyword (method or block level)
 *   2. volatile keyword (visibility guarantee, NOT atomicity)
 *   3. wait() / notify() / notifyAll() — inter-thread communication
 *
 * synchronized:
 *   - Provides MUTUAL EXCLUSION — only one thread enters at a time
 *   - Provides VISIBILITY — changes are visible to other threads
 *   - Acquires an intrinsic lock (monitor) on the object
 *
 * volatile:
 *   - Guarantees VISIBILITY across threads (reads from main memory)
 *   - Does NOT guarantee atomicity (count++ is still unsafe!)
 *   - Use for simple flags (e.g., boolean running = true)
 *
 * wait/notify:
 *   - Must be called inside synchronized block
 *   - wait() releases the lock and suspends the thread
 *   - notify() wakes up ONE waiting thread
 *   - notifyAll() wakes up ALL waiting threads
 *   - Used for Producer-Consumer pattern
 *
 * INTERVIEW CLASSIC: Difference between synchronized and volatile?
 *   synchronized = mutual exclusion + visibility
 *   volatile = visibility only (no locking)
 * ===================================================================
 */
public class SynchronizationDemo {

    // =====================================================
    // RACE CONDITION DEMO — shows why synchronization is needed
    // =====================================================
    static class UnsafeCounter {
        private int count = 0;

        // NOT synchronized — multiple threads can increment simultaneously
        // count++ is actually: read → increment → write (3 steps, NOT atomic!)
        public void increment() { count++; }
        public int getCount() { return count; }
    }

    static class SafeCounter {
        private int count = 0;

        // synchronized — only ONE thread can execute this method at a time
        // Acquires lock on 'this' object before entering
        public synchronized void increment() { count++; }
        public synchronized int getCount() { return count; }
    }

    // =====================================================
    // SYNCHRONIZED BLOCK — finer-grained locking
    // =====================================================
    static class FineGrainedCounter {
        private int count = 0;
        private final Object lock = new Object(); // Dedicated lock object

        public void increment() {
            // Only the critical section is synchronized — better performance
            // than synchronizing the entire method
            synchronized (lock) {
                count++;
            }
        }

        public int getCount() {
            synchronized (lock) {
                return count;
            }
        }
    }

    // =====================================================
    // VOLATILE DEMO — visibility guarantee
    // =====================================================
    static class VolatileFlag {
        // Without volatile, the worker thread might NEVER see the update
        // because it caches the value in CPU register/cache
        private volatile boolean running = true;
        private final List<String> log;

        VolatileFlag(List<String> log) { this.log = log; }

        public void stop() {
            running = false; // Write to main memory immediately (volatile)
        }

        public void doWork() {
            int iterations = 0;
            while (running) { // Read from main memory each time (volatile)
                iterations++;
                if (iterations > 1000) break; // Safety limit for demo
            }
            log.add(String.format("Worker stopped after %d iterations", iterations));
        }
    }

    // =====================================================
    // WAIT/NOTIFY — Producer-Consumer pattern
    // =====================================================
    static class SharedBuffer {
        private final List<Integer> buffer = new ArrayList<>();
        private final int capacity;
        private final List<String> log;

        SharedBuffer(int capacity, List<String> log) {
            this.capacity = capacity;
            this.log = log;
        }

        // Producer: add item to buffer
        public synchronized void produce(int item) throws InterruptedException {
            // WAIT while buffer is full
            while (buffer.size() == capacity) {
                log.add("[Producer] Buffer full — calling wait()...");
                wait(); // Releases lock, suspends thread
            }
            buffer.add(item);
            log.add(String.format("[Producer] Produced: %d (buffer size: %d)", item, buffer.size()));
            notifyAll(); // Wake up consumers
        }

        // Consumer: remove item from buffer
        public synchronized int consume() throws InterruptedException {
            // WAIT while buffer is empty
            while (buffer.isEmpty()) {
                log.add("[Consumer] Buffer empty — calling wait()...");
                wait(); // Releases lock, suspends thread
            }
            int item = buffer.remove(0);
            log.add(String.format("[Consumer] Consumed: %d (buffer size: %d)", item, buffer.size()));
            notifyAll(); // Wake up producers
            return item;
        }
    }

    // =====================================================
    // DEADLOCK DEMO — what NOT to do
    // =====================================================
    static String deadlockExplanation() {
        return """
                --- Deadlock Scenario ---
                Thread A: locks Resource1, then tries to lock Resource2
                Thread B: locks Resource2, then tries to lock Resource1
                → Both threads BLOCKED forever!
                
                Prevention strategies:
                1. Lock ordering — always acquire locks in the SAME order
                2. Lock timeout — tryLock() with timeout (ReentrantLock)
                3. Avoid nested locks when possible
                4. Use java.util.concurrent utilities instead
                """;
    }

    public static String runDemo() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Synchronization ===\n\n");
        List<String> log = Collections.synchronizedList(new ArrayList<>());

        // ---- Race Condition Demo ----
        sb.append("--- Race Condition Demo ---\n");
        UnsafeCounter unsafe = new UnsafeCounter();
        SafeCounter safe = new SafeCounter();

        // Create 10 threads, each incrementing 1000 times
        Thread[] unsafeThreads = new Thread[10];
        Thread[] safeThreads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            unsafeThreads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) unsafe.increment();
            });
            safeThreads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) safe.increment();
            });
        }

        for (Thread t : unsafeThreads) t.start();
        for (Thread t : safeThreads) t.start();
        for (Thread t : unsafeThreads) t.join();
        for (Thread t : safeThreads) t.join();

        sb.append(String.format("UnsafeCounter (10 threads × 1000): %d (expected 10000, likely LESS!)\n",
                unsafe.getCount()));
        sb.append(String.format("SafeCounter   (10 threads × 1000): %d (expected 10000, GUARANTEED)\n\n",
                safe.getCount()));

        // ---- Volatile Demo ----
        sb.append("--- volatile Keyword Demo ---\n");
        log.clear();
        VolatileFlag flag = new VolatileFlag(log);
        Thread worker = new Thread(flag::doWork);
        worker.start();
        Thread.sleep(1); // Let worker run briefly
        flag.stop();     // Set volatile flag to false
        worker.join();
        for (String s : log) sb.append(s).append("\n");
        sb.append("volatile ensures the flag change is visible across threads\n\n");

        // ---- Wait/Notify Producer-Consumer ----
        sb.append("--- wait()/notify() — Producer-Consumer ---\n");
        log.clear();
        SharedBuffer buffer = new SharedBuffer(3, log);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) buffer.produce(i);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) buffer.consume();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        producer.start();
        consumer.start();
        producer.join(2000);
        consumer.join(2000);
        for (String s : log) sb.append(s).append("\n");
        sb.append("\n");

        // ---- Deadlock Explanation ----
        sb.append(deadlockExplanation());

        sb.append("\n--- Key Points ---\n");
        sb.append("• synchronized = mutual exclusion + visibility (uses intrinsic lock)\n");
        sb.append("• volatile = visibility only (no locking, no atomicity)\n");
        sb.append("• wait()/notify() must be inside synchronized block\n");
        sb.append("• wait() RELEASES the lock; sleep() does NOT release the lock\n");
        sb.append("• Always use while loop (not if) with wait() — spurious wakeups!\n");

        return sb.toString();
    }
}
