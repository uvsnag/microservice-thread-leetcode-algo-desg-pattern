package com.example.learningservice.datastructure;

import java.util.*;

/**
 * ===================================================================
 * GRAPH (Adjacency List representation)
 * ===================================================================
 * Structure: Collection of vertices (nodes) connected by edges
 *
 *   A --- B --- D
 *   |     |
 *   C --- E --- F
 *
 * Representations:
 *   1. Adjacency Matrix: 2D array, O(V²) space, O(1) edge lookup
 *   2. Adjacency List:   list of neighbors per vertex, O(V+E) space ← WE USE THIS
 *
 * Key Algorithms:
 *   - BFS (Breadth-First Search): uses QUEUE, finds shortest path (unweighted)
 *   - DFS (Depth-First Search):   uses STACK/recursion, topological sort
 *
 * Real-world:
 *   - Social networks (friends, followers)
 *   - Route planning (Google Maps)
 *   - Dependency resolution (Maven, npm)
 *   - Microservice call graphs
 *
 * Interview essentials:
 *   - BFS vs DFS: when to use which
 *   - Cycle detection
 *   - Topological sort (for DAGs — dependency ordering)
 *   - Dijkstra's algorithm (shortest path with weights)
 * ===================================================================
 */
public class GraphDemo {

    // =====================================================
    // Graph using Adjacency List
    // =====================================================
    public static class Graph {
        private final Map<String, List<String>> adjacencyList;
        private final boolean isDirected;

        public Graph(boolean isDirected) {
            this.adjacencyList = new LinkedHashMap<>();
            this.isDirected = isDirected;
        }

        /** Add a vertex */
        public void addVertex(String vertex) {
            adjacencyList.putIfAbsent(vertex, new ArrayList<>());
        }

        /** Add an edge (undirected: adds both directions) */
        public void addEdge(String from, String to) {
            addVertex(from);
            addVertex(to);
            adjacencyList.get(from).add(to);
            if (!isDirected) {
                adjacencyList.get(to).add(from); // Undirected: both ways
            }
        }

        /**
         * BFS — Breadth-First Search
         * Uses a QUEUE → visits level by level
         * Guarantees shortest path in unweighted graph
         *
         * Time: O(V + E)
         * Space: O(V)
         */
        public String bfs(String start) {
            StringBuilder sb = new StringBuilder();
            sb.append("BFS from '").append(start).append("': ");

            Set<String> visited = new LinkedHashSet<>();
            Queue<String> queue = new LinkedList<>();

            visited.add(start);
            queue.add(start);

            List<String> order = new ArrayList<>();

            while (!queue.isEmpty()) {
                String current = queue.poll(); // Dequeue from front
                order.add(current);

                // Add all unvisited neighbors to queue
                for (String neighbor : adjacencyList.getOrDefault(current, List.of())) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);  // Enqueue
                    }
                }
            }

            sb.append(String.join(" → ", order));
            return sb.toString();
        }

        /**
         * DFS — Depth-First Search
         * Uses STACK (or recursion) → goes deep before backtracking
         *
         * Time: O(V + E)
         * Space: O(V)
         */
        public String dfs(String start) {
            StringBuilder sb = new StringBuilder();
            sb.append("DFS from '").append(start).append("': ");

            Set<String> visited = new LinkedHashSet<>();
            List<String> order = new ArrayList<>();

            dfsRecursive(start, visited, order);

            sb.append(String.join(" → ", order));
            return sb.toString();
        }

        private void dfsRecursive(String vertex, Set<String> visited, List<String> order) {
            visited.add(vertex);
            order.add(vertex);

            // Recurse into each unvisited neighbor (goes DEEP first)
            for (String neighbor : adjacencyList.getOrDefault(vertex, List.of())) {
                if (!visited.contains(neighbor)) {
                    dfsRecursive(neighbor, visited, order);
                }
            }
        }

        /**
         * Detect cycle in undirected graph using DFS
         */
        public boolean hasCycle() {
            Set<String> visited = new HashSet<>();
            for (String vertex : adjacencyList.keySet()) {
                if (!visited.contains(vertex)) {
                    if (hasCycleDFS(vertex, visited, null)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean hasCycleDFS(String vertex, Set<String> visited, String parent) {
            visited.add(vertex);
            for (String neighbor : adjacencyList.getOrDefault(vertex, List.of())) {
                if (!visited.contains(neighbor)) {
                    if (hasCycleDFS(neighbor, visited, vertex)) {
                        return true;
                    }
                } else if (!neighbor.equals(parent)) {
                    // Visited and not parent → cycle found!
                    return true;
                }
            }
            return false;
        }

        /**
         * Find shortest path using BFS (unweighted graph)
         */
        public String shortestPath(String start, String end) {
            Map<String, String> parentMap = new HashMap<>();
            Queue<String> queue = new LinkedList<>();
            Set<String> visited = new HashSet<>();

            visited.add(start);
            queue.add(start);
            parentMap.put(start, null);

            while (!queue.isEmpty()) {
                String current = queue.poll();
                if (current.equals(end)) {
                    // Reconstruct path
                    List<String> path = new ArrayList<>();
                    String node = end;
                    while (node != null) {
                        path.add(0, node);
                        node = parentMap.get(node);
                    }
                    return String.join(" → ", path);
                }
                for (String neighbor : adjacencyList.getOrDefault(current, List.of())) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        parentMap.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
            return "No path found";
        }

        /** Visualize the graph */
        public String visualize() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
                sb.append(entry.getKey()).append(" → ").append(entry.getValue()).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Demo of graph operations
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Graph (Adjacency List) ===\n\n");

        // Build an undirected graph
        //    A --- B --- D
        //    |     |
        //    C --- E --- F
        Graph graph = new Graph(false);
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("B", "E");
        graph.addEdge("C", "E");
        graph.addEdge("E", "F");

        sb.append("--- Graph Structure ---\n");
        sb.append(graph.visualize()).append("\n");

        // BFS — level by level (uses Queue)
        sb.append("--- BFS (uses Queue — level by level) ---\n");
        sb.append(graph.bfs("A")).append("\n\n");

        // DFS — deep first (uses Stack/recursion)
        sb.append("--- DFS (uses Stack — goes deep first) ---\n");
        sb.append(graph.dfs("A")).append("\n\n");

        // Shortest path
        sb.append("--- Shortest Path (BFS-based) ---\n");
        sb.append("A to F: ").append(graph.shortestPath("A", "F")).append("\n");
        sb.append("A to D: ").append(graph.shortestPath("A", "D")).append("\n\n");

        // Cycle detection
        sb.append("--- Cycle Detection ---\n");
        sb.append("Has cycle? ").append(graph.hasCycle()).append(" (A-B-E-C-A forms a cycle)\n\n");

        sb.append("--- BFS vs DFS ---\n");
        sb.append("BFS: shortest path (unweighted), level-order, uses Queue\n");
        sb.append("DFS: topological sort, cycle detection, uses Stack/recursion\n");
        sb.append("Both: O(V+E) time complexity\n");

        return sb.toString();
    }
}
