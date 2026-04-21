package com.example.learningservice.leetcode.array;

import java.util.*;

/**
 * ===================================================================
 * GENERAL ARRAY MANIPULATION
 * ===================================================================
 * Classic array problems that appear frequently in interviews.
 * These test your ability to manipulate arrays efficiently.
 * ===================================================================
 */
public class ArrayProblems {

    /**
     * ---------------------------------------------------------------
     * 1. Merge Intervals (LeetCode #56)
     * ---------------------------------------------------------------
     * Given array of intervals, merge all overlapping intervals.
     *
     * Example: [[1,3],[2,6],[8,10],[15,18]] → [[1,6],[8,10],[15,18]]
     *
     * Approach:
     *   1. Sort by start time
     *   2. If current interval overlaps with previous, merge (extend end)
     *   3. Otherwise, add new interval to result
     *
     * Time: O(n log n) for sorting, Space: O(n)
     */
    public static int[][] merge(int[][] intervals) {
        if (intervals.length <= 1) return intervals;

        // Sort by start time
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        List<int[]> merged = new ArrayList<>();
        merged.add(intervals[0]);

        for (int i = 1; i < intervals.length; i++) {
            int[] last = merged.get(merged.size() - 1);
            int[] curr = intervals[i];

            if (curr[0] <= last[1]) {
                // OVERLAP: merge by extending end time
                last[1] = Math.max(last[1], curr[1]);
            } else {
                // NO OVERLAP: add as new interval
                merged.add(curr);
            }
        }

        return merged.toArray(new int[0][]);
    }

    /**
     * ---------------------------------------------------------------
     * 2. Insert Interval (LeetCode #57)
     * ---------------------------------------------------------------
     * Insert a new interval into sorted non-overlapping intervals, merge if needed.
     *
     * Example: intervals=[[1,3],[6,9]], newInterval=[2,5] → [[1,5],[6,9]]
     *
     * Approach: Three phases:
     *   1. Add intervals that come BEFORE new interval
     *   2. Merge all overlapping intervals with new interval
     *   3. Add intervals that come AFTER
     *
     * Time: O(n), Space: O(n)
     */
    public static int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;

        // Phase 1: Add all intervals that end BEFORE new interval starts
        while (i < intervals.length && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }

        // Phase 2: Merge overlapping intervals
        while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval);

        // Phase 3: Add remaining intervals
        while (i < intervals.length) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[0][]);
    }

    /**
     * ---------------------------------------------------------------
     * 3. Rotate Array (LeetCode #189)
     * ---------------------------------------------------------------
     * Rotate array to the right by k steps.
     *
     * Example: nums=[1,2,3,4,5,6,7], k=3 → [5,6,7,1,2,3,4]
     *
     * Approach: Three reverses (elegant O(1) space)
     *   1. Reverse entire array:    [7,6,5,4,3,2,1]
     *   2. Reverse first k:        [5,6,7,4,3,2,1]
     *   3. Reverse remaining n-k:  [5,6,7,1,2,3,4]
     *
     * Time: O(n), Space: O(1)
     */
    public static void rotate(int[] nums, int k) {
        int n = nums.length;
        k = k % n; // Handle k > n
        if (k == 0) return;

        reverse(nums, 0, n - 1);     // Reverse all
        reverse(nums, 0, k - 1);     // Reverse first k
        reverse(nums, k, n - 1);     // Reverse remaining
    }

    private static void reverse(int[] nums, int left, int right) {
        while (left < right) {
            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            left++;
            right--;
        }
    }

    /**
     * ---------------------------------------------------------------
     * 4. Set Matrix Zeroes (LeetCode #73)
     * ---------------------------------------------------------------
     * If element is 0, set entire row and column to 0. Do it in-place.
     *
     * Example: [[1,1,1],[1,0,1],[1,1,1]] → [[1,0,1],[0,0,0],[1,0,1]]
     *
     * Approach: Use first row/column as markers (O(1) space)
     *   1. Check if first row/column themselves contain zeros
     *   2. Use first row/col as markers for inner matrix
     *   3. Set zeros based on markers
     *   4. Handle first row/col separately
     *
     * Time: O(m×n), Space: O(1)
     */
    public static void setZeroes(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean firstRowZero = false, firstColZero = false;

        // STEP 1: Check if first row/col need zeroing
        for (int j = 0; j < n; j++) {
            if (matrix[0][j] == 0) firstRowZero = true;
        }
        for (int i = 0; i < m; i++) {
            if (matrix[i][0] == 0) firstColZero = true;
        }

        // STEP 2: Use first row/col as markers
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0; // Mark row
                    matrix[0][j] = 0; // Mark column
                }
            }
        }

        // STEP 3: Zero out cells based on markers
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }

        // STEP 4: Handle first row and column
        if (firstRowZero) {
            for (int j = 0; j < n; j++) matrix[0][j] = 0;
        }
        if (firstColZero) {
            for (int i = 0; i < m; i++) matrix[i][0] = 0;
        }
    }

    /**
     * ---------------------------------------------------------------
     * 5. Spiral Matrix (LeetCode #54)
     * ---------------------------------------------------------------
     * Return all elements in spiral order.
     *
     * Example: [[1,2,3],[4,5,6],[7,8,9]] → [1,2,3,6,9,8,7,4,5]
     *
     * Approach: Maintain 4 boundaries, shrink after each direction.
     *   top → right → bottom → left → repeat
     *
     * Time: O(m×n), Space: O(1) excluding output
     */
    public static List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix.length == 0) return result;

        int top = 0, bottom = matrix.length - 1;
        int left = 0, right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {
            // Traverse RIGHT along top row
            for (int j = left; j <= right; j++) result.add(matrix[top][j]);
            top++;

            // Traverse DOWN along right column
            for (int i = top; i <= bottom; i++) result.add(matrix[i][right]);
            right--;

            // Traverse LEFT along bottom row (if rows remain)
            if (top <= bottom) {
                for (int j = right; j >= left; j--) result.add(matrix[bottom][j]);
                bottom--;
            }

            // Traverse UP along left column (if columns remain)
            if (left <= right) {
                for (int i = bottom; i >= top; i--) result.add(matrix[i][left]);
                left++;
            }
        }
        return result;
    }

    /**
     * ---------------------------------------------------------------
     * 6. Gas Station (LeetCode #134)
     * ---------------------------------------------------------------
     * N gas stations in a circle. gas[i] = gas at station i.
     * cost[i] = gas needed to travel from i to i+1.
     * Find starting station to complete the circuit, or -1.
     *
     * Example: gas=[1,2,3,4,5], cost=[3,4,5,1,2] → 3
     *
     * KEY INSIGHTS:
     *   1. If total gas >= total cost, a solution MUST exist
     *   2. If tank goes negative at station i, start from i+1
     *      (proof: if you can't reach i+1 from any station 0..i,
     *       then none of those can be the answer)
     *
     * Time: O(n), Space: O(1)
     */
    public static int canCompleteCircuit(int[] gas, int[] cost) {
        int totalGas = 0;   // Total net gas across all stations
        int currentGas = 0; // Gas from current starting station
        int startStation = 0;

        for (int i = 0; i < gas.length; i++) {
            int net = gas[i] - cost[i];
            totalGas += net;
            currentGas += net;

            // If tank is negative, we can't start from startStation
            // Try starting from next station
            if (currentGas < 0) {
                startStation = i + 1;
                currentGas = 0;
            }
        }

        // If total gas >= total cost, solution exists and it's startStation
        return totalGas >= 0 ? startStation : -1;
    }

    // =====================================================
    // DEMO
    // =====================================================
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== General Array Manipulation ===\n\n");

        // Problem 1
        sb.append("--- 1. Merge Intervals ---\n");
        int[][] intervals = {{1, 3}, {2, 6}, {8, 10}, {15, 18}};
        sb.append("Input: [[1,3],[2,6],[8,10],[15,18]]\n");
        sb.append("Output: ").append(formatIntervals(merge(intervals))).append("\n\n");

        // Problem 2
        sb.append("--- 2. Insert Interval ---\n");
        int[][] existing = {{1, 3}, {6, 9}};
        sb.append("Input: [[1,3],[6,9]], newInterval=[2,5]\n");
        sb.append("Output: ").append(formatIntervals(insert(existing, new int[]{2, 5}))).append("\n\n");

        // Problem 3
        sb.append("--- 3. Rotate Array ---\n");
        int[] arr = {1, 2, 3, 4, 5, 6, 7};
        sb.append("Input: [1,2,3,4,5,6,7], k=3\n");
        rotate(arr, 3);
        sb.append("Output: ").append(Arrays.toString(arr)).append("\n");
        sb.append("Method: reverse all → reverse first k → reverse rest\n\n");

        // Problem 4
        sb.append("--- 4. Set Matrix Zeroes ---\n");
        int[][] matrix = {{1, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        sb.append("Input:  [[1,1,1],[1,0,1],[1,1,1]]\n");
        setZeroes(matrix);
        sb.append("Output: ").append(formatMatrix(matrix)).append("\n\n");

        // Problem 5
        sb.append("--- 5. Spiral Matrix ---\n");
        int[][] spiral = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        sb.append("Input: [[1,2,3],[4,5,6],[7,8,9]]\n");
        sb.append("Output: ").append(spiralOrder(spiral)).append("\n\n");

        // Problem 6
        sb.append("--- 6. Gas Station ---\n");
        sb.append("Input: gas=[1,2,3,4,5], cost=[3,4,5,1,2]\n");
        sb.append("Output: ").append(canCompleteCircuit(
                new int[]{1, 2, 3, 4, 5}, new int[]{3, 4, 5, 1, 2})).append("\n");
        sb.append("Starting from station 3: 4-1=3 → 5-2=6 → 1-3=4 → 2-4=2 → 3-5=0 ✓\n\n");

        return sb.toString();
    }

    private static String formatIntervals(int[][] intervals) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < intervals.length; i++) {
            sb.append(Arrays.toString(intervals[i]));
            if (i < intervals.length - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }

    private static String formatMatrix(int[][] matrix) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < matrix.length; i++) {
            sb.append(Arrays.toString(matrix[i]));
            if (i < matrix.length - 1) sb.append(",");
        }
        return sb.append("]").toString();
    }
}
