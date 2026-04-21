package com.example.learningservice.leetcode.bonus;

import java.util.*;

/**
 * ===================================================================
 * BONUS PROBLEMS (Often Mixed In)
 * ===================================================================
 * These problems frequently appear and often combine multiple patterns.
 * ===================================================================
 */
public class BonusProblems {

    /**
     * ---------------------------------------------------------------
     * 1. Top K Frequent Elements (LeetCode #347)
     * ---------------------------------------------------------------
     * Given integer array nums and k, return the k most frequent elements.
     *
     * Example: nums=[1,1,1,2,2,3], k=2 → [1,2]
     *
     * Approach 1: HashMap + Bucket Sort (optimal)
     *   1. Count frequencies with HashMap
     *   2. Create buckets where index = frequency
     *   3. Collect from highest frequency bucket
     *
     * Time: O(n), Space: O(n) — BETTER than heap O(n log k)
     */
    public static int[] topKFrequent(int[] nums, int k) {
        // STEP 1: Count frequencies
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.merge(num, 1, Integer::sum);
        }

        // STEP 2: Bucket sort — index = frequency, value = list of numbers
        // Max frequency = nums.length (if all elements are the same)
        @SuppressWarnings("unchecked")
        List<Integer>[] buckets = new List[nums.length + 1];
        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            int freq = entry.getValue();
            if (buckets[freq] == null) buckets[freq] = new ArrayList<>();
            buckets[freq].add(entry.getKey());
        }

        // STEP 3: Collect top k from highest frequency buckets
        int[] result = new int[k];
        int idx = 0;
        for (int i = buckets.length - 1; i >= 0 && idx < k; i--) {
            if (buckets[i] != null) {
                for (int num : buckets[i]) {
                    if (idx < k) result[idx++] = num;
                }
            }
        }
        return result;
    }

    /**
     * ---------------------------------------------------------------
     * 2. Sort Colors — Dutch National Flag (LeetCode #75)
     * ---------------------------------------------------------------
     * Sort array containing only 0, 1, 2. One pass, in-place.
     *
     * Example: [2,0,2,1,1,0] → [0,0,1,1,2,2]
     *
     * Approach: THREE pointers (Dutch Flag by Dijkstra)
     *   low:  boundary of 0s region (left)
     *   mid:  current element being examined
     *   high: boundary of 2s region (right)
     *
     *   [0..low-1] = all 0s
     *   [low..mid-1] = all 1s
     *   [mid..high] = unexamined
     *   [high+1..n-1] = all 2s
     *
     * Time: O(n), Space: O(1)
     */
    public static void sortColors(int[] nums) {
        int low = 0;               // Next position for 0
        int mid = 0;               // Current element
        int high = nums.length - 1; // Next position for 2

        while (mid <= high) {
            if (nums[mid] == 0) {
                // Swap to 0s region, advance both low and mid
                swap(nums, low, mid);
                low++;
                mid++;
            } else if (nums[mid] == 1) {
                // 1 is already in correct region, just advance mid
                mid++;
            } else { // nums[mid] == 2
                // Swap to 2s region, shrink high (don't advance mid — need to check swapped value)
                swap(nums, mid, high);
                high--;
                // DON'T increment mid — the swapped element needs examination
            }
        }
    }

    /**
     * ---------------------------------------------------------------
     * 3. Move Zeroes (LeetCode #283)
     * ---------------------------------------------------------------
     * Move all zeroes to the end, maintaining relative order of non-zero elements.
     *
     * Example: [0,1,0,3,12] → [1,3,12,0,0]
     *
     * Approach: Two pointers
     *   slow: next position for non-zero element
     *   fast: scans through array
     *
     * Time: O(n), Space: O(1)
     */
    public static void moveZeroes(int[] nums) {
        int slow = 0; // Position to place next non-zero

        // Move all non-zero elements to the front
        for (int fast = 0; fast < nums.length; fast++) {
            if (nums[fast] != 0) {
                // Swap non-zero element to slow position
                swap(nums, slow, fast);
                slow++;
            }
        }
        // After this, all remaining positions are already 0
    }

    /**
     * ---------------------------------------------------------------
     * 4. Majority Element (LeetCode #169)
     * ---------------------------------------------------------------
     * Find the element that appears more than n/2 times.
     *
     * Example: [3,2,3]     → 3
     *          [2,2,1,1,1,2,2] → 2
     *
     * Approach: Boyer-Moore Voting Algorithm
     *   Maintain a candidate and a count.
     *   Same as candidate → count++
     *   Different → count--
     *   Count reaches 0 → new candidate
     *
     * WHY IT WORKS: The majority element survives all "cancellations"
     *   because it appears MORE than all others combined.
     *
     * Time: O(n), Space: O(1) — this is the magic!
     */
    public static int majorityElement(int[] nums) {
        int candidate = nums[0];
        int count = 1;

        // PHASE 1: Find candidate
        for (int i = 1; i < nums.length; i++) {
            if (count == 0) {
                candidate = nums[i]; // New candidate
                count = 1;
            } else if (nums[i] == candidate) {
                count++; // Same as candidate
            } else {
                count--; // Different — "cancel" one occurrence
            }
        }

        // PHASE 2: candidate is guaranteed to be majority (problem guarantees one exists)
        return candidate;
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    // =====================================================
    // DEMO
    // =====================================================
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Bonus Problems ===\n\n");

        // Problem 1
        sb.append("--- 1. Top K Frequent Elements ---\n");
        sb.append("Input: nums=[1,1,1,2,2,3], k=2\n");
        sb.append("Output: ").append(Arrays.toString(topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2))).append("\n");
        sb.append("Method: HashMap + Bucket Sort = O(n)\n\n");

        // Problem 2
        sb.append("--- 2. Sort Colors (Dutch National Flag) ---\n");
        int[] colors = {2, 0, 2, 1, 1, 0};
        sb.append("Input:  ").append(Arrays.toString(colors)).append("\n");
        sortColors(colors);
        sb.append("Output: ").append(Arrays.toString(colors)).append("\n");
        sb.append("3 pointers: low(0s) | mid(scan) | high(2s)\n\n");

        // Problem 3
        sb.append("--- 3. Move Zeroes ---\n");
        int[] zeros = {0, 1, 0, 3, 12};
        sb.append("Input:  ").append(Arrays.toString(zeros)).append("\n");
        moveZeroes(zeros);
        sb.append("Output: ").append(Arrays.toString(zeros)).append("\n\n");

        // Problem 4
        sb.append("--- 4. Majority Element (Boyer-Moore Voting) ---\n");
        sb.append("Input: [2,2,1,1,1,2,2]\n");
        sb.append("Output: ").append(majorityElement(new int[]{2, 2, 1, 1, 1, 2, 2})).append("\n");
        sb.append("Walkthrough:\n");
        sb.append("  2(c=1) 2(c=2) 1(c=1) 1(c=0) 1(c=1,new) 2(c=0) 2(c=1,new) → candidate=2\n");
        sb.append("  O(n) time, O(1) space — no HashMap needed!\n\n");

        return sb.toString();
    }
}
