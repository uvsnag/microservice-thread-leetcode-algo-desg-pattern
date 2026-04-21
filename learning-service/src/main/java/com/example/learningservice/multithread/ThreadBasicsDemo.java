package com.example.learningservice.multithread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * ===================================================================
 * THREAD BASICS — Creating and Managing Threads in Java
 * ===================================================================
 *
 * 3 ways to create threads:
 *   1. Extend Thread class
 *   2. Implement Runnable interface (preferred — allows extending other class)
 *   3. Implement Callable<V> interface (can return value + throw exception)
 *
 * Thread Lifecycle:
 *   NEW → RUNNABLE → RUNNING → (BLOCKED/WAITING/TIMED_WAITING) → TERMINATED
 *
 * Key methods:
 *   start()   — starts the thread (calls run() internally)
 *   run()     — the task code (NEVER call directly — won't create new thread!)
 *   join()    — wait for thread to finish
 *   sleep()   — pause current thread for specified time
 *   yield()   — hint to scheduler to let other threads run
 *   interrupt() — request thread to stop
 *
 * INTERVIEW WARNING:
 *   Calling run() directly does NOT create a new thread!
 *   Always use start() — which creates a new OS thread, then calls run().
 * ===================================================================
 */
public class ThreadBasicsDemo {

    // =====================================================
    // WAY 1: Extend Thread class
    // =====================================================
    // Simple but INFLEXIBLE — Java is single-inheritance, so you can't
    // extend another class if you extend Thread
    static class MyThread extends Thread {
        private final List<String> log;

        MyThread(String name, List<String> log) {
            super(name);
            this.log = log;
        }

        @Override
        public void run() {
            // This code runs in a NEW thread
            log.add(String.format("[%s] Thread started (extends Thread)", getName()));
            for (int i = 1; i <= 3; i++) {
                log.add(String.format("[%s] Count: %d", getName(), i));
            }
            log.add(String.format("[%s] Thread finished", getName()));
        }
    }

    // =====================================================
    // WAY 2: Implement Runnable (PREFERRED)
    // =====================================================
    // More flexible — can extend other class + implement Runnable
    // Also works with ExecutorService (thread pools)
    static class MyRunnable implements Runnable {
        private final String name;
        private final List<String> log;

        MyRunnable(String name, List<String> log) {
            this.name = name;
            this.log = log;
        }

        @Override
        public void run() {
            log.add(String.format("[%s] Runnable started (implements Runnable)", name));
            for (int i = 1; i <= 3; i++) {
                log.add(String.format("[%s] Processing item %d", name, i));
            }
            log.add(String.format("[%s] Runnable finished", name));
        }
    }

    // =====================================================
    // WAY 3: Implement Callable<V> (can RETURN a value)
    // =====================================================
    // Unlike Runnable, Callable:
    //   - Returns a result via Future<V>
    //   - Can throw checked exceptions
    //   - Must be used with ExecutorService, not Thread directly
    static class MyCallable implements Callable<Integer> {
        private final String name;
        private final int n;
        private final List<String> log;

        MyCallable(String name, int n, List<String> log) {
            this.name = name;
            this.n = n;
            this.log = log;
        }

        @Override
        public Integer call() throws Exception {
            log.add(String.format("[%s] Callable started — computing sum(1..%d)", name, n));
            int sum = 0;
            for (int i = 1; i <= n; i++) sum += i;
            log.add(String.format("[%s] Callable finished — result: %d", name, sum));
            return sum; // Unlike Runnable, we can RETURN a value
        }
    }

    public static String runDemo() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Thread Basics ===\n\n");
        List<String> log = Collections.synchronizedList(new ArrayList<>());

        // ---- WAY 1: Thread class ----
        sb.append("--- Way 1: Extending Thread ---\n");
        log.clear();
        MyThread t1 = new MyThread("Thread-A", log);
        t1.start();  // ← Creates new thread! NEVER call run() directly
        t1.join();   // Wait for thread to finish
        for (String s : log) sb.append(s).append("\n");
        sb.append("\n");

        // ---- WAY 2: Runnable ----
        sb.append("--- Way 2: Implementing Runnable (Preferred) ---\n");
        log.clear();
        Thread t2 = new Thread(new MyRunnable("Runnable-B", log));
        t2.start();
        t2.join();
        for (String s : log) sb.append(s).append("\n");
        sb.append("\n");

        // ---- Lambda Runnable (Java 8+) ----
        sb.append("--- Way 2b: Lambda Runnable (Java 8+) ---\n");
        log.clear();
        Thread t3 = new Thread(() -> {
            log.add("[Lambda-C] Running with lambda expression");
            log.add("[Lambda-C] This is the simplest way to create a thread");
        });
        t3.start();
        t3.join();
        for (String s : log) sb.append(s).append("\n");
        sb.append("\n");

        // ---- WAY 3: Callable with Future ----
        sb.append("--- Way 3: Callable + Future (Returns Value) ---\n");
        log.clear();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new MyCallable("Callable-D", 100, log));

        // future.get() blocks until result is ready
        Integer result = future.get();
        executor.shutdown();
        for (String s : log) sb.append(s).append("\n");
        sb.append(String.format("Future.get() returned: %d\n\n", result));

        // ---- join() demonstration ----
        sb.append("--- Thread.join() — Waiting for Threads ---\n");
        log.clear();
        Thread worker1 = new Thread(() -> {
            log.add("[Worker-1] Starting heavy work...");
            log.add("[Worker-1] Work complete!");
        });
        Thread worker2 = new Thread(() -> {
            log.add("[Worker-2] Starting heavy work...");
            log.add("[Worker-2] Work complete!");
        });
        worker1.start();
        worker2.start();
        worker1.join(); // Main thread waits for worker1
        worker2.join(); // Main thread waits for worker2
        log.add("[Main] Both workers finished — join() unblocked");
        for (String s : log) sb.append(s).append("\n");
        sb.append("\n");

        // ---- Thread states ----
        sb.append("--- Thread Lifecycle States ---\n");
        sb.append("NEW        → Thread created but start() not called yet\n");
        sb.append("RUNNABLE   → start() called, ready to run (OS schedules it)\n");
        sb.append("RUNNING    → Currently executing on CPU\n");
        sb.append("BLOCKED    → Waiting to acquire a lock (synchronized)\n");
        sb.append("WAITING    → Waiting indefinitely (wait(), join())\n");
        sb.append("TIMED_WAIT → Waiting with timeout (sleep(), wait(ms))\n");
        sb.append("TERMINATED → run() completed or exception thrown\n\n");

        sb.append("--- Key Points ---\n");
        sb.append("• Prefer Runnable over Thread (composition over inheritance)\n");
        sb.append("• Use Callable when you need a return value\n");
        sb.append("• NEVER call run() directly — always use start()\n");
        sb.append("• join() is used to wait for thread completion\n");
        sb.append("• Thread creation is expensive — use Thread Pools in production\n");

        return sb.toString();
    }
}
