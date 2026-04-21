package com.example.learningservice.leetcode.prefixsum;

import java.util.*;

/**
 * ===================================================================
 * PREFIX SUM
 * ===================================================================
 * Pattern: Precompute cumulative sums so that any range sum [i..j]
 *          can be answered in O(1).
 *
 *   prefix[0] = 0
 *   prefix[i] = nums[0] + nums[1] + ... + nums[i-1]
 *   sum(i..j) = prefix[j+1] - prefix[i]
 *
 * Combined with HashMap for "subarray sum equals K" problems:
 *   If prefix[j] - prefix[i] == k, then subarray [i..j-1] sums to k.
 *   Store prefix sums in a map and check if (currentPrefix - k) exists.
 *
 * Time: O(n) precompute, O(1) per query
 * ===================================================================
 */
public class PrefixSumProblems {

    /**
     * ---------------------------------------------------------------
     * 1. Subarray Sum Equals K (LeetCode #560)
     * ---------------------------------------------------------------
     * Given array nums and integer k, return the total number of
     * subarrays whose sum equals k.
     *
     * Example: nums=[1,1,1], k=2 → 2 ([1,1] at index 0-1 and 1-2)
     *          nums=[1,2,3], k=3 → 2 ([1,2] and [3])
     *
     * KEY INSIGHT: Use prefix sum + HashMap
     *   If prefixSum[j] - prefixSum[i] == k, then sum(i+1..j) == k
     *   So we look for how many times (prefixSum - k) appeared before.
     *
     * Time: O(n), Space: O(n)
     */
    public static int subarraySum(int[] nums, int k) {
        // Map: prefix_sum → count of times this sum has occurred
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1); // Empty prefix (sum = 0) occurs once

        int prefixSum = 0;
        int count = 0;

        for (int num : nums) {
            prefixSum += num;

            // If (prefixSum - k) was seen before, those are valid subarrays
            // Because: prefixSum[j] - prefixSum[i] = k → subarray [i+1..j] sums to k
            if (prefixCount.containsKey(prefixSum - k)) {
                count += prefixCount.get(prefixSum - k);
            }

            // Record current prefix sum
            prefixCount.merge(prefixSum, 1, Integer::sum);
        }
        return count;
    }

    /**
     * ---------------------------------------------------------------
     * 2. Continuous Subarray Sum (LeetCode #523)
     * ---------------------------------------------------------------
     * Given array nums and int k, return true if there is a subarray
     * of size >= 2 whose sum is a multiple of k.
     *
     * Example: nums=[23,2,4,6,7], k=6 → true ([2,4] sum=6)
     *          nums=[23,2,6,4,7], k=6 → true ([23,2,6,4,7] sum=42=7*6)
     *
     * KEY INSIGHT: Prefix sum MOD k + HashMap
     *   If prefix[j] % k == prefix[i] % k, then sum(i+1..j) is divisible by k
     *   (Because the difference is divisible by k)
     *
     * Time: O(n), Space: O(min(n, k))
     */
    public static boolean checkSubarraySum(int[] nums, int k) {
        // Map: remainder → earliest index where this remainder occurred
        Map<Integer, Integer> remainderMap = new HashMap<>();
        remainderMap.put(0, -1); // Empty prefix at index -1

        int prefixSum = 0;

        for (int i = 0; i < nums.length; i++) {
            prefixSum += nums[i];
            int remainder = prefixSum % k;

            // Handle negative remainders (Java % can return negative)
            if (remainder < 0) remainder += k;

            if (remainderMap.containsKey(remainder)) {
                // Check if subarray length >= 2
                if (i - remainderMap.get(remainder) >= 2) {
                    return true;
                }
            } else {
                // Only store FIRST occurrence (want longest/earliest)
                remainderMap.put(remainder, i);
            }
        }
        return false;
    }

    /**
     * ---------------------------------------------------------------
     * 3. Range Sum Query — Immutable (LeetCode #303)
     * ---------------------------------------------------------------
     * Given array, answer multiple range sum queries efficiently.
     *
     * Example: nums=[-2,0,3,-5,2,-1]
     *          sumRange(0,2) → 1   (-2+0+3)
     *          sumRange(2,5) → -1  (3-5+2-1)
     *          sumRange(0,5) → -3
     *
     * Approach: Precompute prefix sums
     *   sum(i..j) = prefix[j+1] - prefix[i]
     *
     * Time: O(n) precompute, O(1) per query
     */
    public static class NumArray {
        private final int[] prefix;

        // Precompute prefix sums: O(n)
        public NumArray(int[] nums) {
            prefix = new int[nums.length + 1];
            for (int i = 0; i < nums.length; i++) {
                prefix[i + 1] = prefix[i] + nums[i];
            }
        }

        // Answer range query: O(1)
        public int sumRange(int left, int right) {
            return prefix[right + 1] - prefix[left];
        }
    }

    /**
     * ---------------------------------------------------------------
     * 4. Product of Array Except Self (LeetCode #238)
     * ---------------------------------------------------------------
     * Given array nums, return array where answer[i] is the product
     * of all elements EXCEPT nums[i]. Without using division.
     *
     * Example: nums=[1,2,3,4] → [24,12,8,6]
     *
     * Approach: Prefix and suffix products
     *   answer[i] = (product of all elements left of i) × (product of all right of i)
     *   Pass 1: left to right → compute prefix products
     *   Pass 2: right to left → multiply by suffix products
     *
     * Time: O(n), Space: O(1) excluding output
     */
    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];

        // PASS 1: Compute prefix products (left to right)
        // answer[i] = product of all elements to the LEFT of i
        answer[0] = 1;
        for (int i = 1; i < n; i++) {
            answer[i] = answer[i - 1] * nums[i - 1];
        }
        // After pass 1: answer = [1, 1, 2, 6]

        // PASS 2: Multiply by suffix products (right to left)
        // Multiply each answer[i] by product of all elements to the RIGHT
        int suffixProduct = 1;
        for (int i = n - 1; i >= 0; i--) {
            answer[i] *= suffixProduct;
            suffixProduct *= nums[i];
        }
        // After pass 2: answer = [24, 12, 8, 6]

        return answer;
    }

    // =====================================================
    // DEMO
    // =====================================================
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Prefix Sum ===\n\n");

        // Problem 1
        sb.append("--- 1. Subarray Sum Equals K ---\n");
        sb.append("Input: nums=[1,1,1], k=2\n");
        sb.append("Output: ").append(subarraySum(new int[]{1, 1, 1}, 2)).append("\n");
        sb.append("Input: nums=[1,2,3], k=3\n");
        sb.append("Output: ").append(subarraySum(new int[]{1, 2, 3}, 3)).append("\n\n");

        // Problem 2
        sb.append("--- 2. Continuous Subarray Sum ---\n");
        sb.append("Input: nums=[23,2,4,6,7], k=6\n");
        sb.append("Output: ").append(checkSubarraySum(new int[]{23, 2, 4, 6, 7}, 6)).append("\n");
        sb.append("Input: nums=[23,2,6,4,7], k=13\n");
        sb.append("Output: ").append(checkSubarraySum(new int[]{23, 2, 6, 4, 7}, 13)).append("\n\n");

        // Problem 3
        sb.append("--- 3. Range Sum Query (Immutable) ---\n");
        NumArray numArray = new NumArray(new int[]{-2, 0, 3, -5, 2, -1});
        sb.append("Array: [-2, 0, 3, -5, 2, -1]\n");
        sb.append("sumRange(0, 2) = ").append(numArray.sumRange(0, 2)).append("\n");
        sb.append("sumRange(2, 5) = ").append(numArray.sumRange(2, 5)).append("\n");
        sb.append("sumRange(0, 5) = ").append(numArray.sumRange(0, 5)).append("\n\n");

        // Problem 4
        sb.append("--- 4. Product of Array Except Self ---\n");
        sb.append("Input: [1, 2, 3, 4]\n");
        sb.append("Output: ").append(Arrays.toString(productExceptSelf(new int[]{1, 2, 3, 4}))).append("\n");
        sb.append("Step-by-step:\n");
        sb.append("  Prefix products: [1, 1, 2, 6]\n");
        sb.append("  Suffix products: [24, 12, 4, 1]\n");
        sb.append("  Result = prefix × suffix: [24, 12, 8, 6]\n\n");

        sb.append("--- Pattern Summary ---\n");
        sb.append("prefix[i] = sum(nums[0..i-1])\n");
        sb.append("sum(i..j) = prefix[j+1] - prefix[i]\n");
        sb.append("For 'subarray sum = k': use HashMap to find (prefixSum - k)\n");

        return sb.toString();
    }
}
