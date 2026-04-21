package com.example.learningservice.algorithm.sorting;

import java.util.Arrays;

/**
 * ===================================================================
 * INSERTION SORT
 * ===================================================================
 * How it works:
 *   Builds the sorted array one element at a time.
 *   Takes each element and INSERTS it into its correct position
 *   in the already-sorted left portion (like sorting playing cards).
 *
 * Time Complexity:
 *   - Best:    O(n)     — already sorted (just compares, no shifts)
 *   - Average: O(n²)
 *   - Worst:   O(n²)    — reverse sorted
 *
 * Space: O(1) — in-place
 * Stable: YES
 *
 * When to use:
 *   - Small datasets (n < ~50) — faster than QuickSort due to low overhead
 *   - Nearly sorted data — approaches O(n)
 *   - Online algorithm — can sort data as it arrives
 *   - TimSort (Java's Arrays.sort()) uses Insertion Sort for small runs!
 * ===================================================================
 */
public class InsertionSortDemo {

    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Insertion Sort ===\n\n");

        int[] arr = {12, 11, 13, 5, 6};
        sb.append("Input: ").append(Arrays.toString(arr)).append("\n\n");

        int n = arr.length;
        int totalComparisons = 0;
        int totalShifts = 0;

        // Start from index 1 (index 0 is already "sorted" by itself)
        for (int i = 1; i < n; i++) {
            int key = arr[i];  // The element to insert
            int j = i - 1;

            sb.append(String.format("Insert %d: ", key));

            // SHIFT elements that are greater than key to the right
            // This creates space for the key at its correct position
            while (j >= 0 && arr[j] > key) {
                totalComparisons++;
                arr[j + 1] = arr[j];  // Shift right
                j--;
                totalShifts++;
            }
            if (j >= 0) totalComparisons++; // Count the comparison that ended the loop

            // Place the key at its correct position
            arr[j + 1] = key;

            sb.append(String.format("placed at index %d → %s\n", j + 1, Arrays.toString(arr)));
        }

        sb.append(String.format("\nResult: %s\n", Arrays.toString(arr)));
        sb.append(String.format("Total comparisons: %d, Total shifts: %d\n", totalComparisons, totalShifts));
        sb.append("\nComplexity: O(n²) average/worst, O(n) best\n");
        sb.append("Stable: YES | In-place: YES | Online: YES\n");
        sb.append("Fun fact: Java's Arrays.sort() uses Insertion Sort for arrays < 47 elements!\n");

        return sb.toString();
    }
}
