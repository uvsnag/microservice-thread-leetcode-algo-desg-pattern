package com.example.learningservice.datastructure;

import java.util.ArrayList;
import java.util.List;

/**
 * ===================================================================
 * BINARY SEARCH TREE (BST)
 * ===================================================================
 * Structure: Each node has at most 2 children.
 *   - LEFT child < parent
 *   - RIGHT child > parent
 *
 *         50
 *        /  \
 *      30    70
 *     / \   / \
 *   20  40 60  80
 *
 * Operations (balanced BST):
 *   - Search:  O(log n)
 *   - Insert:  O(log n)
 *   - Delete:  O(log n)
 *
 * DANGER: If you insert sorted data → degenerates to linked list → O(n)!
 *   Solution: Self-balancing trees (AVL, Red-Black)
 *
 * Traversals (KEY INTERVIEW TOPIC):
 *   - In-order (Left, Root, Right): gives sorted order!
 *   - Pre-order (Root, Left, Right): copy tree structure
 *   - Post-order (Left, Right, Root): delete tree, postfix evaluation
 *   - Level-order (BFS): level by level using queue
 *
 * Real-world:
 *   - Java TreeMap/TreeSet are Red-Black Trees
 *   - Database indexes often use B-Trees (variant)
 * ===================================================================
 */
public class BinaryTreeDemo {

    private static class TreeNode {
        int value;
        TreeNode left, right;

        TreeNode(int value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    public static class BinarySearchTree {
        private TreeNode root;

        /** Insert a value — O(log n) average, O(n) worst */
        public void insert(int value) {
            root = insertRec(root, value);
        }

        private TreeNode insertRec(TreeNode node, int value) {
            if (node == null) {
                return new TreeNode(value); // Found empty spot
            }
            if (value < node.value) {
                node.left = insertRec(node.left, value);   // Go left
            } else if (value > node.value) {
                node.right = insertRec(node.right, value);  // Go right
            }
            // Duplicate values ignored
            return node;
        }

        /** Search for a value — O(log n) average */
        public boolean search(int value) {
            return searchRec(root, value);
        }

        private boolean searchRec(TreeNode node, int value) {
            if (node == null) return false;           // Not found
            if (value == node.value) return true;     // Found!
            if (value < node.value) return searchRec(node.left, value);  // Go left
            return searchRec(node.right, value);      // Go right
        }

        /**
         * IN-ORDER traversal: Left → Root → Right
         * For BST: produces SORTED output!
         */
        public List<Integer> inOrder() {
            List<Integer> result = new ArrayList<>();
            inOrderRec(root, result);
            return result;
        }

        private void inOrderRec(TreeNode node, List<Integer> result) {
            if (node != null) {
                inOrderRec(node.left, result);    // Visit left subtree
                result.add(node.value);            // Visit root
                inOrderRec(node.right, result);   // Visit right subtree
            }
        }

        /**
         * PRE-ORDER traversal: Root → Left → Right
         * Used to copy/serialize tree structure
         */
        public List<Integer> preOrder() {
            List<Integer> result = new ArrayList<>();
            preOrderRec(root, result);
            return result;
        }

        private void preOrderRec(TreeNode node, List<Integer> result) {
            if (node != null) {
                result.add(node.value);            // Visit root FIRST
                preOrderRec(node.left, result);
                preOrderRec(node.right, result);
            }
        }

        /**
         * POST-ORDER traversal: Left → Right → Root
         * Used to delete tree, evaluate postfix
         */
        public List<Integer> postOrder() {
            List<Integer> result = new ArrayList<>();
            postOrderRec(root, result);
            return result;
        }

        private void postOrderRec(TreeNode node, List<Integer> result) {
            if (node != null) {
                postOrderRec(node.left, result);
                postOrderRec(node.right, result);
                result.add(node.value);            // Visit root LAST
            }
        }

        /** Find height of tree — O(n) */
        public int height() {
            return heightRec(root);
        }

        private int heightRec(TreeNode node) {
            if (node == null) return -1; // Empty tree has height -1
            return 1 + Math.max(heightRec(node.left), heightRec(node.right));
        }

        /** Find minimum value — O(log n) average (leftmost node) */
        public int findMin() {
            TreeNode current = root;
            while (current.left != null) {
                current = current.left; // Keep going left
            }
            return current.value;
        }

        /** Find maximum value — O(log n) average (rightmost node) */
        public int findMax() {
            TreeNode current = root;
            while (current.right != null) {
                current = current.right; // Keep going right
            }
            return current.value;
        }

        /** Visual representation of the tree */
        public String visualize() {
            StringBuilder sb = new StringBuilder();
            visualizeRec(root, "", true, sb);
            return sb.toString();
        }

        private void visualizeRec(TreeNode node, String prefix, boolean isLast, StringBuilder sb) {
            if (node != null) {
                sb.append(prefix);
                sb.append(isLast ? "└── " : "├── ");
                sb.append(node.value).append("\n");
                visualizeRec(node.left, prefix + (isLast ? "    " : "│   "), false, sb);
                visualizeRec(node.right, prefix + (isLast ? "    " : "│   "), true, sb);
            }
        }
    }

    /**
     * Demo of BST operations
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Binary Search Tree ===\n\n");

        BinarySearchTree bst = new BinarySearchTree();

        // Insert elements
        int[] values = {50, 30, 70, 20, 40, 60, 80, 10, 35};
        sb.append("Insert order: ");
        for (int v : values) {
            sb.append(v).append(" ");
            bst.insert(v);
        }
        sb.append("\n\n");

        // Visualize
        sb.append("--- Tree Structure ---\n");
        sb.append(bst.visualize()).append("\n");

        // Traversals — KEY INTERVIEW TOPIC
        sb.append("--- Traversals ---\n");
        sb.append("In-order   (L,Root,R) = sorted! : ").append(bst.inOrder()).append("\n");
        sb.append("Pre-order  (Root,L,R)            : ").append(bst.preOrder()).append("\n");
        sb.append("Post-order (L,R,Root)            : ").append(bst.postOrder()).append("\n\n");

        // Search
        sb.append("--- Search ---\n");
        sb.append("Search 40: ").append(bst.search(40)).append("\n");
        sb.append("Search 99: ").append(bst.search(99)).append("\n\n");

        // Properties
        sb.append("--- Properties ---\n");
        sb.append("Height: ").append(bst.height()).append("\n");
        sb.append("Min: ").append(bst.findMin()).append("\n");
        sb.append("Max: ").append(bst.findMax()).append("\n\n");

        sb.append("--- Key Points ---\n");
        sb.append("In-order traversal of BST = SORTED output!\n");
        sb.append("Balanced BST: O(log n) operations\n");
        sb.append("Unbalanced (sorted insert): degrades to O(n) → use AVL/Red-Black\n");
        sb.append("Java TreeMap = Red-Black Tree (self-balancing BST)\n");

        return sb.toString();
    }
}
