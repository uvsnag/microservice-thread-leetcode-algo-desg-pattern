package com.example.learningservice.controller;

import com.example.learningservice.datastructure.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/learning/data-structures")
public class DataStructureController {

    @GetMapping
    public Map<String, String> listDataStructures() {
        Map<String, String> structures = new LinkedHashMap<>();
        structures.put("linked-list", "/api/learning/data-structures/linked-list");
        structures.put("stack", "/api/learning/data-structures/stack");
        structures.put("queue", "/api/learning/data-structures/queue");
        structures.put("binary-tree", "/api/learning/data-structures/binary-tree");
        structures.put("hash-map", "/api/learning/data-structures/hash-map");
        structures.put("graph", "/api/learning/data-structures/graph");
        return structures;
    }

    @GetMapping("/linked-list")
    public Map<String, String> linkedList() {
        return Map.of("dataStructure", "Linked List", "output", LinkedListDemo.runDemo());
    }

    @GetMapping("/stack")
    public Map<String, String> stack() {
        return Map.of("dataStructure", "Stack", "output", StackDemo.runDemo());
    }

    @GetMapping("/queue")
    public Map<String, String> queue() {
        return Map.of("dataStructure", "Queue", "output", QueueDemo.runDemo());
    }

    @GetMapping("/binary-tree")
    public Map<String, String> binaryTree() {
        return Map.of("dataStructure", "Binary Search Tree", "output", BinaryTreeDemo.runDemo());
    }

    @GetMapping("/hash-map")
    public Map<String, String> hashMap() {
        return Map.of("dataStructure", "HashMap", "output", HashMapDemo.runDemo());
    }

    @GetMapping("/graph")
    public Map<String, String> graph() {
        return Map.of("dataStructure", "Graph", "output", GraphDemo.runDemo());
    }
}
