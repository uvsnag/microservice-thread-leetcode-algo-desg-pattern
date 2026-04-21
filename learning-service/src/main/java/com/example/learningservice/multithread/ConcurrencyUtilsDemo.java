package com.example.learningservice.multithread;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * ===================================================================
 * CONCURRENCY UTILITIES — Locks, Atomics, Synchronizers
 * ===================================================================
 *
 * java.util.concurrent.locks:
 *   ReentrantLock     — like synchronized but with more control (tryLock, fairness)
 *   ReadWriteLock     — multiple readers OR one writer (great for read-heavy workloads)
 *   StampedLock       — optimistic read locking (Java 8+, fastest)
 *
 * Atomic classes:
 *   AtomicInteger, AtomicLong, AtomicBoolean, AtomicReference
 *   — Lock-free, thread-safe using CAS (Compare-And-Swap) hardware instruction
 *   — Much faster than synchronized for simple counters
 *
 * Synchronizers:
 *   CountDownLatch  — "wait for N events" (one-time, can't reset)
 *   CyclicBarrier   — "all threads wait at barrier, then proceed together" (reusable)
 *   Semaphore       — "allow N threads at a time" (rate limiting)
 *   Phaser          — flexible barrier for phased tasks
 *
 * INTERVIEW: "When would you use ReentrantLock over synchronized?"
 *   → When you need: tryLock(), timeout, fairness, interruptible lock, Condition objects
 * ===================================================================
 */
public class ConcurrencyUtilsDemo {

    public static String runDemo() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Concurrency Utilities ===\n\n");

        sb.append(reentrantLockDemo());
        sb.append(readWriteLockDemo());
        sb.append(atomicDemo());
        sb.append(countDownLatchDemo());
        sb.append(cyclicBarrierDemo());
        sb.append(semaphoreDemo());

        sb.append("--- Key Points ---\n");
        sb.append("• ReentrantLock: tryLock(), fairness, Condition — more flexible than synchronized\n");
        sb.append("• ReadWriteLock: concurrent reads, exclusive writes — for read-heavy data\n");
        sb.append("• AtomicInteger: lock-free CAS — fastest for simple counters\n");
        sb.append("• CountDownLatch: one-time countdown (can't reset)\n");
        sb.append("• CyclicBarrier: reusable barrier (all threads sync at a point)\n");
        sb.append("• Semaphore: limit concurrent access (connection pool, rate limiter)\n");

        return sb.toString();
    }

    // =====================================================
    // REENTRANT LOCK — advanced locking
    // =====================================================
    private static String reentrantLockDemo() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ReentrantLock ---\n");

        ReentrantLock lock = new ReentrantLock(true); // true = fair (FIFO order)
        List<String> log = Collections.synchronizedList(new ArrayList<>());

        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            // tryLock: non-blocking attempt to acquire lock
            if (lock.tryLock()) {
                try {
                    log.add(String.format("[%s] Acquired lock, doing work...", name));
                    log.add(String.format("[%s] Hold count: %d, isHeldByCurrentThread: %s",
                            name, lock.getHoldCount(), lock.isHeldByCurrentThread()));
                } finally {
                    lock.unlock(); // ALWAYS unlock in finally!
                    log.add(String.format("[%s] Released lock", name));
                }
            } else {
                log.add(String.format("[%s] Could not acquire lock — tryLock returned false", name));
            }
        };

        Thread t1 = new Thread(task, "Worker-1");
        Thread t2 = new Thread(task, "Worker-2");
        t1.start(); t1.join();
        t2.start(); t2.join();

        for (String s : log) sb.append(s).append("\n");
        sb.append("Key: always unlock in finally block; tryLock() is non-blocking\n\n");
        return sb.toString();
    }

    // =====================================================
    // READ-WRITE LOCK — concurrent reads, exclusive writes
    // =====================================================
    private static String readWriteLockDemo() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ReadWriteLock ---\n");

        ReadWriteLock rwLock = new ReentrantReadWriteLock();
        List<String> log = Collections.synchronizedList(new ArrayList<>());
        final int[] sharedData = {0};

        // Multiple readers can read simultaneously
        Runnable reader = () -> {
            rwLock.readLock().lock();
            try {
                log.add(String.format("[%s] Reading: %d", Thread.currentThread().getName(), sharedData[0]));
            } finally {
                rwLock.readLock().unlock();
            }
        };

        // Only ONE writer at a time, blocks all readers
        Runnable writer = () -> {
            rwLock.writeLock().lock();
            try {
                sharedData[0]++;
                log.add(String.format("[%s] Writing: %d", Thread.currentThread().getName(), sharedData[0]));
            } finally {
                rwLock.writeLock().unlock();
            }
        };

        Thread w = new Thread(writer, "Writer-1");
        Thread r1 = new Thread(reader, "Reader-1");
        Thread r2 = new Thread(reader, "Reader-2");
        Thread r3 = new Thread(reader, "Reader-3");

        w.start(); w.join();
        r1.start(); r2.start(); r3.start();
        r1.join(); r2.join(); r3.join();

        for (String s : log) sb.append(s).append("\n");
        sb.append("Multiple readers can read concurrently; writer blocks all\n\n");
        return sb.toString();
    }

    // =====================================================
    // ATOMIC CLASSES — lock-free thread safety via CAS
    // =====================================================
    private static String atomicDemo() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Atomic Classes (CAS — Compare And Swap) ---\n");

        AtomicInteger atomicCount = new AtomicInteger(0);
        int[] unsafeCount = {0};

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    atomicCount.incrementAndGet(); // Thread-safe, lock-free!
                    unsafeCount[0]++;               // NOT thread-safe
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        sb.append(String.format("AtomicInteger (10×1000): %d (always correct)\n", atomicCount.get()));
        sb.append(String.format("Plain int     (10×1000): %d (likely wrong!)\n", unsafeCount[0]));
        sb.append("\nUseful Atomic operations:\n");
        sb.append("  incrementAndGet()        → atomic count++\n");
        sb.append("  compareAndSet(expect,new) → CAS operation\n");
        sb.append("  getAndUpdate(func)       → atomic transform\n");
        sb.append("  accumulateAndGet()       → atomic reduce\n\n");
        return sb.toString();
    }

    // =====================================================
    // COUNTDOWN LATCH — "wait for N events"
    // =====================================================
    private static String countDownLatchDemo() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("--- CountDownLatch (Wait for N events) ---\n");
        sb.append("Use case: Main thread waits for 3 services to start\n");

        List<String> log = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(3); // Wait for 3 events

        for (int i = 1; i <= 3; i++) {
            final int serviceId = i;
            new Thread(() -> {
                log.add(String.format("Service-%d started (latch count: %d)", serviceId, latch.getCount()));
                latch.countDown(); // Decrement count
            }).start();
        }

        latch.await(2, TimeUnit.SECONDS); // Block until count reaches 0
        log.add("All services started! Latch count: " + latch.getCount());

        for (String s : log) sb.append(s).append("\n");
        sb.append("Note: CountDownLatch is ONE-TIME — cannot reset after reaching 0\n\n");
        return sb.toString();
    }

    // =====================================================
    // CYCLIC BARRIER — "all threads wait, then proceed together"
    // =====================================================
    private static String cyclicBarrierDemo() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("--- CyclicBarrier (All wait at barrier, then proceed) ---\n");
        sb.append("Use case: All players must load before game starts\n");

        List<String> log = Collections.synchronizedList(new ArrayList<>());
        CyclicBarrier barrier = new CyclicBarrier(3, () ->
                log.add(">>> BARRIER REACHED — all threads proceed!")
        );

        for (int i = 1; i <= 3; i++) {
            final int playerId = i;
            new Thread(() -> {
                try {
                    log.add(String.format("Player-%d loading...", playerId));
                    barrier.await(2, TimeUnit.SECONDS); // Wait at barrier
                    log.add(String.format("Player-%d playing!", playerId));
                } catch (Exception e) { /* timeout */ }
            }).start();
        }
        Thread.sleep(500); // Let threads complete

        for (String s : log) sb.append(s).append("\n");
        sb.append("Note: CyclicBarrier is REUSABLE (unlike CountDownLatch)\n\n");
        return sb.toString();
    }

    // =====================================================
    // SEMAPHORE — "allow N threads at a time"
    // =====================================================
    private static String semaphoreDemo() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Semaphore (Limit Concurrent Access) ---\n");
        sb.append("Use case: Connection pool with max 2 connections\n");

        List<String> log = Collections.synchronizedList(new ArrayList<>());
        Semaphore semaphore = new Semaphore(2); // Allow max 2 threads

        Thread[] threads = new Thread[5];
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            threads[i - 1] = new Thread(() -> {
                try {
                    semaphore.acquire(); // Block if no permits available
                    log.add(String.format("Task-%d acquired permit (available: %d)", taskId, semaphore.availablePermits()));
                    Thread.sleep(10);
                    semaphore.release(); // Return permit
                    log.add(String.format("Task-%d released permit", taskId));
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join(2000);

        for (String s : log) sb.append(s).append("\n");
        sb.append("Semaphore(2) = max 2 concurrent threads — like a connection pool\n\n");
        return sb.toString();
    }
}
