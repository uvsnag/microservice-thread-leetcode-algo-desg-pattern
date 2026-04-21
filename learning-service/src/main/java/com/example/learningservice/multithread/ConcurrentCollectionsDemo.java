package com.example.learningservice.multithread;

import java.util.*;
import java.util.concurrent.*;

/**
 * ===================================================================
 * CONCURRENT COLLECTIONS — Thread-Safe Data Structures
 * ===================================================================
 *
 * Problem:
 *   HashMap, ArrayList, etc. are NOT thread-safe.
 *   Collections.synchronizedXxx() wraps them but uses coarse-grained locking.
 *
 * Better solutions in java.util.concurrent:
 *
 *   ConcurrentHashMap    — segment-level locking, high read concurrency
 *   CopyOnWriteArrayList — copy-on-write, great for read-heavy lists
 *   BlockingQueue        — thread-safe queue with blocking put/take
 *   ConcurrentLinkedQueue — lock-free, non-blocking queue
 *
 * INTERVIEW: "HashMap vs ConcurrentHashMap?"
 *   HashMap: NOT thread-safe, allows null key/value
 *   Hashtable: thread-safe BUT slow (synchronized on entire map)
 *   ConcurrentHashMap: thread-safe, fast (segment locking), NO null keys/values
 *
 * INTERVIEW: "CopyOnWriteArrayList use case?"
 *   → Read-heavy, write-rare scenarios (event listener lists, configuration)
 *   → Writes create a new copy of the array (expensive but reads are lock-free)
 * ===================================================================
 */
public class ConcurrentCollectionsDemo {

    public static String runDemo() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Concurrent Collections ===\n\n");

        sb.append(concurrentHashMapDemo());
        sb.append(copyOnWriteDemo());
        sb.append(blockingQueueDemo());
        sb.append(threadLocalDemo());

        sb.append("--- Key Points ---\n");
        sb.append("• ConcurrentHashMap: segment-level locking, 16 default segments\n");
        sb.append("• CopyOnWriteArrayList: reads are O(1) lock-free, writes copy entire array\n");
        sb.append("• BlockingQueue: put() blocks when full, take() blocks when empty\n");
        sb.append("• ThreadLocal: each thread has its own copy (no sharing needed)\n");
        sb.append("• NEVER use HashMap in multi-threaded code — use ConcurrentHashMap\n");

        return sb.toString();
    }

    // =====================================================
    // CONCURRENT HASH MAP
    // =====================================================
    private static String concurrentHashMapDemo() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ConcurrentHashMap ---\n");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        Map<String, Integer> unsafeMap = new HashMap<>();

        // 10 threads updating both maps concurrently
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final String key = "key-" + (i % 3); // 3 unique keys
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    // ConcurrentHashMap: atomic merge operation
                    map.merge(key, 1, Integer::sum);
                    // Unsafe: HashMap is NOT thread-safe
                    unsafeMap.merge(key, 1, Integer::sum);
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        sb.append("ConcurrentHashMap results:\n");
        map.forEach((k, v) -> sb.append(String.format("  %s = %d\n", k, v)));

        sb.append("\nUseful atomic operations:\n");
        sb.append("  putIfAbsent(key, value)          — add only if key missing\n");
        sb.append("  computeIfAbsent(key, func)       — compute value if key missing\n");
        sb.append("  merge(key, value, remappingFunc)  — atomic read-modify-write\n");
        sb.append("  compute(key, func)               — atomic compute\n\n");
        return sb.toString();
    }

    // =====================================================
    // COPY ON WRITE ARRAY LIST
    // =====================================================
    private static String copyOnWriteDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- CopyOnWriteArrayList ---\n");

        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>();
        cowList.add("EventListener-1");
        cowList.add("EventListener-2");
        cowList.add("EventListener-3");

        sb.append("Initial: ").append(cowList).append("\n");

        // Safe to iterate while modifying — iterator sees snapshot
        sb.append("Iterating while modifying:\n");
        for (String item : cowList) {
            sb.append("  Reading: ").append(item).append("\n");
            if (item.equals("EventListener-2")) {
                cowList.add("EventListener-4"); // Creates a new copy!
            }
        }
        sb.append("After iteration: ").append(cowList).append("\n");
        sb.append("Note: Iterator saw the ORIGINAL snapshot (no ConcurrentModificationException)\n\n");
        return sb.toString();
    }

    // =====================================================
    // BLOCKING QUEUE — Producer-Consumer (better than wait/notify)
    // =====================================================
    private static String blockingQueueDemo() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("--- BlockingQueue (Modern Producer-Consumer) ---\n");

        List<String> log = Collections.synchronizedList(new ArrayList<>());
        // ArrayBlockingQueue: bounded, blocking
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.put(i); // BLOCKS if queue is full
                    log.add(String.format("[Producer] Put: %d (queue size: %d)", i, queue.size()));
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    int item = queue.take(); // BLOCKS if queue is empty
                    log.add(String.format("[Consumer] Took: %d (queue size: %d)", item, queue.size()));
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Consumer");

        producer.start();
        consumer.start();
        producer.join(2000);
        consumer.join(2000);

        for (String s : log) sb.append(s).append("\n");
        sb.append("\nBlockingQueue types:\n");
        sb.append("  ArrayBlockingQueue    — bounded, backed by array\n");
        sb.append("  LinkedBlockingQueue   — optionally bounded, backed by linked nodes\n");
        sb.append("  PriorityBlockingQueue — unbounded, elements ordered by priority\n");
        sb.append("  SynchronousQueue      — zero capacity, handoff between threads\n\n");
        return sb.toString();
    }

    // =====================================================
    // THREAD LOCAL — per-thread isolated storage
    // =====================================================
    private static String threadLocalDemo() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ThreadLocal (Per-Thread Storage) ---\n");

        List<String> log = Collections.synchronizedList(new ArrayList<>());
        // Each thread gets its own copy — no sharing, no synchronization needed
        ThreadLocal<String> userContext = new ThreadLocal<>();

        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            userContext.set("User-" + name); // Set value for THIS thread only
            log.add(String.format("[%s] Set ThreadLocal = %s", name, userContext.get()));
            log.add(String.format("[%s] Get ThreadLocal = %s", name, userContext.get()));
            userContext.remove(); // IMPORTANT: prevent memory leaks!
        };

        Thread t1 = new Thread(task, "Thread-A");
        Thread t2 = new Thread(task, "Thread-B");
        t1.start(); t2.start();
        t1.join(); t2.join();

        for (String s : log) sb.append(s).append("\n");
        sb.append("\nUse cases:\n");
        sb.append("  • Store user session/request context in web apps\n");
        sb.append("  • SimpleDateFormat (not thread-safe) per thread\n");
        sb.append("  • Spring's @Transactional uses ThreadLocal for connection binding\n");
        sb.append("  • ALWAYS call remove() to prevent memory leaks in thread pools!\n\n");
        return sb.toString();
    }
}
