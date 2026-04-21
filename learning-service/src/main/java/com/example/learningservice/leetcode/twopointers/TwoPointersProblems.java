package com.example.learningservice.leetcode.twopointers;

import java.util.*;

/**
 * ===================================================================
 * TWO POINTERS (Opposite Direction)
 * ===================================================================
 * Pattern: Two pointers start at opposite ends and move toward each other.
 *
 * Template:
 *   int left = 0, right = n - 1;
 *   while (left < right) {
 *       if (some condition)  left++;
 *       else if (other cond) right--;
 *       else                 // found answer
 *   }
 *
 * Prerequisite: Usually requires SORTED array.
 * Time: O(n) for two pointers, O(n log n) if sorting needed.
 * ===================================================================
 */
public class TwoPointersProblems {

    /**
     * ---------------------------------------------------------------
     * 1. Two Sum II — Input Array Is Sorted (LeetCode #167)
     * ---------------------------------------------------------------
     * Given sorted array and target, find two numbers that sum to target.
     * Return 1-indexed positions.
     *
     * Example: numbers=[2,7,11,15], target=9 → [1,2]
     *
     * Approach:
     *   left at start, right at end.
     *   sum < target → move left right (need bigger number)
     *   sum > target → move right left (need smaller number)
     *   sum == target → found!
     *
     * Time: O(n), Space: O(1)
     */
    public static int[] twoSumSorted(int[] numbers, int target) {
        int left = 0, right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];

            if (sum == target) {
                return new int[]{left + 1, right + 1}; // 1-indexed
            } else if (sum < target) {
                left++;   // Need bigger sum → move left pointer right
            } else {
                right--;  // Need smaller sum → move right pointer left
            }
        }
        return new int[]{-1, -1}; // No solution
    }

    /**
     * ---------------------------------------------------------------
     * 2. Container With Most Water (LeetCode #11)
     * ---------------------------------------------------------------
     * Given array of heights, find two lines that form container with most water.
     * Area = min(height[left], height[right]) × (right - left)
     *
     * Example: height=[1,8,6,2,5,4,8,3,7] → 49
     *
     * KEY INSIGHT: Always move the SHORTER line inward.
     *   Moving the taller line can only decrease area (width shrinks,
     *   height is limited by the shorter line anyway).
     *
     * Time: O(n), Space: O(1)
     */
    public static int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int maxWater = 0;

        while (left < right) {
            // Area = width × min(heights)
            int width = right - left;
            int h = Math.min(height[left], height[right]);
            maxWater = Math.max(maxWater, width * h);

            // Move the SHORTER line — it's the bottleneck
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return maxWater;
    }

    /**
     * ---------------------------------------------------------------
     * 3. 3Sum (LeetCode #15)
     * ---------------------------------------------------------------
     * Find all unique triplets that sum to zero.
     *
     * Example: nums=[-1,0,1,2,-1,-4] → [[-1,-1,2],[-1,0,1]]
     *
     * Approach:
     *   1. Sort the array
     *   2. Fix one number (i), use two pointers for remaining two
     *   3. Skip duplicates to avoid duplicate triplets
     *
     * Time: O(n²), Space: O(1) excluding output
     */
    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums); // MUST sort first

        for (int i = 0; i < nums.length - 2; i++) {
            // Optimization: if nums[i] > 0, no solution possible
            if (nums[i] > 0) break;

            // Skip duplicates for first number
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            int left = i + 1, right = nums.length - 1;
            int target = -nums[i]; // We need two numbers that sum to -nums[i]

            while (left < right) {
                int sum = nums[left] + nums[right];

                if (sum == target) {
                    result.add(List.of(nums[i], nums[left], nums[right]));
                    // Skip duplicates for second and third numbers
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        return result;
    }

    /**
     * ---------------------------------------------------------------
     * 4. 4Sum (LeetCode #18)
     * ---------------------------------------------------------------
     * Find all unique quadruplets that sum to target.
     *
     * Example: nums=[1,0,-1,0,-2,2], target=0 → [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]
     *
     * Approach: Fix two numbers (i, j), use two pointers for remaining.
     * Generalization of 3Sum.
     *
     * Time: O(n³), Space: O(1) excluding output
     */
    public static List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums.length < 4) return result;
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue; // Skip duplicates

            for (int j = i + 1; j < nums.length - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) continue; // Skip duplicates

                int left = j + 1, right = nums.length - 1;

                while (left < right) {
                    // Use long to prevent integer overflow
                    long sum = (long) nums[i] + nums[j] + nums[left] + nums[right];

                    if (sum == target) {
                        result.add(List.of(nums[i], nums[j], nums[left], nums[right]));
                        while (left < right && nums[left] == nums[left + 1]) left++;
                        while (left < right && nums[right] == nums[right - 1]) right--;
                        left++;
                        right--;
                    } else if (sum < target) {
                        left++;
                    } else {
                        right--;
                    }
                }
            }
        }
        return result;
    }

    /**
     * ---------------------------------------------------------------
     * 5. Remove Duplicates from Sorted Array (LeetCode #26)
     * ---------------------------------------------------------------
     * Remove duplicates IN-PLACE. Return number of unique elements.
     *
     * Example: nums=[0,0,1,1,1,2,2,3,3,4] → 5, nums=[0,1,2,3,4,...]
     *
     * Approach: Slow/fast pointers (same direction)
     *   slow: position to place next unique element
     *   fast: scans through array
     *
     * Time: O(n), Space: O(1)
     */
    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;

        int slow = 0; // Points to last unique element

        for (int fast = 1; fast < nums.length; fast++) {
            if (nums[fast] != nums[slow]) {
                // Found a new unique element
                slow++;
                nums[slow] = nums[fast]; // Place it at slow position
            }
            // If equal, just advance fast (skip duplicate)
        }

        return slow + 1; // Number of unique elements
    }

    /**
     * ---------------------------------------------------------------
     * 6. Valid Palindrome (LeetCode #125)
     * ---------------------------------------------------------------
     * Check if string is palindrome, considering only alphanumeric chars,
     * ignoring case.
     *
     * Example: "A man, a plan, a canal: Panama" → true
     *          "race a car" → false
     *
     * Approach: Two pointers from both ends, skip non-alphanumeric.
     *
     * Time: O(n), Space: O(1)
     */
    public static boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;

        while (left < right) {
            // Skip non-alphanumeric characters
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) left++;
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) right--;

            // Compare (case-insensitive)
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    // =====================================================
    // DEMO
    // =====================================================
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Two Pointers (Opposite Direction) ===\n\n");

        // Problem 1
        sb.append("--- 1. Two Sum II (Sorted Array) ---\n");
        sb.append("Input: [2,7,11,15], target=9\n");
        sb.append("Output: ").append(Arrays.toString(twoSumSorted(new int[]{2, 7, 11, 15}, 9))).append("\n\n");

        // Problem 2
        sb.append("--- 2. Container With Most Water ---\n");
        sb.append("Input: [1,8,6,2,5,4,8,3,7]\n");
        sb.append("Output: ").append(maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7})).append("\n\n");

        // Problem 3
        sb.append("--- 3. 3Sum ---\n");
        sb.append("Input: [-1,0,1,2,-1,-4]\n");
        sb.append("Output: ").append(threeSum(new int[]{-1, 0, 1, 2, -1, -4})).append("\n\n");

        // Problem 4
        sb.append("--- 4. 4Sum ---\n");
        sb.append("Input: [1,0,-1,0,-2,2], target=0\n");
        sb.append("Output: ").append(fourSum(new int[]{1, 0, -1, 0, -2, 2}, 0)).append("\n\n");

        // Problem 5
        sb.append("--- 5. Remove Duplicates from Sorted Array ---\n");
        int[] arr = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        sb.append("Input: ").append(Arrays.toString(arr)).append("\n");
        int k = removeDuplicates(arr);
        sb.append("Output: k=").append(k).append(", nums=").append(Arrays.toString(Arrays.copyOf(arr, k))).append("\n\n");

        // Problem 6
        sb.append("--- 6. Valid Palindrome ---\n");
        sb.append("Input: \"A man, a plan, a canal: Panama\"\n");
        sb.append("Output: ").append(isPalindrome("A man, a plan, a canal: Panama")).append("\n");
        sb.append("Input: \"race a car\"\n");
        sb.append("Output: ").append(isPalindrome("race a car")).append("\n\n");

        sb.append("--- Pattern Summary ---\n");
        sb.append("Two pointers from opposite ends — great for sorted arrays\n");
        sb.append("3Sum/4Sum: fix outer elements, two pointers for inner pair\n");
        sb.append("Skip duplicates to handle repeated elements\n");

        return sb.toString();
    }
}
