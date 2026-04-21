package com.example.learningservice.algorithm.searching;

import java.util.Arrays;

/**
 * ===================================================================
 * BINARY SEARCH
 * ===================================================================
 * How it works:
 *   Requires a SORTED array. Repeatedly divides the search interval in half.
 *   1. Compare target with middle element
 *   2. If equal → found!
 *   3. If target < middle → search left half
 *   4. If target > middle → search right half
 *
 * Time Complexity: O(log n) — halves the search space each step
 * Space: O(1) iterative, O(log n) recursive
 *
 * Key interview points:
 *   - MUST be sorted first
 *   - Use (left + (right-left)/2) to avoid integer overflow
 *   - Java provides Collections.binarySearch() and Arrays.binarySearch()
 *   - Variants: find first/last occurrence, lower/upper bound
 *   - Can be used on answer space (Binary Search on Answer)
 * ===================================================================
 */
public class BinarySearchDemo {

    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Binary Search vs Linear Search ===\n\n");

        int[] arr = {2, 5, 8, 12, 16, 23, 38, 42, 56, 72, 91};
        sb.append("Sorted array: ").append(Arrays.toString(arr)).append("\n\n");

        // --- BINARY SEARCH ---
        sb.append("--- Binary Search ---\n");
        int target1 = 23;
        sb.append(binarySearch(arr, target1));

        int target2 = 50;
        sb.append(binarySearch(arr, target2));

        // --- LINEAR SEARCH (for comparison) ---
        sb.append("\n--- Linear Search (for comparison) ---\n");
        sb.append(linearSearch(arr, target1));
        sb.append(linearSearch(arr, target2));

        // --- Comparison ---
        sb.append("\n--- Performance Comparison ---\n");
        sb.append(String.format("Array size: %d elements\n", arr.length));
        sb.append(String.format("Linear Search: up to %d comparisons (O(n))\n", arr.length));
        sb.append(String.format("Binary Search: up to %d comparisons (O(log n))\n",
                (int) Math.ceil(Math.log(arr.length) / Math.log(2))));
        sb.append("\nFor 1,000,000 elements:\n");
        sb.append("  Linear: up to 1,000,000 comparisons\n");
        sb.append("  Binary: up to 20 comparisons (2^20 > 1,000,000)\n");

        return sb.toString();
    }

    /**
     * Iterative binary search with step-by-step trace
     */
    private static String binarySearch(int[] arr, int target) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\nSearching for %d:\n", target));

        int left = 0;
        int right = arr.length - 1;
        int step = 1;

        while (left <= right) {
            // KEY: Use this formula to avoid integer overflow
            // (left + right) / 2 can overflow if left + right > Integer.MAX_VALUE
            int mid = left + (right - left) / 2;

            sb.append(String.format("  Step %d: left=%d, right=%d, mid=%d, arr[mid]=%d",
                    step, left, right, mid, arr[mid]));

            if (arr[mid] == target) {
                sb.append(String.format(" → FOUND at index %d!\n", mid));
                return sb.toString();
            } else if (arr[mid] < target) {
                sb.append(" → target is LARGER, search RIGHT half\n");
                left = mid + 1;  // Discard left half
            } else {
                sb.append(" → target is SMALLER, search LEFT half\n");
                right = mid - 1; // Discard right half
            }
            step++;
        }

        sb.append(String.format("  → NOT FOUND after %d steps\n", step - 1));
        return sb.toString();
    }

    /**
     * Linear search — O(n) comparison baseline
     */
    private static String linearSearch(int[] arr, int target) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Searching for %d: ", target));

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                sb.append(String.format("FOUND at index %d after %d comparisons\n", i, i + 1));
                return sb.toString();
            }
        }
        sb.append(String.format("NOT FOUND after %d comparisons\n", arr.length));
        return sb.toString();
    }
}
