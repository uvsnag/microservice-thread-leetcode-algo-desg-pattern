package com.example.learningservice.datastructure;

/**
 * ===================================================================
 * LINKED LIST (Singly Linked)
 * ===================================================================
 * Structure: Each node stores data + pointer to next node
 *   [data|next] → [data|next] → [data|next] → null
 *
 * Operations & Complexity:
 *   - Access by index: O(n) — must traverse from head
 *   - Insert at head:  O(1) — just update head pointer
 *   - Insert at tail:  O(n) — traverse to end (O(1) if we keep tail)
 *   - Insert at index: O(n) — traverse to position
 *   - Delete:          O(n) — need to find the node
 *   - Search:          O(n) — linear scan
 *
 * vs ArrayList:
 *   - ArrayList: O(1) random access, O(n) insert/delete in middle
 *   - LinkedList: O(n) access, O(1) insert/delete IF you have the node reference
 *
 * When to use:
 *   - Frequent insertions/deletions at beginning
 *   - Don't need random access
 *   - Implementing stacks, queues, hash table chaining
 *
 * Interview classic: reverse a linked list, detect cycle, find middle
 * ===================================================================
 */
public class LinkedListDemo {

    // =====================================================
    // Node: the building block
    // =====================================================
    private static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    // =====================================================
    // Custom Singly Linked List implementation
    // =====================================================
    public static class SinglyLinkedList {
        private Node head;
        private int size;

        public SinglyLinkedList() {
            this.head = null;
            this.size = 0;
        }

        /** Insert at the BEGINNING — O(1) */
        public void insertAtHead(int data) {
            Node newNode = new Node(data);
            newNode.next = head; // New node points to old head
            head = newNode;      // Head now points to new node
            size++;
        }

        /** Insert at the END — O(n) */
        public void insertAtTail(int data) {
            Node newNode = new Node(data);
            if (head == null) {
                head = newNode;
            } else {
                Node current = head;
                while (current.next != null) {
                    current = current.next; // Traverse to last node
                }
                current.next = newNode;
            }
            size++;
        }

        /** Delete first occurrence of value — O(n) */
        public boolean delete(int data) {
            if (head == null) return false;

            // Special case: deleting head
            if (head.data == data) {
                head = head.next;
                size--;
                return true;
            }

            // Find the node BEFORE the one to delete
            Node current = head;
            while (current.next != null && current.next.data != data) {
                current = current.next;
            }

            if (current.next != null) {
                current.next = current.next.next; // Skip over the deleted node
                size--;
                return true;
            }
            return false; // Not found
        }

        /** Search for a value — O(n) */
        public boolean contains(int data) {
            Node current = head;
            while (current != null) {
                if (current.data == data) return true;
                current = current.next;
            }
            return false;
        }

        /**
         * CLASSIC INTERVIEW: Reverse a linked list — O(n)
         * Uses three pointers: prev, current, next
         */
        public void reverse() {
            Node prev = null;
            Node current = head;

            while (current != null) {
                Node next = current.next;  // Save next
                current.next = prev;       // Reverse the link
                prev = current;            // Move prev forward
                current = next;            // Move current forward
            }
            head = prev; // prev is now the new head
        }

        /**
         * CLASSIC INTERVIEW: Find middle element — O(n)
         * Fast-slow pointer technique (Floyd's)
         */
        public int findMiddle() {
            Node slow = head;
            Node fast = head;

            // Fast moves 2 steps, slow moves 1 step
            // When fast reaches end, slow is at middle
            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
            }
            return slow != null ? slow.data : -1;
        }

        /**
         * CLASSIC INTERVIEW: Detect cycle — O(n)
         * Floyd's Cycle Detection (Tortoise and Hare)
         */
        public boolean hasCycle() {
            Node slow = head;
            Node fast = head;

            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
                if (slow == fast) return true; // They met → cycle exists!
            }
            return false; // Fast reached end → no cycle
        }

        public int getSize() { return size; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Node current = head;
            while (current != null) {
                sb.append(current.data);
                if (current.next != null) sb.append(" → ");
                current = current.next;
            }
            sb.append(" → null");
            return sb.toString();
        }
    }

    /**
     * Demo of linked list operations
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Linked List ===\n\n");

        SinglyLinkedList list = new SinglyLinkedList();

        // Insert operations
        list.insertAtTail(10);
        list.insertAtTail(20);
        list.insertAtTail(30);
        list.insertAtHead(5);
        sb.append("After inserts (5, 10, 20, 30): ").append(list).append("\n");
        sb.append("Size: ").append(list.getSize()).append("\n\n");

        // Search
        sb.append("Contains 20? ").append(list.contains(20)).append("\n");
        sb.append("Contains 99? ").append(list.contains(99)).append("\n\n");

        // Find middle (fast-slow pointer)
        sb.append("Middle element: ").append(list.findMiddle()).append("\n\n");

        // Reverse
        list.reverse();
        sb.append("After reverse: ").append(list).append("\n\n");

        // Delete
        list.delete(20);
        sb.append("After delete 20: ").append(list).append("\n");
        sb.append("Size: ").append(list.getSize()).append("\n\n");

        // Cycle detection
        sb.append("Has cycle? ").append(list.hasCycle()).append("\n\n");

        sb.append("--- Complexity Summary ---\n");
        sb.append("Insert at head: O(1)\n");
        sb.append("Insert at tail: O(n) without tail pointer\n");
        sb.append("Search/Delete:  O(n)\n");
        sb.append("Reverse:        O(n) time, O(1) space\n");

        return sb.toString();
    }
}
