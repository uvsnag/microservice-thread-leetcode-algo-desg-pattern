package com.example.learningservice.leetcode.slidingwindow;

import java.util.*;

/**
 * ===================================================================
 * SLIDING WINDOW / TWO POINTERS (Same Direction)
 * ===================================================================
 * Pattern: Maintain a "window" [left, right] that slides over the array/string.
 *
 * Two types:
 *   1. VARIABLE-size window: expand right, shrink left when condition breaks
 *   2. FIXED-size window: slide window of size k across the array
 *
 * Template for VARIABLE window:
 *   int left = 0;
 *   for (int right = 0; right < n; right++) {
 *       // Add arr[right] to window
 *       while (window is INVALID) {
 *           // Remove arr[left] from window
 *           left++;
 *       }
 *       // Update answer
 *   }
 *
 * Time: O(n) — each element enters and leaves window at most once
 * ===================================================================
 */
public class SlidingWindowProblems {

    /**
     * ---------------------------------------------------------------
     * 1. Longest Substring Without Repeating Characters (LeetCode #3)
     * ---------------------------------------------------------------
     * Given a string s, find the length of the longest substring
     * without repeating characters.
     *
     * Example: "abcabcbb" → 3 ("abc")
     *          "bbbbb"    → 1 ("b")
     *          "pwwkew"   → 3 ("wke")
     *
     * Approach: Sliding window with HashSet
     *   - Expand right: add character to set
     *   - If duplicate found: shrink left until no duplicate
     *   - Track maximum window size
     *
     * Time: O(n), Space: O(min(n, charset))
     */
    public static int lengthOfLongestSubstring(String s) {
        // Set tracks characters currently in the window
        Set<Character> window = new HashSet<>();
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            // SHRINK: if current char already in window, remove from left
            while (window.contains(c)) {
                window.remove(s.charAt(left));
                left++;
            }

            // EXPAND: add current char to window
            window.add(c);

            // UPDATE: track the maximum window size
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    /**
     * ---------------------------------------------------------------
     * 2. Longest Repeating Character Replacement (LeetCode #424)
     * ---------------------------------------------------------------
     * Given string s and int k, you can replace at most k characters.
     * Find the length of the longest substring with all same characters.
     *
     * Example: s="AABABBA", k=1 → 4 ("AABA" → replace B → "AAAA")
     *
     * KEY INSIGHT:
     *   Window is valid when: windowSize - maxFreqChar <= k
     *   (we need to replace at most k chars to make all same)
     *
     * Time: O(n), Space: O(26) = O(1)
     */
    public static int characterReplacement(String s, int k) {
        int[] count = new int[26]; // Count of each character in window
        int left = 0;
        int maxFreq = 0; // Frequency of the most common char in window
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            // Add right char to window
            count[s.charAt(right) - 'A']++;

            // Update the max frequency character count
            maxFreq = Math.max(maxFreq, count[s.charAt(right) - 'A']);

            // WINDOW SIZE - MAX_FREQ = characters we need to replace
            // If > k, we need to shrink the window
            int windowSize = right - left + 1;
            if (windowSize - maxFreq > k) {
                count[s.charAt(left) - 'A']--; // Remove left char
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    /**
     * ---------------------------------------------------------------
     * 3. Minimum Window Substring (LeetCode #76) — HARD
     * ---------------------------------------------------------------
     * Given strings s and t, find the minimum window in s that
     * contains ALL characters of t (including duplicates).
     *
     * Example: s="ADOBECODEBANC", t="ABC" → "BANC"
     *
     * Approach:
     *   1. Count characters needed from t (need map)
     *   2. Expand right to include characters
     *   3. When all chars satisfied, shrink left to find minimum
     *
     * Time: O(n), Space: O(charset)
     */
    public static String minWindow(String s, String t) {
        if (s.length() < t.length()) return "";

        // Count characters we NEED
        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) {
            need.merge(c, 1, Integer::sum);
        }

        // Track how many unique chars are fully satisfied
        int required = need.size(); // Number of unique chars in t
        int formed = 0;             // Chars currently satisfied in window

        // Window character counts
        Map<Character, Integer> windowCounts = new HashMap<>();

        int left = 0;
        int minLen = Integer.MAX_VALUE;
        int minLeft = 0;

        for (int right = 0; right < s.length(); right++) {
            // EXPAND: add right char
            char c = s.charAt(right);
            windowCounts.merge(c, 1, Integer::sum);

            // Check if this char's frequency matches what we need
            if (need.containsKey(c) && windowCounts.get(c).intValue() == need.get(c).intValue()) {
                formed++;
            }

            // SHRINK: try to minimize window while all chars are satisfied
            while (formed == required) {
                // Update minimum window
                int windowSize = right - left + 1;
                if (windowSize < minLen) {
                    minLen = windowSize;
                    minLeft = left;
                }

                // Remove left char and shrink
                char leftChar = s.charAt(left);
                windowCounts.merge(leftChar, -1, Integer::sum);
                if (need.containsKey(leftChar) && windowCounts.get(leftChar) < need.get(leftChar)) {
                    formed--;
                }
                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minLeft, minLeft + minLen);
    }

    /**
     * ---------------------------------------------------------------
     * 4. Permutation in String (LeetCode #567)
     * ---------------------------------------------------------------
     * Given s1 and s2, return true if s2 contains a permutation of s1.
     *
     * Example: s1="ab", s2="eidbaooo" → true ("ba" is permutation of "ab")
     *
     * Approach: Fixed-size sliding window of size s1.length()
     *   Compare character frequency counts.
     *
     * Time: O(n), Space: O(1) — 26 chars
     */
    public static boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length()) return false;

        int[] s1Count = new int[26];
        int[] windowCount = new int[26];

        // Count characters in s1
        for (char c : s1.toCharArray()) s1Count[c - 'a']++;

        int windowSize = s1.length();

        for (int i = 0; i < s2.length(); i++) {
            // Add right character
            windowCount[s2.charAt(i) - 'a']++;

            // Remove leftmost character when window exceeds size
            if (i >= windowSize) {
                windowCount[s2.charAt(i - windowSize) - 'a']--;
            }

            // Compare counts — if equal, we found a permutation
            if (Arrays.equals(s1Count, windowCount)) return true;
        }
        return false;
    }

    /**
     * ---------------------------------------------------------------
     * 5. Find All Anagrams in a String (LeetCode #438)
     * ---------------------------------------------------------------
     * Given strings s and p, find all start indices of p's anagrams in s.
     *
     * Example: s="cbaebabacd", p="abc" → [0, 6]
     *
     * Approach: Same as #567 but collect ALL matching positions.
     *
     * Time: O(n), Space: O(1)
     */
    public static List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) return result;

        int[] pCount = new int[26];
        int[] windowCount = new int[26];

        for (char c : p.toCharArray()) pCount[c - 'a']++;

        int windowSize = p.length();

        for (int i = 0; i < s.length(); i++) {
            // Add right character to window
            windowCount[s.charAt(i) - 'a']++;

            // Remove left character when window exceeds size
            if (i >= windowSize) {
                windowCount[s.charAt(i - windowSize) - 'a']--;
            }

            // If window matches, record start index
            if (Arrays.equals(pCount, windowCount)) {
                result.add(i - windowSize + 1);
            }
        }
        return result;
    }

    /**
     * ---------------------------------------------------------------
     * 6. Subarray Product Less Than K (LeetCode #713)
     * ---------------------------------------------------------------
     * Given array of positive integers and k, count subarrays where
     * product of all elements is strictly less than k.
     *
     * Example: nums=[10,5,2,6], k=100 → 8
     *
     * Approach: Sliding window with running product
     *   Each position right adds (right - left + 1) new valid subarrays
     *
     * Time: O(n), Space: O(1)
     */
    public static int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) return 0;

        int product = 1;
        int left = 0;
        int count = 0;

        for (int right = 0; right < nums.length; right++) {
            product *= nums[right]; // Expand window

            // Shrink until product < k
            while (product >= k) {
                product /= nums[left];
                left++;
            }

            // KEY INSIGHT: For each right position, new valid subarrays are:
            // [left..right], [left+1..right], ..., [right..right]
            // That's (right - left + 1) subarrays
            count += right - left + 1;
        }
        return count;
    }

    /**
     * ---------------------------------------------------------------
     * 7. Maximum Average Subarray I (LeetCode #643)
     * ---------------------------------------------------------------
     * Given array nums and integer k, find the contiguous subarray of
     * length k with maximum average value.
     *
     * Example: nums=[1,12,-5,-6,50,3], k=4 → 12.75 ([12,-5,-6,50])
     *
     * Approach: FIXED-size sliding window
     *   Maintain running sum, slide by adding right and removing left.
     *
     * Time: O(n), Space: O(1)
     */
    public static double findMaxAverage(int[] nums, int k) {
        // Compute sum of first window
        double windowSum = 0;
        for (int i = 0; i < k; i++) windowSum += nums[i];

        double maxSum = windowSum;

        // Slide the window: add right element, remove left element
        for (int i = k; i < nums.length; i++) {
            windowSum += nums[i] - nums[i - k]; // Add new right, remove old left
            maxSum = Math.max(maxSum, windowSum);
        }

        return maxSum / k;
    }

    /**
     * ---------------------------------------------------------------
     * 8. Longest Subarray with Sum K (Variant of LeetCode #325)
     * ---------------------------------------------------------------
     * Given an array of integers and target sum k, find the length
     * of the longest subarray that sums to k.
     *
     * Example: nums=[1,-1,5,-2,3], k=3 → 4 ([1,-1,5,-2])
     *
     * Approach: Prefix sum + HashMap
     *   Store prefix sum → earliest index
     *   If prefixSum[j] - prefixSum[i] == k, then subarray [i+1..j] sums to k
     *
     * Time: O(n), Space: O(n)
     */
    public static int longestSubarrayWithSumK(int[] nums, int k) {
        // Map: prefix_sum → earliest index where this sum occurred
        Map<Integer, Integer> prefixMap = new HashMap<>();
        prefixMap.put(0, -1); // Empty prefix has sum 0 at index -1

        int prefixSum = 0;
        int maxLen = 0;

        for (int i = 0; i < nums.length; i++) {
            prefixSum += nums[i];

            // If prefixSum - k exists in map, we found a subarray summing to k
            // Subarray is from (map.get(prefixSum - k) + 1) to i
            if (prefixMap.containsKey(prefixSum - k)) {
                maxLen = Math.max(maxLen, i - prefixMap.get(prefixSum - k));
            }

            // Only store FIRST occurrence (we want longest subarray)
            prefixMap.putIfAbsent(prefixSum, i);
        }
        return maxLen;
    }

    // =====================================================
    // DEMO — run all problems with test cases
    // =====================================================
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Sliding Window / Two Pointers (Same Direction) ===\n\n");

        // Problem 1
        sb.append("--- 1. Longest Substring Without Repeating Characters ---\n");
        sb.append("Input: \"abcabcbb\"\n");
        sb.append("Output: ").append(lengthOfLongestSubstring("abcabcbb")).append(" (\"abc\")\n");
        sb.append("Input: \"bbbbb\"\n");
        sb.append("Output: ").append(lengthOfLongestSubstring("bbbbb")).append(" (\"b\")\n");
        sb.append("Input: \"pwwkew\"\n");
        sb.append("Output: ").append(lengthOfLongestSubstring("pwwkew")).append(" (\"wke\")\n\n");

        // Problem 2
        sb.append("--- 2. Longest Repeating Character Replacement ---\n");
        sb.append("Input: s=\"AABABBA\", k=1\n");
        sb.append("Output: ").append(characterReplacement("AABABBA", 1)).append("\n");
        sb.append("Input: s=\"ABAB\", k=2\n");
        sb.append("Output: ").append(characterReplacement("ABAB", 2)).append("\n\n");

        // Problem 3
        sb.append("--- 3. Minimum Window Substring (HARD) ---\n");
        sb.append("Input: s=\"ADOBECODEBANC\", t=\"ABC\"\n");
        sb.append("Output: \"").append(minWindow("ADOBECODEBANC", "ABC")).append("\"\n");
        sb.append("Input: s=\"a\", t=\"a\"\n");
        sb.append("Output: \"").append(minWindow("a", "a")).append("\"\n\n");

        // Problem 4
        sb.append("--- 4. Permutation in String ---\n");
        sb.append("Input: s1=\"ab\", s2=\"eidbaooo\"\n");
        sb.append("Output: ").append(checkInclusion("ab", "eidbaooo")).append("\n");
        sb.append("Input: s1=\"ab\", s2=\"eidboaoo\"\n");
        sb.append("Output: ").append(checkInclusion("ab", "eidboaoo")).append("\n\n");

        // Problem 5
        sb.append("--- 5. Find All Anagrams in a String ---\n");
        sb.append("Input: s=\"cbaebabacd\", p=\"abc\"\n");
        sb.append("Output: ").append(findAnagrams("cbaebabacd", "abc")).append("\n\n");

        // Problem 6
        sb.append("--- 6. Subarray Product Less Than K ---\n");
        sb.append("Input: nums=[10,5,2,6], k=100\n");
        sb.append("Output: ").append(numSubarrayProductLessThanK(new int[]{10, 5, 2, 6}, 100)).append("\n\n");

        // Problem 7
        sb.append("--- 7. Maximum Average Subarray I ---\n");
        sb.append("Input: nums=[1,12,-5,-6,50,3], k=4\n");
        sb.append("Output: ").append(findMaxAverage(new int[]{1, 12, -5, -6, 50, 3}, 4)).append("\n\n");

        // Problem 8
        sb.append("--- 8. Longest Subarray with Sum K ---\n");
        sb.append("Input: nums=[1,-1,5,-2,3], k=3\n");
        sb.append("Output: ").append(longestSubarrayWithSumK(new int[]{1, -1, 5, -2, 3}, 3)).append("\n\n");

        sb.append("--- Pattern Summary ---\n");
        sb.append("Variable window: expand right, shrink left when invalid\n");
        sb.append("Fixed window: slide by adding right, removing left\n");
        sb.append("Time: O(n) — each element enters/leaves window once\n");

        return sb.toString();
    }
}
