package com.example.learningservice.leetcode.string;

import java.util.*;

/**
 * ===================================================================
 * STRING PROBLEMS (High Frequency)
 * ===================================================================
 * Strings are the most common interview topic. Master these patterns:
 *   - Sorting/hashing for anagram detection
 *   - Expand from center for palindromes
 *   - State machine for parsing
 *   - Length-prefix encoding for serialization
 * ===================================================================
 */
public class StringProblems {

    /**
     * ---------------------------------------------------------------
     * 1. Group Anagrams (LeetCode #49)
     * ---------------------------------------------------------------
     * Group strings that are anagrams of each other.
     *
     * Example: ["eat","tea","tan","ate","nat","bat"]
     *       → [["bat"],["nat","tan"],["ate","eat","tea"]]
     *
     * Approach: Sort each string → use sorted string as HashMap key
     *   "eat" → "aet", "tea" → "aet", "ate" → "aet" → same group!
     *
     * Time: O(n × k log k) where k = max string length
     * Space: O(n × k)
     */
    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();

        for (String s : strs) {
            // Sort characters → anagrams will have the same sorted key
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            // Group by sorted key
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        return new ArrayList<>(map.values());
    }

    /**
     * ---------------------------------------------------------------
     * 2. Valid Anagram (LeetCode #242)
     * ---------------------------------------------------------------
     * Given two strings s and t, return true if t is an anagram of s.
     *
     * Example: s="anagram", t="nagaram" → true
     *          s="rat", t="car" → false
     *
     * Approach: Character frequency count
     *
     * Time: O(n), Space: O(1) — 26 chars
     */
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;

        int[] count = new int[26];

        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i) - 'a']++; // Increment for s
            count[t.charAt(i) - 'a']--; // Decrement for t
        }

        // If all counts are zero, they're anagrams
        for (int c : count) {
            if (c != 0) return false;
        }
        return true;
    }

    /**
     * ---------------------------------------------------------------
     * 3. Longest Palindromic Substring (LeetCode #5)
     * ---------------------------------------------------------------
     * Find the longest palindromic substring.
     *
     * Example: "babad" → "bab" (or "aba")
     *          "cbbd"  → "bb"
     *
     * Approach: EXPAND FROM CENTER
     *   For each position, try expanding outward.
     *   Two cases: odd length (single center) and even length (two centers).
     *
     * Why this works: A palindrome mirrors around its center.
     *   If s[i-1] == s[i+1], we can expand the palindrome.
     *
     * Time: O(n²), Space: O(1)
     */
    public static String longestPalindrome(String s) {
        if (s.length() < 2) return s;

        int start = 0, maxLen = 1;

        for (int i = 0; i < s.length(); i++) {
            // ODD length palindromes (single center): "aba"
            int len1 = expandFromCenter(s, i, i);
            // EVEN length palindromes (two centers): "abba"
            int len2 = expandFromCenter(s, i, i + 1);

            int len = Math.max(len1, len2);
            if (len > maxLen) {
                maxLen = len;
                start = i - (len - 1) / 2; // Calculate start index
            }
        }

        return s.substring(start, start + maxLen);
    }

    /**
     * Expand from center — returns length of palindrome
     * Starts with s[left..right] and expands outward while chars match
     */
    private static int expandFromCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1; // Length of palindrome
    }

    /**
     * ---------------------------------------------------------------
     * 4. Palindromic Substrings (LeetCode #647)
     * ---------------------------------------------------------------
     * Count the number of palindromic substrings.
     *
     * Example: "abc" → 3 ("a", "b", "c")
     *          "aaa" → 6 ("a","a","a","aa","aa","aaa")
     *
     * Approach: Same expand-from-center, but COUNT instead of tracking longest.
     *
     * Time: O(n²), Space: O(1)
     */
    public static int countSubstrings(String s) {
        int count = 0;

        for (int i = 0; i < s.length(); i++) {
            // Count odd-length palindromes centered at i
            count += countFromCenter(s, i, i);
            // Count even-length palindromes centered at i, i+1
            count += countFromCenter(s, i, i + 1);
        }

        return count;
    }

    private static int countFromCenter(String s, int left, int right) {
        int count = 0;
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            count++; // Each valid expansion is a palindrome
            left--;
            right++;
        }
        return count;
    }

    /**
     * ---------------------------------------------------------------
     * 5. String to Integer — atoi (LeetCode #8)
     * ---------------------------------------------------------------
     * Convert string to 32-bit signed integer.
     * Rules: skip whitespace, handle sign, read digits, clamp to int range.
     *
     * Example: "   -42"  → -42
     *          "4193 with words" → 4193
     *          "words and 987"  → 0
     *
     * Approach: Step-by-step parsing with overflow check
     *
     * Time: O(n), Space: O(1)
     */
    public static int myAtoi(String s) {
        int i = 0, n = s.length();

        // STEP 1: Skip leading whitespace
        while (i < n && s.charAt(i) == ' ') i++;
        if (i == n) return 0;

        // STEP 2: Handle sign
        int sign = 1;
        if (s.charAt(i) == '+' || s.charAt(i) == '-') {
            sign = s.charAt(i) == '-' ? -1 : 1;
            i++;
        }

        // STEP 3: Read digits and build number
        int result = 0;
        while (i < n && Character.isDigit(s.charAt(i))) {
            int digit = s.charAt(i) - '0';

            // STEP 4: Check for overflow BEFORE adding digit
            // If result > MAX/10, or result == MAX/10 and digit > 7
            if (result > Integer.MAX_VALUE / 10 ||
                    (result == Integer.MAX_VALUE / 10 && digit > 7)) {
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }

            result = result * 10 + digit;
            i++;
        }

        return result * sign;
    }

    /**
     * ---------------------------------------------------------------
     * 6. Encode and Decode Strings (LeetCode #271)
     * ---------------------------------------------------------------
     * Design an algorithm to encode a list of strings to a single string
     * and decode it back.
     *
     * Approach: Length-prefix encoding
     *   "hello" → "5#hello"
     *   ["hello","world"] → "5#hello5#world"
     *
     * This handles ANY character including '#' and digits in the string.
     *
     * Time: O(n), Space: O(n)
     */
    public static String encode(List<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            // Format: length + '#' + string
            sb.append(s.length()).append('#').append(s);
        }
        return sb.toString();
    }

    public static List<String> decode(String s) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < s.length()) {
            // Find the '#' delimiter
            int j = i;
            while (s.charAt(j) != '#') j++;

            // Parse length
            int length = Integer.parseInt(s.substring(i, j));

            // Extract string of that length
            String str = s.substring(j + 1, j + 1 + length);
            result.add(str);

            i = j + 1 + length; // Move to next encoded string
        }

        return result;
    }

    // =====================================================
    // DEMO
    // =====================================================
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== String Problems (High Frequency) ===\n\n");

        // Problem 1
        sb.append("--- 1. Group Anagrams ---\n");
        sb.append("Input: [\"eat\",\"tea\",\"tan\",\"ate\",\"nat\",\"bat\"]\n");
        sb.append("Output: ").append(groupAnagrams(
                new String[]{"eat", "tea", "tan", "ate", "nat", "bat"})).append("\n\n");

        // Problem 2
        sb.append("--- 2. Valid Anagram ---\n");
        sb.append("\"anagram\" vs \"nagaram\": ").append(isAnagram("anagram", "nagaram")).append("\n");
        sb.append("\"rat\" vs \"car\": ").append(isAnagram("rat", "car")).append("\n\n");

        // Problem 3
        sb.append("--- 3. Longest Palindromic Substring ---\n");
        sb.append("\"babad\" → \"").append(longestPalindrome("babad")).append("\"\n");
        sb.append("\"cbbd\"  → \"").append(longestPalindrome("cbbd")).append("\"\n");
        sb.append("Method: Expand from center (odd + even length)\n\n");

        // Problem 4
        sb.append("--- 4. Palindromic Substrings ---\n");
        sb.append("\"abc\" → ").append(countSubstrings("abc")).append(" palindromes\n");
        sb.append("\"aaa\" → ").append(countSubstrings("aaa")).append(" palindromes\n\n");

        // Problem 5
        sb.append("--- 5. String to Integer (atoi) ---\n");
        sb.append("\"   -42\"            → ").append(myAtoi("   -42")).append("\n");
        sb.append("\"4193 with words\"   → ").append(myAtoi("4193 with words")).append("\n");
        sb.append("\"words and 987\"     → ").append(myAtoi("words and 987")).append("\n");
        sb.append("\"91283472332\"       → ").append(myAtoi("91283472332")).append(" (clamped to MAX)\n\n");

        // Problem 6
        sb.append("--- 6. Encode and Decode Strings ---\n");
        List<String> original = List.of("hello", "world", "test#123", "");
        String encoded = encode(original);
        List<String> decoded = decode(encoded);
        sb.append("Original: ").append(original).append("\n");
        sb.append("Encoded:  \"").append(encoded).append("\"\n");
        sb.append("Decoded:  ").append(decoded).append("\n");
        sb.append("Match: ").append(original.equals(decoded)).append("\n\n");

        return sb.toString();
    }
}
