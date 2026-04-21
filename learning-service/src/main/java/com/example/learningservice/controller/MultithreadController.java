package com.example.learningservice.controller;

import com.example.learningservice.multithread.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/learning/multithread")
public class MultithreadController {

    @GetMapping
    public Map<String, String> listTopics() {
        Map<String, String> topics = new LinkedHashMap<>();
        topics.put("thread-basics", "/api/learning/multithread/thread-basics");
        topics.put("synchronization", "/api/learning/multithread/synchronization");
        topics.put("executor-service", "/api/learning/multithread/executor-service");
        topics.put("concurrency-utils", "/api/learning/multithread/concurrency-utils");
        topics.put("concurrent-collections", "/api/learning/multithread/concurrent-collections");
        return topics;
    }

    @GetMapping("/thread-basics")
    public Map<String, String> threadBasics() throws Exception {
        return Map.of("topic", "Thread Basics", "output", ThreadBasicsDemo.runDemo());
    }

    @GetMapping("/synchronization")
    public Map<String, String> synchronization() throws Exception {
        return Map.of("topic", "Synchronization", "output", SynchronizationDemo.runDemo());
    }

    @GetMapping("/executor-service")
    public Map<String, String> executorService() throws Exception {
        return Map.of("topic", "ExecutorService & CompletableFuture", "output", ExecutorServiceDemo.runDemo());
    }

    @GetMapping("/concurrency-utils")
    public Map<String, String> concurrencyUtils() throws Exception {
        return Map.of("topic", "Concurrency Utilities", "output", ConcurrencyUtilsDemo.runDemo());
    }

    @GetMapping("/concurrent-collections")
    public Map<String, String> concurrentCollections() throws Exception {
        return Map.of("topic", "Concurrent Collections", "output", ConcurrentCollectionsDemo.runDemo());
    }
}
