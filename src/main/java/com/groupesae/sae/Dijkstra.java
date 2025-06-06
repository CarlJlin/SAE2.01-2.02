package com.groupesae.sae;

import java.util.*;

public class Dijkstra {


    static class Edge {
        String to;
        int weight;

        public Edge(String to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public static void dijkstra(Map<String, List<Edge>> graph, String start) {
        Set<String> P = new HashSet<>();
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> predecessor = new HashMap<>();

        for (String node : graph.keySet()) {
            distance.put(node, Integer.MAX_VALUE);
        }
        distance.put(start, 0);

        while (P.size() < graph.size()) {

            String a = null;
            int minDist = Integer.MAX_VALUE;

            for (String node : graph.keySet()) {
                if (!P.contains(node) && distance.get(node) < minDist) {
                    minDist = distance.get(node);
                    a = node;
                }
            }

            if (a == null) break;

            P.add(a);

            for (Edge edge : graph.getOrDefault(a, new ArrayList<>())) {
                String b = edge.to;
                if (!P.contains(b)) {
                    int newDist = distance.get(a) + edge.weight;
                    if (newDist < distance.get(b)) {
                        distance.put(b, newDist);
                        predecessor.put(b, a);
                    }
                }
            }
        }

        for (String node : graph.keySet()) {
            System.out.println("Distance de " + start + " à " + node + " = " + distance.get(node));
            if (predecessor.containsKey(node)) {
                System.out.println("Prédecesseur de " + node + " : " + predecessor.get(node));
            }
        }
    }

    public static void main(String[] args) {
        Map<String, List<Edge>> graph = new HashMap<>();

        graph.put("A", Arrays.asList(new Edge("B", 3), new Edge("C", 1)));
        graph.put("B", Arrays.asList(new Edge("C", 7), new Edge("D", 5)));
        graph.put("C", Arrays.asList(new Edge("D", 2)));
        graph.put("D", new ArrayList<>());

        dijkstra(graph, "A");
    }
}