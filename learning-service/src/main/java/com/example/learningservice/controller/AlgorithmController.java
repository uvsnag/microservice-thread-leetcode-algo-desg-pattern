package com.example.learningservice.controller;

import com.example.learningservice.algorithm.searching.BinarySearchDemo;
import com.example.learningservice.algorithm.sorting.*;
import com.example.learningservice.algorithm.graph.BfsDemo;
import com.example.learningservice.algorithm.graph.DfsDemo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/learning/algorithms")
public class AlgorithmController {

    @GetMapping
    public Map<String, Object> listAlgorithms() {
        Map<String, Object> algorithms = new LinkedHashMap<>();

        Map<String, String> sorting = new LinkedHashMap<>();
        sorting.put("bubble-sort", "/api/learning/algorithms/sorting/bubble");
        sorting.put("selection-sort", "/api/learning/algorithms/sorting/selection");
        sorting.put("insertion-sort", "/api/learning/algorithms/sorting/insertion");
        sorting.put("merge-sort", "/api/learning/algorithms/sorting/merge");
        sorting.put("quick-sort", "/api/learning/algorithms/sorting/quick");
        algorithms.put("sorting", sorting);

        Map<String, String> searching = new LinkedHashMap<>();
        searching.put("binary-search", "/api/learning/algorithms/searching/binary");
        algorithms.put("searching", searching);

        Map<String, String> graph = new LinkedHashMap<>();
        graph.put("bfs", "/api/learning/algorithms/graph/bfs");
        graph.put("dfs", "/api/learning/algorithms/graph/dfs");
        algorithms.put("graph", graph);

        return algorithms;
    }

    // --- Sorting Algorithms ---

    @GetMapping("/sorting/bubble")
    public Map<String, String> bubbleSort() {
        return Map.of("algorithm", "Bubble Sort", "output", BubbleSortDemo.runDemo());
    }

    @GetMapping("/sorting/selection")
    public Map<String, String> selectionSort() {
        return Map.of("algorithm", "Selection Sort", "output", SelectionSortDemo.runDemo());
    }

    @GetMapping("/sorting/insertion")
    public Map<String, String> insertionSort() {
        return Map.of("algorithm", "Insertion Sort", "output", InsertionSortDemo.runDemo());
    }

    @GetMapping("/sorting/merge")
    public Map<String, String> mergeSort() {
        return Map.of("algorithm", "Merge Sort", "output", MergeSortDemo.runDemo());
    }

    @GetMapping("/sorting/quick")
    public Map<String, String> quickSort() {
        return Map.of("algorithm", "Quick Sort", "output", QuickSortDemo.runDemo());
    }

    // --- Searching Algorithms ---

    @GetMapping("/searching/binary")
    public Map<String, String> binarySearch() {
        return Map.of("algorithm", "Binary Search", "output", BinarySearchDemo.runDemo());
    }

    // --- Graph Algorithms ---

    @GetMapping("/graph/bfs")
    public Map<String, String> bfs() {
        return Map.of("algorithm", "BFS (Breadth-First Search)", "output", BfsDemo.runDemo());
    }

    @GetMapping("/graph/dfs")
    public Map<String, String> dfs() {
        return Map.of("algorithm", "DFS (Depth-First Search)", "output", DfsDemo.runDemo());
    }
}
