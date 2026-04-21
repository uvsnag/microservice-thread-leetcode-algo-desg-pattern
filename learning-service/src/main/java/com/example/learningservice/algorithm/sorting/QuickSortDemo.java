package com.example.learningservice.algorithm.sorting;

import java.util.Arrays;

/**
 * ===================================================================
 * QUICK SORT
 * ===================================================================
 * How it works:
 *   Divide-and-conquer algorithm.
 *   1. Pick a PIVOT element
 *   2. PARTITION: rearrange so elements < pivot go left, > pivot go right
 *   3. Recursively sort left and right partitions
 *
 * Time Complexity:
 *   - Best:    O(n log n)  — pivot always splits evenly
 *   - Average: O(n log n)
 *   - Worst:   O(n²)       — already sorted + bad pivot (always picks min/max)
 *
 * Space: O(log n) — recursive call stack (in-place partitioning)
 * Stable: NO
 *
 * Why QuickSort is preferred in practice:
 *   - In-place (O(log n) space vs MergeSort's O(n))
 *   - Cache-friendly (sequential access pattern)
 *   - Small constant factors → faster than MergeSort for arrays
 *   - Java's Arrays.sort() for primitives uses Dual-Pivot QuickSort
 *
 * Pivot selection strategies:
 *   - First/Last element: simple but O(n²) on sorted data
 *   - Random: good average case
 *   - Median-of-three: pick median of first, middle, last
 * ===================================================================
 */
public class QuickSortDemo {

    private static int comparisons = 0;
    private static int swaps = 0;
    private static StringBuilder traceLog = new StringBuilder();

    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Quick Sort ===\n\n");

        int[] arr = {10, 80, 30, 90, 40, 50, 70};
        sb.append("Input: ").append(Arrays.toString(arr)).append("\n\n");

        // Reset counters
        comparisons = 0;
        swaps = 0;
        traceLog = new StringBuilder();

        quickSort(arr, 0, arr.length - 1);

        sb.append("--- Step-by-Step Trace ---\n");
        sb.append(traceLog);

        sb.append(String.format("\nResult: %s\n", Arrays.toString(arr)));
        sb.append(String.format("Total comparisons: %d, Total swaps: %d\n", comparisons, swaps));
        sb.append("\nComplexity: O(n log n) average, O(n²) worst\n");
        sb.append("Space: O(log n) call stack | Stable: NO | In-place: YES\n");
        sb.append("Java uses Dual-Pivot QuickSort for primitive arrays (Arrays.sort(int[]))\n");

        return sb.toString();
    }

    private static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            // STEP 1: Partition the array and get pivot's final position
            int pivotIndex = partition(arr, low, high);

            traceLog.append(String.format("After partition: %s (pivot %d at index %d)\n",
                    Arrays.toString(arr), arr[pivotIndex], pivotIndex));

            // STEP 2: Recursively sort elements before and after pivot
            quickSort(arr, low, pivotIndex - 1);   // Left of pivot
            quickSort(arr, pivotIndex + 1, high);   // Right of pivot
        }
    }

    /**
     * Lomuto partition scheme:
     * - Uses LAST element as pivot
     * - Rearranges so: [elements ≤ pivot] [pivot] [elements > pivot]
     * - Returns the final index of the pivot
     */
    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high]; // Choose last element as pivot
        traceLog.append(String.format("\nPartition [%d..%d], pivot=%d: ", low, high, pivot));

        int i = low - 1; // i tracks the boundary of "elements ≤ pivot"

        for (int j = low; j < high; j++) {
            comparisons++;
            if (arr[j] <= pivot) {
                // Element is ≤ pivot → move it to the left partition
                i++;
                swap(arr, i, j);
            }
        }

        // Place pivot at its correct position (between left and right partitions)
        swap(arr, i + 1, high);

        traceLog.append(String.format("pivot placed at index %d\n", i + 1));
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        if (i != j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
            swaps++;
        }
    }
}
