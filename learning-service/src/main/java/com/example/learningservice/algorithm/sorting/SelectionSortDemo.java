package com.example.learningservice.algorithm.sorting;

import java.util.Arrays;

/**
 * ===================================================================
 * SELECTION SORT
 * ===================================================================
 * How it works:
 *   Divides array into sorted (left) and unsorted (right) portions.
 *   Repeatedly SELECTS the minimum element from unsorted portion
 *   and swaps it with the first unsorted element.
 *
 * Time Complexity:
 *   - Best:    O(n²)  — always scans entire unsorted portion
 *   - Average: O(n²)
 *   - Worst:   O(n²)
 *
 * Space: O(1) — in-place
 * Stable: NO — swapping can change relative order of equal elements
 *
 * Advantage over Bubble Sort:
 *   Performs at most O(n) swaps (one per pass), while Bubble Sort
 *   may do O(n²) swaps. Good when memory writes are expensive.
 * ===================================================================
 */
public class SelectionSortDemo {

    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Selection Sort ===\n\n");

        int[] arr = {64, 25, 12, 22, 11};
        sb.append("Input: ").append(Arrays.toString(arr)).append("\n\n");

        int n = arr.length;
        int totalComparisons = 0;
        int totalSwaps = 0;

        for (int i = 0; i < n - 1; i++) {
            // STEP 1: Find the minimum element in unsorted portion [i..n-1]
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                totalComparisons++;
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;  // Track index of minimum
                }
            }

            // STEP 2: Swap minimum with first unsorted element
            if (minIdx != i) {
                int temp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = temp;
                totalSwaps++;
                sb.append(String.format("Pass %d: Found min=%d at index %d, swapped with index %d → %s\n",
                        i + 1, arr[i], minIdx, i, Arrays.toString(arr)));
            } else {
                sb.append(String.format("Pass %d: Element %d already in correct position → %s\n",
                        i + 1, arr[i], Arrays.toString(arr)));
            }
            // After this pass, arr[0..i] is sorted
        }

        sb.append(String.format("\nResult: %s\n", Arrays.toString(arr)));
        sb.append(String.format("Total comparisons: %d, Total swaps: %d (max n-1 swaps!)\n",
                totalComparisons, totalSwaps));
        sb.append("\nComplexity: O(n²) all cases\n");
        sb.append("Stable: NO | In-place: YES\n");
        sb.append("Key: Minimizes swaps — good when writes are expensive.\n");

        return sb.toString();
    }
}
