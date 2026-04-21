package com.example.learningservice.datastructure;

/**
 * ===================================================================
 * QUEUE (First-In, First-Out — FIFO)
 * ===================================================================
 * Structure: Elements added at REAR, removed from FRONT
 *   FRONT → | 10 | 20 | 30 | ← REAR
 *
 * Operations (all O(1) with circular array):
 *   - enqueue(item): Add to rear
 *   - dequeue():     Remove from front
 *   - peek():        View front without removing
 *
 * Real-world uses:
 *   - Message Queues (Kafka, RabbitMQ) — THIS PROJECT!
 *   - Task scheduling (thread pools, print queue)
 *   - BFS (Breadth-First Search) — uses queue
 *   - Request handling in web servers
 *
 * Variants:
 *   - Circular Queue: wraps around using modulo (no wasted space)
 *   - Priority Queue: dequeue by priority, not arrival order (heap-based)
 *   - Deque: insert/remove from BOTH ends
 * ===================================================================
 */
public class QueueDemo {

    // =====================================================
    // Circular Queue implementation (array-based)
    // =====================================================
    // Uses modulo to wrap around — no space wasted
    public static class CircularQueue {
        private int[] data;
        private int front;
        private int rear;
        private int size;
        private int capacity;

        public CircularQueue(int capacity) {
            this.capacity = capacity;
            this.data = new int[capacity];
            this.front = 0;
            this.rear = -1;
            this.size = 0;
        }

        /** Add element to rear — O(1) */
        public void enqueue(int item) {
            if (size == capacity) {
                throw new RuntimeException("Queue is full! Capacity: " + capacity);
            }
            // CIRCULAR: rear wraps around using modulo
            rear = (rear + 1) % capacity;
            data[rear] = item;
            size++;
        }

        /** Remove and return front element — O(1) */
        public int dequeue() {
            if (isEmpty()) {
                throw new RuntimeException("Queue is empty!");
            }
            int item = data[front];
            // CIRCULAR: front wraps around using modulo
            front = (front + 1) % capacity;
            size--;
            return item;
        }

        /** View front element without removing — O(1) */
        public int peek() {
            if (isEmpty()) {
                throw new RuntimeException("Queue is empty!");
            }
            return data[front];
        }

        public boolean isEmpty() { return size == 0; }
        public boolean isFull() { return size == capacity; }
        public int size() { return size; }

        @Override
        public String toString() {
            if (isEmpty()) return "Queue[]";
            StringBuilder sb = new StringBuilder("FRONT → [");
            int idx = front;
            for (int i = 0; i < size; i++) {
                sb.append(data[idx]);
                if (i < size - 1) sb.append(", ");
                idx = (idx + 1) % capacity;
            }
            sb.append("] ← REAR");
            return sb.toString();
        }
    }

    // =====================================================
    // Simple Priority Queue (min-heap based)
    // =====================================================
    public static class MinPriorityQueue {
        private int[] heap;
        private int size;

        public MinPriorityQueue(int capacity) {
            this.heap = new int[capacity];
            this.size = 0;
        }

        /** Insert with sift-up — O(log n) */
        public void insert(int value) {
            heap[size] = value;
            siftUp(size);
            size++;
        }

        /** Remove minimum (root) with sift-down — O(log n) */
        public int removeMin() {
            int min = heap[0];
            size--;
            heap[0] = heap[size];
            siftDown(0);
            return min;
        }

        /** Sift-up: move element up until heap property is restored */
        private void siftUp(int index) {
            while (index > 0) {
                int parent = (index - 1) / 2;
                if (heap[index] < heap[parent]) {
                    swap(index, parent);
                    index = parent;
                } else {
                    break;
                }
            }
        }

        /** Sift-down: move element down until heap property is restored */
        private void siftDown(int index) {
            while (index < size) {
                int leftChild = 2 * index + 1;
                int rightChild = 2 * index + 2;
                int smallest = index;

                if (leftChild < size && heap[leftChild] < heap[smallest]) {
                    smallest = leftChild;
                }
                if (rightChild < size && heap[rightChild] < heap[smallest]) {
                    smallest = rightChild;
                }
                if (smallest != index) {
                    swap(index, smallest);
                    index = smallest;
                } else {
                    break;
                }
            }
        }

        private void swap(int i, int j) {
            int temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
        }

        public boolean isEmpty() { return size == 0; }
    }

    /**
     * Demo of queue operations
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Queue (FIFO) ===\n\n");

        // --- Circular Queue ---
        sb.append("--- Circular Queue ---\n");
        CircularQueue queue = new CircularQueue(5);

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        sb.append("After enqueue 10, 20, 30: ").append(queue).append("\n");
        sb.append("Peek: ").append(queue.peek()).append("\n");

        int removed = queue.dequeue();
        sb.append("Dequeue: ").append(removed).append("\n");
        sb.append("After dequeue: ").append(queue).append("\n");

        // Demonstrate circular wrapping
        queue.enqueue(40);
        queue.enqueue(50);
        queue.enqueue(60); // This wraps around!
        sb.append("After enqueue 40, 50, 60 (wraps around): ").append(queue).append("\n");
        sb.append("Size: ").append(queue.size()).append(", Full: ").append(queue.isFull()).append("\n\n");

        // --- Priority Queue ---
        sb.append("--- Priority Queue (Min-Heap) ---\n");
        MinPriorityQueue pq = new MinPriorityQueue(10);
        int[] insertOrder = {30, 10, 50, 20, 40};
        sb.append("Insert order: 30, 10, 50, 20, 40\n");
        for (int val : insertOrder) {
            pq.insert(val);
        }

        sb.append("Remove order (always minimum first): ");
        while (!pq.isEmpty()) {
            sb.append(pq.removeMin()).append(" ");
        }
        sb.append("\n\n");

        sb.append("--- Key Points ---\n");
        sb.append("Circular Queue: O(1) enqueue/dequeue, no wasted space\n");
        sb.append("Priority Queue: O(log n) insert/remove, backed by heap\n");
        sb.append("Kafka/RabbitMQ are distributed queues — same FIFO concept at scale!\n");
        sb.append("Java: LinkedList implements Queue, PriorityQueue for priority queues\n");

        return sb.toString();
    }
}
