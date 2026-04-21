package com.example.learningservice.algorithm.sorting;

import java.util.Arrays;

/**
 * ===================================================================
 * MERGE SORT
 * ===================================================================
 * How it works:
 *   Divide-and-conquer algorithm.
 *   1. DIVIDE: Split array into two halves
 *   2. CONQUER: Recursively sort each half
 *   3. MERGE: Merge two sorted halves into one sorted array
 *
 * Time Complexity:
 *   - Best:    O(n log n)
 *   - Average: O(n log n)
 *   - Worst:   O(n log n) — GUARANTEED, unlike QuickSort!
 *
 * Space: O(n) — needs temporary arrays for merging
 * Stable: YES
 *
 * When to use:
 *   - Need guaranteed O(n log n) worst-case
 *   - Linked lists (merge sort is optimal for linked lists — no random access needed)
 *   - External sorting (sorting data that doesn't fit in memory)
 *   - When stability is required
 *
 * Comparison with QuickSort:
 *   - MergeSort: guaranteed O(n log n), but uses O(n) extra space
 *   - QuickSort: O(n log n) average, O(n²) worst, but O(log n) space
 * ===================================================================
 */
public class MergeSortDemo {

    private static int comparisons = 0;
    private static int mergeOperations = 0;
    private static StringBuilder traceLog = new StringBuilder();

    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Merge Sort ===\n\n");

        int[] arr = {38, 27, 43, 3, 9, 82, 10};
        sb.append("Input: ").append(Arrays.toString(arr)).append("\n\n");

        // Reset counters
        comparisons = 0;
        mergeOperations = 0;
        traceLog = new StringBuilder();

        // Perform merge sort
        mergeSort(arr, 0, arr.length - 1, 0);

        sb.append("--- Step-by-Step Trace ---\n");
        sb.append(traceLog);

        sb.append(String.format("\nResult: %s\n", Arrays.toString(arr)));
        sb.append(String.format("Total comparisons: %d, Total merge operations: %d\n",
                comparisons, mergeOperations));
        sb.append("\nComplexity: O(n log n) ALL cases — guaranteed!\n");
        sb.append("Space: O(n) — needs temp arrays\n");
        sb.append("Stable: YES | In-place: NO\n");

        return sb.toString();
    }

    /**
     * Recursive merge sort
     * @param depth — for indentation in trace log
     */
    private static void mergeSort(int[] arr, int left, int right, int depth) {
        if (left < right) {
            // STEP 1: Find the middle point
            int mid = left + (right - left) / 2;  // Avoids integer overflow vs (left+right)/2

            String indent = "  ".repeat(depth);
            traceLog.append(String.format("%sSplit: %s → [%d..%d] and [%d..%d]\n",
                    indent, arraySlice(arr, left, right), left, mid, mid + 1, right));

            // STEP 2: Recursively sort first and second halves
            mergeSort(arr, left, mid, depth + 1);
            mergeSort(arr, mid + 1, right, depth + 1);

            // STEP 3: Merge the two sorted halves
            merge(arr, left, mid, right);
            traceLog.append(String.format("%sMerge: %s\n", indent, arraySlice(arr, left, right)));
        }
    }

    /**
     * Merges two sorted subarrays:
     *   arr[left..mid] and arr[mid+1..right]
     */
    private static void merge(int[] arr, int left, int mid, int right) {
        // Create temporary arrays for left and right halves
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] leftArr = new int[n1];
        int[] rightArr = new int[n2];

        // Copy data to temp arrays
        System.arraycopy(arr, left, leftArr, 0, n1);
        System.arraycopy(arr, mid + 1, rightArr, 0, n2);

        // Merge back into arr[left..right]
        int i = 0, j = 0;  // Pointers for leftArr and rightArr
        int k = left;       // Pointer for merged array

        while (i < n1 && j < n2) {
            comparisons++;
            if (leftArr[i] <= rightArr[j]) {
                // Take from left (≤ preserves stability)
                arr[k] = leftArr[i];
                i++;
            } else {
                // Take from right
                arr[k] = rightArr[j];
                j++;
            }
            k++;
            mergeOperations++;
        }

        // Copy remaining elements (one of the halves may have leftovers)
        while (i < n1) {
            arr[k] = leftArr[i];
            i++;
            k++;
            mergeOperations++;
        }
        while (j < n2) {
            arr[k] = rightArr[j];
            j++;
            k++;
            mergeOperations++;
        }
    }

    /** Helper: format a slice of the array for display */
    private static String arraySlice(int[] arr, int from, int to) {
        int[] slice = Arrays.copyOfRange(arr, from, to + 1);
        return Arrays.toString(slice);
    }
}
