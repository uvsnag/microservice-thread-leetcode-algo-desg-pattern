package com.example.learningservice.datastructure;

import java.util.ArrayList;
import java.util.List;

/**
 * ===================================================================
 * HASH MAP (Custom Implementation)
 * ===================================================================
 * Structure: Array of "buckets", each bucket is a linked list (chaining)
 *   Index 0: → [key1=val1] → [key5=val5] → null  (hash collision!)
 *   Index 1: → [key2=val2] → null
 *   Index 2: → null (empty bucket)
 *   Index 3: → [key3=val3] → null
 *
 * Operations (average case):
 *   - put(key, value): O(1)
 *   - get(key):        O(1)
 *   - remove(key):     O(1)
 *   - Worst case:      O(n) when all keys hash to same bucket
 *
 * Key concepts:
 *   - Hash function: converts key → array index
 *   - Collision handling: chaining (linked list) or open addressing
 *   - Load factor: ratio of entries to buckets (Java default: 0.75)
 *   - Rehashing: double capacity when load factor exceeded
 *
 * Interview essentials:
 *   - hashCode() and equals() contract
 *   - Why initial capacity matters for performance
 *   - Java 8: buckets switch from LinkedList to Red-Black Tree at 8 entries
 *   - HashMap is NOT thread-safe → ConcurrentHashMap for multi-threading
 * ===================================================================
 */
public class HashMapDemo {

    // =====================================================
    // Custom HashMap implementation with chaining
    // =====================================================
    public static class SimpleHashMap<K, V> {

        // Entry: a node in the bucket's linked list
        private static class Entry<K, V> {
            K key;
            V value;
            Entry<K, V> next; // For chaining (collision handling)

            Entry(K key, V value) {
                this.key = key;
                this.value = value;
                this.next = null;
            }
        }

        private Entry<K, V>[] buckets;  // The array of buckets
        private int size;
        private int capacity;
        private static final double LOAD_FACTOR = 0.75;

        @SuppressWarnings("unchecked")
        public SimpleHashMap(int capacity) {
            this.capacity = capacity;
            this.buckets = new Entry[capacity];
            this.size = 0;
        }

        public SimpleHashMap() {
            this(16); // Default capacity like Java's HashMap
        }

        /**
         * Hash function: converts key to bucket index
         * Uses Math.abs and modulo to map to valid index
         */
        private int hash(K key) {
            // Use key's hashCode(), take absolute value, mod by capacity
            return Math.abs(key.hashCode()) % capacity;
        }

        /** Put key-value pair — O(1) average */
        public void put(K key, V value) {
            // Check if we need to resize (load factor exceeded)
            if ((double) size / capacity > LOAD_FACTOR) {
                resize();
            }

            int index = hash(key);
            Entry<K, V> head = buckets[index];

            // Check if key already exists → update value
            Entry<K, V> current = head;
            while (current != null) {
                if (current.key.equals(key)) {
                    current.value = value; // Update existing
                    return;
                }
                current = current.next;
            }

            // Key doesn't exist → add to front of chain (O(1))
            Entry<K, V> newEntry = new Entry<>(key, value);
            newEntry.next = head;
            buckets[index] = newEntry;
            size++;
        }

        /** Get value by key — O(1) average */
        public V get(K key) {
            int index = hash(key);
            Entry<K, V> current = buckets[index];

            while (current != null) {
                if (current.key.equals(key)) {
                    return current.value; // Found!
                }
                current = current.next;
            }
            return null; // Not found
        }

        /** Remove key — O(1) average */
        public boolean remove(K key) {
            int index = hash(key);
            Entry<K, V> current = buckets[index];
            Entry<K, V> prev = null;

            while (current != null) {
                if (current.key.equals(key)) {
                    if (prev == null) {
                        buckets[index] = current.next; // Remove head
                    } else {
                        prev.next = current.next; // Skip over
                    }
                    size--;
                    return true;
                }
                prev = current;
                current = current.next;
            }
            return false;
        }

        /**
         * Resize: double capacity and rehash all entries
         * This is O(n) but happens rarely (amortized O(1) per put)
         */
        @SuppressWarnings("unchecked")
        private void resize() {
            int newCapacity = capacity * 2;
            Entry<K, V>[] oldBuckets = buckets;
            buckets = new Entry[newCapacity];
            capacity = newCapacity;
            size = 0;

            // Rehash all existing entries
            for (Entry<K, V> head : oldBuckets) {
                Entry<K, V> current = head;
                while (current != null) {
                    put(current.key, current.value);
                    current = current.next;
                }
            }
        }

        public int size() { return size; }

        /** Visualize bucket distribution — shows collision patterns */
        public String visualize() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < capacity; i++) {
                Entry<K, V> current = buckets[i];
                if (current != null) {
                    sb.append(String.format("Bucket[%d]: ", i));
                    while (current != null) {
                        sb.append(String.format("[%s=%s]", current.key, current.value));
                        if (current.next != null) sb.append(" → ");
                        current = current.next;
                    }
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
    }

    /**
     * Demo of HashMap operations
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HashMap (Custom Implementation) ===\n\n");

        SimpleHashMap<String, Integer> map = new SimpleHashMap<>(4); // Small capacity to show resizing

        // Insert entries
        sb.append("--- Put Operations ---\n");
        map.put("Alice", 90);
        map.put("Bob", 85);
        map.put("Charlie", 92);
        map.put("Diana", 88);
        sb.append("Added: Alice=90, Bob=85, Charlie=92, Diana=88\n");
        sb.append("Bucket distribution:\n").append(map.visualize()).append("\n");

        // Get operations
        sb.append("--- Get Operations ---\n");
        sb.append("get(Alice): ").append(map.get("Alice")).append("\n");
        sb.append("get(Bob): ").append(map.get("Bob")).append("\n");
        sb.append("get(Unknown): ").append(map.get("Unknown")).append("\n\n");

        // Update existing key
        map.put("Alice", 95);
        sb.append("--- Update Alice to 95 ---\n");
        sb.append("get(Alice): ").append(map.get("Alice")).append("\n\n");

        // Add more to trigger resize
        map.put("Eve", 91);
        map.put("Frank", 87);
        sb.append("--- After adding Eve and Frank (may trigger resize) ---\n");
        sb.append("Size: ").append(map.size()).append("\n");
        sb.append("Bucket distribution:\n").append(map.visualize()).append("\n");

        // Remove
        map.remove("Bob");
        sb.append("--- After removing Bob ---\n");
        sb.append("get(Bob): ").append(map.get("Bob")).append("\n");
        sb.append("Size: ").append(map.size()).append("\n\n");

        sb.append("--- Key Points ---\n");
        sb.append("Hash function: key.hashCode() % capacity → bucket index\n");
        sb.append("Collision: chaining (linked list in bucket)\n");
        sb.append("Load factor 0.75: resize when 75% full\n");
        sb.append("Java 8+: bucket converts to Red-Black Tree at 8 entries\n");
        sb.append("ALWAYS override both hashCode() AND equals()!\n");

        return sb.toString();
    }
}
