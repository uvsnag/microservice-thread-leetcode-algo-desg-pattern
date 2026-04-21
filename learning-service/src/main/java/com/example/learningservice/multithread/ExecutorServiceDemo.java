package com.example.learningservice.multithread;

import java.util.*;
import java.util.concurrent.*;

/**
 * ===================================================================
 * EXECUTOR SERVICE & COMPLETABLE FUTURE
 * ===================================================================
 *
 * Why Thread Pools?
 *   Creating threads is EXPENSIVE (OS-level operation).
 *   Thread pools REUSE threads, limiting resource consumption.
 *
 * ExecutorService types:
 *   - newFixedThreadPool(n)     — exactly n threads (most common)
 *   - newCachedThreadPool()     — creates threads as needed, reuses idle ones
 *   - newSingleThreadExecutor() — single thread, tasks execute sequentially
 *   - newScheduledThreadPool(n) — for delayed/periodic tasks
 *   - newVirtualThreadPerTaskExecutor() — Java 21+ virtual threads
 *
 * CompletableFuture (Java 8+):
 *   - Non-blocking asynchronous programming
 *   - Chainable: thenApply → thenCombine → thenAccept
 *   - Error handling: exceptionally(), handle()
 *   - Combines multiple futures: allOf(), anyOf()
 *
 * INTERVIEW: "How does Spring @Async work?"
 *   → Uses a TaskExecutor (thread pool) behind the scenes.
 *   → The method runs in a separate thread from the pool.
 * ===================================================================
 */
public class ExecutorServiceDemo {

    public static String runDemo() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ExecutorService & CompletableFuture ===\n\n");
        List<String> log = Collections.synchronizedList(new ArrayList<>());

        // ---- Fixed Thread Pool ----
        sb.append("--- Fixed Thread Pool (3 threads, 6 tasks) ---\n");
        log.clear();
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);

        List<Future<String>> futures = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            futures.add(fixedPool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                log.add(String.format("Task-%d running on %s", taskId, threadName));
                return String.format("Result-%d", taskId);
            }));
        }

        // Collect results
        for (Future<String> f : futures) {
            f.get(); // Wait for completion
        }
        fixedPool.shutdown();
        fixedPool.awaitTermination(5, TimeUnit.SECONDS);

        for (String s : log) sb.append(s).append("\n");
        sb.append("Notice: 3 threads handle 6 tasks — threads are REUSED\n\n");

        // ---- submit() vs execute() ----
        sb.append("--- submit() vs execute() ---\n");
        sb.append("execute(Runnable) — fire-and-forget, no return value\n");
        sb.append("submit(Runnable)  — returns Future<?> (can check completion)\n");
        sb.append("submit(Callable)  — returns Future<V> (can get result)\n\n");

        // ---- invokeAll and invokeAny ----
        sb.append("--- invokeAll() and invokeAny() ---\n");
        log.clear();
        ExecutorService pool = Executors.newFixedThreadPool(3);

        List<Callable<String>> tasks = List.of(
                () -> { Thread.sleep(50); return "Task-A (50ms)"; },
                () -> { Thread.sleep(10); return "Task-B (10ms)"; },
                () -> { Thread.sleep(30); return "Task-C (30ms)"; }
        );

        // invokeAll — waits for ALL tasks to complete
        List<Future<String>> allResults = pool.invokeAll(tasks);
        sb.append("invokeAll() results: ");
        for (Future<String> f : allResults) sb.append(f.get()).append(", ");
        sb.append("\n");

        // invokeAny — returns result of FASTEST task
        String fastest = pool.invokeAny(tasks);
        sb.append("invokeAny() result (fastest): ").append(fastest).append("\n\n");
        pool.shutdown();

        // ---- CompletableFuture ----
        sb.append("--- CompletableFuture (Async Chaining) ---\n");

        // Basic: supplyAsync → thenApply → thenAccept
        String result1 = CompletableFuture
                .supplyAsync(() -> "Hello")              // Run async, return value
                .thenApply(s -> s + " World")             // Transform result (map)
                .thenApply(String::toUpperCase)            // Chain another transform
                .get();                                    // Block and get result
        sb.append("Chain: supplyAsync→thenApply→thenApply = ").append(result1).append("\n");

        // Combining two independent futures
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Price: $100");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Discount: 20%");

        String combined = future1
                .thenCombine(future2, (price, discount) -> price + " | " + discount)
                .get();
        sb.append("thenCombine (two futures): ").append(combined).append("\n");

        // allOf — wait for ALL futures
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Service-A OK");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "Service-B OK");
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "Service-C OK");

        CompletableFuture.allOf(f1, f2, f3).get(); // Wait for all
        sb.append("allOf: ").append(f1.get()).append(", ")
                .append(f2.get()).append(", ").append(f3.get()).append("\n");

        // anyOf — returns first completed
        CompletableFuture<Object> anyResult = CompletableFuture.anyOf(
                CompletableFuture.supplyAsync(() -> { sleep(50); return "Slow"; }),
                CompletableFuture.supplyAsync(() -> "Fast"),
                CompletableFuture.supplyAsync(() -> { sleep(30); return "Medium"; })
        );
        sb.append("anyOf (first completed): ").append(anyResult.get()).append("\n");

        // Error handling
        String errorResult = CompletableFuture
                .supplyAsync(() -> {
                    if (true) throw new RuntimeException("DB connection failed!");
                    return "data";
                })
                .exceptionally(ex -> "Fallback: " + ex.getMessage())
                .get();
        sb.append("exceptionally (error handling): ").append(errorResult).append("\n\n");

        // ---- Shutdown best practices ----
        sb.append("--- Shutdown Best Practices ---\n");
        sb.append("executor.shutdown()          — stop accepting, finish running tasks\n");
        sb.append("executor.shutdownNow()       — interrupt running tasks\n");
        sb.append("executor.awaitTermination()  — block until all tasks finish\n\n");

        sb.append("--- Key Points ---\n");
        sb.append("• Always use thread pools in production — never create threads manually\n");
        sb.append("• FixedThreadPool for CPU-bound, CachedThreadPool for I/O-bound\n");
        sb.append("• CompletableFuture enables reactive-style async programming\n");
        sb.append("• Spring @Async = method runs on TaskExecutor thread pool\n");
        sb.append("• Always call shutdown() to prevent thread leaks\n");

        return sb.toString();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
