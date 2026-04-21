package com.example.learningservice.algorithm.sorting;

import java.util.Arrays;

/**
 * ===================================================================
 * BUBBLE SORT
 * ===================================================================
 * How it works:
 *   Repeatedly swaps adjacent elements if they are in wrong order.
 *   Each pass "bubbles" the largest unsorted element to its final position.
 *
 * Time Complexity:
 *   - Best:    O(n)     — already sorted (with optimization flag)
 *   - Average: O(n²)
 *   - Worst:   O(n²)    — reverse sorted
 *
 * Space: O(1) — in-place
 * Stable: YES — equal elements maintain their relative order
 *
 * When to use (interview answer):
 *   "Almost never in production. It's useful for teaching and for
 *    nearly-sorted small datasets. In interviews, it demonstrates
 *    understanding of basic comparison-based sorting."
 * ===================================================================
 */
public class BubbleSortDemo {

    /**
     * Standard bubble sort with early-termination optimization.
     * @return step-by-step trace of each pass
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Bubble Sort ===\n\n");

        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        sb.append("Input: ").append(Arrays.toString(arr)).append("\n\n");

        int n = arr.length;
        int totalComparisons = 0;
        int totalSwaps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false; // Optimization: detect if array is already sorted

            // Each pass compares adjacent elements
            // After pass i, the last i elements are already in place
            for (int j = 0; j < n - i - 1; j++) {
                totalComparisons++;

                if (arr[j] > arr[j + 1]) {
                    // SWAP — the larger element "bubbles up"
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                    totalSwaps++;
                }
            }

            sb.append(String.format("Pass %d: %s (compared %d pairs)\n",
                    i + 1, Arrays.toString(arr), n - i - 1));

            // OPTIMIZATION: If no swaps happened, array is sorted → stop early
            if (!swapped) {
                sb.append("  → No swaps needed, array is sorted! (Early termination)\n");
                break;
            }
        }

        sb.append(String.format("\nResult: %s\n", Arrays.toString(arr)));
        sb.append(String.format("Total comparisons: %d, Total swaps: %d\n", totalComparisons, totalSwaps));
        sb.append("\nComplexity: O(n²) average/worst, O(n) best (with optimization)\n");
        sb.append("Stable: YES | In-place: YES\n");

        return sb.toString();
    }
}
