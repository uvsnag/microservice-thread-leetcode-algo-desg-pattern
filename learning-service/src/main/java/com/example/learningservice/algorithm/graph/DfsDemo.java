package com.example.learningservice.algorithm.graph;

import java.util.*;

/**
 * ===================================================================
 * DFS — Depth-First Search
 * ===================================================================
 * How it works:
 *   Uses a STACK (or recursion) to go as deep as possible before backtracking.
 *   1. Start from source, mark visited
 *   2. Visit an unvisited neighbor, go deeper
 *   3. When stuck (no unvisited neighbors), backtrack
 *   4. Repeat until all reachable nodes visited
 *
 * Time:  O(V + E)
 * Space: O(V) — recursion stack + visited set
 *
 * When to use:
 *   - Cycle detection
 *   - Topological sort (dependency ordering)
 *   - Finding connected components
 *   - Path existence (does a path exist?)
 *   - Solving mazes (find ANY path, not necessarily shortest)
 *   - Tree traversals (in/pre/post-order ARE DFS)
 *
 * Variants:
 *   - Recursive DFS (natural, elegant)
 *   - Iterative DFS (explicit stack, avoids stack overflow)
 *   - DFS with backtracking (permutations, combinations, Sudoku solver)
 * ===================================================================
 */
public class DfsDemo {

    public static String runDemo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DFS (Depth-First Search) ===\n\n");

        // Build a sample graph
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

        // ---- Recursive DFS ----
        sb.append("--- Recursive DFS from node 1 ---\n");
        sb.append(dfsRecursive(graph, 1)).append("\n");

        // ---- Iterative DFS ----
        sb.append("--- Iterative DFS from node 1 (uses explicit Stack) ---\n");
        sb.append(dfsIterative(graph, 1)).append("\n");

        // ---- Path Finding with DFS ----
        sb.append("--- DFS Path Finding ---\n");
        sb.append(dfsFindPath(graph, 1, 7)).append("\n");
        sb.append(dfsFindPath(graph, 4, 6)).append("\n");

        // ---- Cycle Detection ----
        sb.append("--- Cycle Detection (Directed Graph) ---\n");
        sb.append(cycleDetectionDemo()).append("\n");

        // ---- Topological Sort ----
        sb.append("--- Topological Sort (Dependency Ordering) ---\n");
        sb.append(topologicalSortDemo()).append("\n");

        // ---- Connected Components ----
        sb.append("--- Connected Components ---\n");
        sb.append(connectedComponentsDemo()).append("\n");

        sb.append("--- Key Points ---\n");
        sb.append("• DFS uses STACK (recursion or explicit) → goes deep first\n");
        sb.append("• Does NOT guarantee shortest path (use BFS for that)\n");
        sb.append("• Time: O(V+E), Space: O(V)\n");
        sb.append("• Key applications: cycle detection, topological sort, connected components\n");

        return sb.toString();
    }

    /**
     * Recursive DFS — elegant and natural
     */
    private static String dfsRecursive(Map<Integer, List<Integer>> graph, int start) {
        StringBuilder sb = new StringBuilder();
        Set<Integer> visited = new LinkedHashSet<>();
        sb.append("Visit order: ");
        dfsHelper(graph, start, visited, sb);
        sb.append("\n");
        return sb.toString();
    }

    private static void dfsHelper(Map<Integer, List<Integer>> graph, int node,
                                  Set<Integer> visited, StringBuilder sb) {
        // STEP 1: Mark current node as visited
        visited.add(node);
        sb.append(node).append(" ");

        // STEP 2: Recurse into each unvisited neighbor (goes DEEP first)
        for (int neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                dfsHelper(graph, neighbor, visited, sb);
            }
        }
        // STEP 3: When all neighbors visited, BACKTRACK (return from recursion)
    }

    /**
     * Iterative DFS — uses explicit Stack (avoids StackOverflow on large graphs)
     */
    private static String dfsIterative(Map<Integer, List<Integer>> graph, int start) {
        StringBuilder sb = new StringBuilder();
        Set<Integer> visited = new LinkedHashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();

        stack.push(start);
        sb.append("Visit order: ");

        while (!stack.isEmpty()) {
            int node = stack.pop();

            if (visited.contains(node)) continue; // Skip if already visited
            visited.add(node);
            sb.append(node).append(" ");

            // Push neighbors in REVERSE order so leftmost is processed first
            List<Integer> neighbors = graph.getOrDefault(node, List.of());
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                if (!visited.contains(neighbors.get(i))) {
                    stack.push(neighbors.get(i));
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * DFS to find a path between two nodes
     */
    private static String dfsFindPath(Map<Integer, List<Integer>> graph, int start, int end) {
        List<Integer> path = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        if (dfsFindPathHelper(graph, start, end, visited, path)) {
            return String.format("Path %d → %d: %s\n", start, end, path);
        }
        return String.format("No path from %d to %d\n", start, end);
    }

    private static boolean dfsFindPathHelper(Map<Integer, List<Integer>> graph, int node,
                                             int target, Set<Integer> visited, List<Integer> path) {
        visited.add(node);
        path.add(node);

        if (node == target) return true; // Found!

        for (int neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                if (dfsFindPathHelper(graph, neighbor, target, visited, path)) {
                    return true;
                }
            }
        }

        path.remove(path.size() - 1); // BACKTRACK: remove from path
        return false;
    }

    /**
     * Cycle detection in a DIRECTED graph using DFS with 3 colors:
     *   WHITE (0) = unvisited
     *   GRAY  (1) = in current DFS path (being processed)
     *   BLACK (2) = fully processed
     *
     * Cycle exists if we encounter a GRAY node (back edge)
     */
    private static String cycleDetectionDemo() {
        StringBuilder sb = new StringBuilder();

        // Directed graph WITH cycle: A → B → C → A
        Map<String, List<String>> graphWithCycle = new LinkedHashMap<>();
        graphWithCycle.put("A", List.of("B"));
        graphWithCycle.put("B", List.of("C"));
        graphWithCycle.put("C", List.of("A"));  // Creates cycle!
        graphWithCycle.put("D", List.of("B"));

        sb.append("Graph with cycle: A→B→C→A, D→B\n");
        sb.append("Has cycle? ").append(hasCycleDirected(graphWithCycle)).append("\n");

        // Directed graph WITHOUT cycle (DAG)
        Map<String, List<String>> dag = new LinkedHashMap<>();
        dag.put("A", List.of("B", "C"));
        dag.put("B", List.of("D"));
        dag.put("C", List.of("D"));
        dag.put("D", List.of());

        sb.append("DAG: A→B→D, A→C→D\n");
        sb.append("Has cycle? ").append(hasCycleDirected(dag)).append("\n");
        return sb.toString();
    }

    private static boolean hasCycleDirected(Map<String, List<String>> graph) {
        Map<String, Integer> color = new HashMap<>(); // 0=WHITE, 1=GRAY, 2=BLACK
        for (String node : graph.keySet()) color.put(node, 0);

        for (String node : graph.keySet()) {
            if (color.get(node) == 0) { // WHITE
                if (hasCycleDFS(graph, node, color)) return true;
            }
        }
        return false;
    }

    private static boolean hasCycleDFS(Map<String, List<String>> graph, String node,
                                       Map<String, Integer> color) {
        color.put(node, 1); // Mark GRAY (in current path)

        for (String neighbor : graph.getOrDefault(node, List.of())) {
            if (color.getOrDefault(neighbor, 0) == 1) return true;  // GRAY → cycle!
            if (color.getOrDefault(neighbor, 0) == 0) { // WHITE → recurse
                if (hasCycleDFS(graph, neighbor, color)) return true;
            }
        }

        color.put(node, 2); // Mark BLACK (fully processed)
        return false;
    }

    /**
     * Topological Sort — ordering where every edge u→v has u before v
     * Used for: build systems, course scheduling, dependency resolution
     *
     * Only works on DAGs (Directed Acyclic Graphs)
     */
    private static String topologicalSortDemo() {
        StringBuilder sb = new StringBuilder();

        // Course prerequisites:
        // Math → Physics → Quantum
        // Math → CS → AI
        // CS → ML → AI
        Map<String, List<String>> courses = new LinkedHashMap<>();
        courses.put("Math", List.of("Physics", "CS"));
        courses.put("Physics", List.of("Quantum"));
        courses.put("CS", List.of("AI", "ML"));
        courses.put("ML", List.of("AI"));
        courses.put("Quantum", List.of());
        courses.put("AI", List.of());

        sb.append("Prerequisites: Math→{Physics,CS}, Physics→Quantum, CS→{AI,ML}, ML→AI\n");

        // DFS-based topological sort
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String course : courses.keySet()) {
            if (!visited.contains(course)) {
                topoSortDFS(courses, course, visited, stack);
            }
        }

        sb.append("Topological order (valid course schedule): ");
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
            if (!stack.isEmpty()) sb.append(" → ");
        }
        sb.append("\n");
        return sb.toString();
    }

    private static void topoSortDFS(Map<String, List<String>> graph, String node,
                                     Set<String> visited, Deque<String> stack) {
        visited.add(node);
        for (String neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                topoSortDFS(graph, neighbor, visited, stack);
            }
        }
        stack.push(node); // Push AFTER all descendants are processed
    }

    /**
     * Connected Components — find groups of connected nodes
     */
    private static String connectedComponentsDemo() {
        StringBuilder sb = new StringBuilder();

        // Three separate groups: {1,2,3}, {4,5}, {6}
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(1, List.of(2, 3));
        graph.put(2, List.of(1, 3));
        graph.put(3, List.of(1, 2));
        graph.put(4, List.of(5));
        graph.put(5, List.of(4));
        graph.put(6, List.of());

        Set<Integer> visited = new HashSet<>();
        int componentCount = 0;

        for (int node : graph.keySet()) {
            if (!visited.contains(node)) {
                componentCount++;
                List<Integer> component = new ArrayList<>();
                dfsCollect(graph, node, visited, component);
                sb.append(String.format("Component %d: %s\n", componentCount, component));
            }
        }
        sb.append(String.format("Total connected components: %d\n", componentCount));
        return sb.toString();
    }

    private static void dfsCollect(Map<Integer, List<Integer>> graph, int node,
                                   Set<Integer> visited, List<Integer> component) {
        visited.add(node);
        component.add(node);
        for (int neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                dfsCollect(graph, neighbor, visited, component);
            }
        }
    }
}
