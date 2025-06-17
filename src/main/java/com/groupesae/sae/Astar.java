package com.groupesae.sae;

import java.util.*;

public class Astar implements PathFinder {
    private Grille grille;

    public Astar(Grille grille) {
        this.grille = grille;
    }

    public List<int[]> trouverCheminVersSortie(int startX, int startY) {
        int width = grille.getX();
        int height = grille.getY();
        boolean[][] visited = new boolean[height][width];
        int[][][] prev = new int[height][width][2];

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        openSet.add(new Node(startX, startY, 0, heuristique(startX, startY)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (estSortie(current.x, current.y)) {
                return reconstruireChemin(prev, startX, startY, current.x, current.y);
            }
            if (visited[current.y][current.x]) continue;
            visited[current.y][current.x] = true;

            for (int[] dir : new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}}) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                if (visited[ny][nx]) continue;
                int element = grille.getElement(ny, nx);
                if (element == Grille.ROCHER) continue;

                int g = current.g + 1;
                int h = heuristique(nx, ny);
                openSet.add(new Node(nx, ny, g, g + h));
                prev[ny][nx][0] = current.x;
                prev[ny][nx][1] = current.y;
            }
        }
        return Collections.emptyList();
    }

    private boolean estSortie(int x, int y) {
        int width = grille.getX();
        int height = grille.getY();
        if ((x == 0 || x == width - 1 || y == 0 || y == height - 1) && grille.getElement(y, x) != Grille.ROCHER) {
            return true;
        }
        return false;
    }

    private int heuristique(int x, int y) {
        int width = grille.getX();
        int height = grille.getY();
        int distBord = Math.min(Math.min(x, width - 1 - x), Math.min(y, height - 1 - y));
        return distBord;
    }

    public List<int[]> trouverChemin(int startX, int startY, int endX, int endY) {
        int width = grille.getX();
        int height = grille.getY();
        boolean[][] visited = new boolean[height][width];
        int[][][] prev = new int[height][width][2];

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        openSet.add(new Node(startX, startY, 0, heuristiqueDistance(startX, startY, endX, endY)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.x == endX && current.y == endY) {
                return reconstruireChemin(prev, startX, startY, endX, endY);
            }
            if (visited[current.y][current.x]) continue;
            visited[current.y][current.x] = true;

            for (int[] dir : new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}}) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                if (visited[ny][nx]) continue;
                int element = grille.getElement(ny, nx);
                if (element == Grille.ROCHER || element == Grille.LOUP) continue;

                int g = current.g + 1;
                int h = heuristiqueDistance(nx, ny, endX, endY);
                openSet.add(new Node(nx, ny, g, g + h));
                prev[ny][nx][0] = current.x;
                prev[ny][nx][1] = current.y;
            }
        }
        return null;
    }

    private int heuristiqueDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private List<int[]> reconstruireChemin(int[][][] prev, int startX, int startY, int endX, int endY) {
        LinkedList<int[]> chemin = new LinkedList<>();
        int x = endX, y = endY;
        while (x != startX || y != startY) {
            chemin.addFirst(new int[]{x, y});
            int px = prev[y][x][0];
            int py = prev[y][x][1];
            x = px;
            y = py;
        }
        chemin.addFirst(new int[]{startX, startY});
        return chemin;
    }

    private static class Node {
        int x, y, g, f;
        Node(int x, int y, int g, int f) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.f = f;
        }
    }
}