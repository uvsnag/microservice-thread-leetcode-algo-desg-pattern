package com.example.learningservice.datastructure;

/**
 * ===================================================================
 * STACK (Last-In, First-Out — LIFO)
 * ===================================================================
 * Structure: Elements added/removed from the same end (TOP)
 *   TOP → | 30 |
 *         | 20 |
 *         | 10 |
 *         ------
 *
 * Operations (all O(1)):
 *   - push(item):  Add to top
 *   - pop():       Remove and return top
 *   - peek():      View top without removing
 *   - isEmpty():   Check if empty
 *
 * Real-world uses:
 *   - JVM Call Stack (method invocation tracking)
 *   - Undo/Redo functionality
 *   - Expression evaluation and parsing (parentheses matching)
 *   - Browser back button (navigation history)
 *   - DFS (Depth-First Search) — uses stack
 *
 * Interview classics:
 *   - Valid parentheses check
 *   - Evaluate postfix expression
 *   - Min stack (get minimum in O(1))
 * ===================================================================
 */
public class StackDemo {

    // =====================================================
    // Array-based Stack implementation
    // =====================================================
    public static class ArrayStack {
        private int[] data;
        private int top;      // Index of top element
        private int capacity;

        public ArrayStack(int capacity) {
            this.capacity = capacity;
            this.data = new int[capacity];
            this.top = -1;  // -1 means empty
        }

        /** Push element onto top — O(1) */
        public void push(int item) {
            if (top == capacity - 1) {
                throw new RuntimeException("Stack Overflow! Capacity: " + capacity);
            }
            data[++top] = item;
        }

        /** Remove and return top element — O(1) */
        public int pop() {
            if (isEmpty()) {
                throw new RuntimeException("Stack Underflow! Stack is empty.");
            }
            return data[top--];
        }

        /** View top element without removing — O(1) */
        public int peek() {
            if (isEmpty()) {
                throw new RuntimeException("Stack is empty.");
            }
            return data[top];
        }

        public boolean isEmpty() { return top == -1; }
        public int size() { return top + 1; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Stack[");
            for (int i = 0; i <= top; i++) {
                sb.append(data[i]);
                if (i < top) sb.append(", ");
            }
            sb.append("] ← TOP");
            return sb.toString();
        }
    }

    // =====================================================
    // CLASSIC INTERVIEW: Valid Parentheses Check
    // =====================================================
    /**
     * Given a string of brackets, check if they are properly nested.
     * Examples: "({[]})" → true, "([)]" → false, "(((" → false
     */
    public static boolean isValidParentheses(String s) {
        // Stack-based approach:
        // Push every opening bracket
        // For each closing bracket, check if it matches the top
        char[] stack = new char[s.length()];
        int top = -1;

        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack[++top] = c;  // Push opening bracket
            } else {
                if (top == -1) return false;  // No matching opening bracket

                char open = stack[top--];  // Pop
                // Check if brackets match
                if ((c == ')' && open != '(') ||
                    (c == '}' && open != '{') ||
                    (c == ']' && open != '[')) {
                    return false;
                }
            }
        }
        return top == -1;  // Stack should be empty if all brackets matched
    }

    /**
     * Demo of stack operations
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Stack (LIFO) ===\n\n");

        // Basic operations
        ArrayStack stack = new ArrayStack(10);
        stack.push(10);
        stack.push(20);
        stack.push(30);
        sb.append("After push 10, 20, 30: ").append(stack).append("\n");
        sb.append("Peek: ").append(stack.peek()).append("\n");

        int popped = stack.pop();
        sb.append("Pop: ").append(popped).append("\n");
        sb.append("After pop: ").append(stack).append("\n");
        sb.append("Size: ").append(stack.size()).append("\n\n");

        // Valid Parentheses
        sb.append("--- Valid Parentheses (Classic Interview) ---\n");
        String[] tests = {"({[]})", "([)]", "(((", "()", "{[]}", ""};
        for (String test : tests) {
            sb.append(String.format("  \"%s\" → %s\n",
                    test, isValidParentheses(test) ? "VALID" : "INVALID"));
        }

        sb.append("\n--- Key Points ---\n");
        sb.append("All operations O(1)\n");
        sb.append("JVM uses a stack for method calls (StackOverflowError = too deep recursion)\n");
        sb.append("Java provides: java.util.Stack (legacy), Deque (preferred)\n");

        return sb.toString();
    }
}
