package com.example.learningservice.algorithm.graph;

import java.util.*;

/**
 * ===================================================================
 * BFS — Breadth-First Search
 * ===================================================================
 * How it works:
 *   Uses a QUEUE to visit nodes level-by-level.
 *   1. Start from source, add to queue, mark visited
 *   2. Dequeue a node, process it
 *   3. Enqueue ALL unvisited neighbors
 *   4. Repeat until queue is empty
 *
 * Time:  O(V + E) — visit every vertex and edge once
 * Space: O(V)     — queue + visited set
 *
 * Properties:
 *   - Explores ALL neighbors before going deeper
 *   - Guarantees SHORTEST PATH in unweighted graphs
 *   - Level-order traversal of trees is BFS
 *
 * When to use:
 *   - Shortest path in unweighted graph
 *   - Level-order traversal
 *   - Finding connected components
 *   - Web crawlers, social network "degrees of separation"
 *   - Solving puzzles with minimum moves (e.g., shortest maze path)
 *
 * BFS vs DFS:
 *   BFS → shortest path, level-by-level, uses Queue
 *   DFS → topological sort, cycle detection, uses Stack/recursion
 * ===================================================================
 */
public class BfsDemo {

    /**
     * Standard BFS on adjacency list graph.
     * Returns visit order + level info.
     */
    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== BFS (Breadth-First Search) ===\n\n");

        // Build a sample graph:
        //      1
        //     / \
        //    2   3
        //   / \   \
        //  4   5   6
        //       \ /
        //        7
        Map<Integer, List<Integer>> graph = new LinkedHashMap<>();
        graph.put(1, List.of(2, 3));
        graph.put(2, List.of(1, 4, 5));
        graph.put(3, List.of(1, 6));
        graph.put(4, List.of(2));
        graph.put(5, List.of(2, 7));
        graph.put(6, List.of(3, 7));
        graph.put(7, List.of(5, 6));

        sb.append("Graph:\n");
        sb.append("    1\n   / \\\n  2   3\n / \\   \\\n4   5   6\n     \\ /\n      7\n\n");

        // ---- Standard BFS ----
        sb.append("--- Standard BFS from node 1 ---\n");
        sb.append(bfs(graph, 1)).append("\n");

        // ---- BFS with level tracking ----
        sb.append("--- BFS with Level Tracking ---\n");
        sb.append(bfsWithLevels(graph, 1)).append("\n");

        // ---- Shortest Path using BFS ----
        sb.append("--- Shortest Path (BFS) ---\n");
        sb.append(bfsShortestPath(graph, 1, 7)).append("\n");
        sb.append(bfsShortestPath(graph, 4, 6)).append("\n");

        // ---- BFS on Grid (2D Matrix) ----
        sb.append("--- BFS on Grid (Maze Shortest Path) ---\n");
        sb.append(bfsGrid()).append("\n");

        sb.append("--- Key Points ---\n");
        sb.append("• BFS uses QUEUE (FIFO) → processes level-by-level\n");
        sb.append("• Guarantees shortest path in UNWEIGHTED graphs\n");
        sb.append("• Time: O(V+E), Space: O(V)\n");
        sb.append("• For weighted graphs → use Dijkstra instead\n");

        return sb.toString();
    }

    /**
     * Standard BFS — visits all nodes level by level
     */
    private static String bfs(Map<Integer, List<Integer>> graph, int start) {
        StringBuilder sb = new StringBuilder();
        Set<Integer> visited = new LinkedHashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        // STEP 1: Start with the source node
        visited.add(start);
        queue.add(start);

        sb.append("Visit order: ");
        while (!queue.isEmpty()) {
            // STEP 2: Dequeue and process
            int node = queue.poll();
            sb.append(node).append(" ");

            // STEP 3: Enqueue all unvisited neighbors
            for (int neighbor : graph.getOrDefault(node, List.of())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * BFS with level tracking — useful for "minimum steps" problems
     */
    private static String bfsWithLevels(Map<Integer, List<Integer>> graph, int start) {
        StringBuilder sb = new StringBuilder();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        visited.add(start);
        queue.add(start);
        int level = 0;

        while (!queue.isEmpty()) {
            // Process ALL nodes at current level before moving to next
            int levelSize = queue.size();  // KEY: snapshot the size
            sb.append(String.format("Level %d: ", level));

            for (int i = 0; i < levelSize; i++) {
                int node = queue.poll();
                sb.append(node).append(" ");

                for (int neighbor : graph.getOrDefault(node, List.of())) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
            sb.append("\n");
            level++;
        }
        return sb.toString();
    }

    /**
     * BFS Shortest Path — reconstructs path using parent map
     */
    private static String bfsShortestPath(Map<Integer, List<Integer>> graph, int start, int end) {
        Map<Integer, Integer> parent = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        visited.add(start);
        queue.add(start);
        parent.put(start, -1);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            if (node == end) {
                // Reconstruct path by following parent pointers
                List<Integer> path = new ArrayList<>();
                int current = end;
                while (current != -1) {
                    path.add(0, current);
                    current = parent.get(current);
                }
                return String.format("Shortest path %d → %d: %s (distance: %d)\n",
                        start, end, path, path.size() - 1);
            }
            for (int neighbor : graph.getOrDefault(node, List.of())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, node);
                    queue.add(neighbor);
                }
            }
        }
        return String.format("No path from %d to %d\n", start, end);
    }

    /**
     * BFS on 2D Grid — classic interview pattern (maze/island problems)
     */
    private static String bfsGrid() {
        StringBuilder sb = new StringBuilder();

        // 0 = path, 1 = wall
        // Find shortest path from top-left to bottom-right
        int[][] grid = {
                {0, 0, 1, 0},
                {0, 1, 0, 0},
                {0, 0, 0, 1},
                {1, 0, 0, 0}
        };

        sb.append("Grid (0=path, 1=wall):\n");
        for (int[] row : grid) sb.append(Arrays.toString(row)).append("\n");

        // BFS on grid uses 4-directional movement
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // right, left, down, up
        int rows = grid.length, cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();

        queue.add(new int[]{0, 0, 0}); // {row, col, distance}
        visited[0][0] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0], c = curr[1], dist = curr[2];

            if (r == rows - 1 && c == cols - 1) {
                sb.append(String.format("Shortest path from (0,0) to (%d,%d): %d steps\n",
                        rows - 1, cols - 1, dist));
                return sb.toString();
            }

            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !visited[nr][nc] && grid[nr][nc] == 0) {
                    visited[nr][nc] = true;
                    queue.add(new int[]{nr, nc, dist + 1});
                }
            }
        }
        sb.append("No path found!\n");
        return sb.toString();
    }
}
