package com.example.learningservice.controller;

import com.example.learningservice.leetcode.slidingwindow.SlidingWindowProblems;
import com.example.learningservice.leetcode.twopointers.TwoPointersProblems;
import com.example.learningservice.leetcode.prefixsum.PrefixSumProblems;
import com.example.learningservice.leetcode.array.ArrayProblems;
import com.example.learningservice.leetcode.string.StringProblems;
import com.example.learningservice.leetcode.bonus.BonusProblems;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/learning/leetcode")
public class LeetCodeController {

    @GetMapping
    public Map<String, Object> listCategories() {
        Map<String, Object> categories = new LinkedHashMap<>();

        Map<String, String> slidingWindow = new LinkedHashMap<>();
        slidingWindow.put("description", "8 problems: Longest Substring, Min Window Substring, Permutation, Anagrams, etc.");
        slidingWindow.put("endpoint", "/api/learning/leetcode/sliding-window");
        categories.put("sliding-window", slidingWindow);

        Map<String, String> twoPointers = new LinkedHashMap<>();
        twoPointers.put("description", "6 problems: Two Sum II, Container With Most Water, 3Sum, 4Sum, etc.");
        twoPointers.put("endpoint", "/api/learning/leetcode/two-pointers");
        categories.put("two-pointers", twoPointers);

        Map<String, String> prefixSum = new LinkedHashMap<>();
        prefixSum.put("description", "4 problems: Subarray Sum Equals K, Range Sum Query, Product Except Self, etc.");
        prefixSum.put("endpoint", "/api/learning/leetcode/prefix-sum");
        categories.put("prefix-sum", prefixSum);

        Map<String, String> array = new LinkedHashMap<>();
        array.put("description", "6 problems: Merge Intervals, Insert Interval, Rotate Array, Spiral Matrix, etc.");
        array.put("endpoint", "/api/learning/leetcode/array");
        categories.put("array", array);

        Map<String, String> string = new LinkedHashMap<>();
        string.put("description", "6 problems: Group Anagrams, Longest Palindrome, Encode/Decode Strings, etc.");
        string.put("endpoint", "/api/learning/leetcode/string");
        categories.put("string", string);

        Map<String, String> bonus = new LinkedHashMap<>();
        bonus.put("description", "4 problems: Top K Frequent, Sort Colors, Move Zeroes, Majority Element");
        bonus.put("endpoint", "/api/learning/leetcode/bonus");
        categories.put("bonus", bonus);

        return categories;
    }

    @GetMapping("/sliding-window")
    public Map<String, String> slidingWindow() {
        return Map.of("category", "Sliding Window / Two Pointers (Same Direction)",
                "problems", "8", "output", SlidingWindowProblems.runDemo());
    }

    @GetMapping("/two-pointers")
    public Map<String, String> twoPointers() {
        return Map.of("category", "Two Pointers (Opposite Direction)",
                "problems", "6", "output", TwoPointersProblems.runDemo());
    }

    @GetMapping("/prefix-sum")
    public Map<String, String> prefixSum() {
        return Map.of("category", "Prefix Sum",
                "problems", "4", "output", PrefixSumProblems.runDemo());
    }

    @GetMapping("/array")
    public Map<String, String> array() {
        return Map.of("category", "General Array Manipulation",
                "problems", "6", "output", ArrayProblems.runDemo());
    }

    @GetMapping("/string")
    public Map<String, String> string() {
        return Map.of("category", "String Problems (High Frequency)",
                "problems", "6", "output", StringProblems.runDemo());
    }

    @GetMapping("/bonus")
    public Map<String, String> bonus() {
        return Map.of("category", "Bonus Problems",
                "problems", "4", "output", BonusProblems.runDemo());
    }
}
