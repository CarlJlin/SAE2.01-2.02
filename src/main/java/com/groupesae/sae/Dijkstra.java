package com.groupesae.sae;

import java.util.*;

public class Dijkstra implements PathFinder {
    private Grille grille;
    private int hauteur;
    private int largeur;
    private int[][] distance;

    public Dijkstra(Grille grille) {
        this.grille = grille;
        this.hauteur = grille.getY();
        this.largeur = grille.getX();
        this.distance = new int[hauteur][largeur];
    }

    public List<int[]> trouverCheminVersSortie(int startX, int startY) {
        // Réinitialiser la matrice de distance
        for (int i = 0; i < hauteur; i++) {
            Arrays.fill(distance[i], Integer.MAX_VALUE);
        }

        // Tableau pour stocker les prédécesseurs
        int[][][] predecesseur = new int[hauteur][largeur][2];

        // Tableau pour marquer les cellules visitées
        boolean[][] visite = new boolean[hauteur][largeur];

        // File de priorité pour l'algorithme de Dijkstra
        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        // Position de départ
        distance[startY][startX] = 0;
        queue.add(new int[]{startX, startY, 0});

        // Variables pour stocker la meilleure sortie trouvée
        int meilleureDistanceSortie = Integer.MAX_VALUE;
        int meilleurX = -1, meilleurY = -1;

        // Directions possibles (haut, droite, bas, gauche)
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            if (visite[y][x]) continue;
            visite[y][x] = true;

            // Vérifier si on est sur une sortie
            if ((x == 0 || x == largeur - 1 || y == 0 || y == hauteur - 1) &&
                    grille.getElement(y, x) != Grille.ROCHER) {
                if (distance[y][x] < meilleureDistanceSortie) {
                    meilleureDistanceSortie = distance[y][x];
                    meilleurX = x;
                    meilleurY = y;
                }
            }

            // Vérifier chaque direction
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                // Vérifier si la nouvelle position est valide
                if (newX >= 0 && newX < largeur && newY >= 0 && newY < hauteur &&
                        grille.getElement(newY, newX) != Grille.ROCHER && !visite[newY][newX]) {

                    // Calculer la nouvelle distance
                    int poids = getPoids(grille.getElement(newY, newX));
                    int newDist = distance[y][x] + poids;

                    // Si cette route est meilleure, mettre à jour la distance
                    if (newDist < distance[newY][newX]) {
                        distance[newY][newX] = newDist;
                        predecesseur[newY][newX][0] = x;
                        predecesseur[newY][newX][1] = y;
                        queue.add(new int[]{newX, newY, newDist});
                    }
                }
            }
        }

        // Reconstruire le chemin vers la meilleure sortie
        List<int[]> chemin = new ArrayList<>();
        if (meilleurX != -1 && meilleurY != -1) {
            int x = meilleurX;
            int y = meilleurY;

            while (!(x == startX && y == startY)) {
                chemin.add(0, new int[]{x, y});
                int tempX = predecesseur[y][x][0];
                int tempY = predecesseur[y][x][1];
                x = tempX;
                y = tempY;
            }
        }

        return chemin;
    }

    public List<int[]> trouverChemin(int startX, int startY, int endX, int endY) {
        // Réinitialiser la matrice de distance
        for (int i = 0; i < hauteur; i++) {
            Arrays.fill(distance[i], Integer.MAX_VALUE);
        }

        // Tableau pour stocker les prédécesseurs
        int[][][] predecesseur = new int[hauteur][largeur][2];

        // Tableau pour marquer les cellules visitées
        boolean[][] visite = new boolean[hauteur][largeur];

        // File de priorité pour l'algorithme de Dijkstra
        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        // Position de départ
        distance[startY][startX] = 0;
        queue.add(new int[]{startX, startY, 0});

        // Directions possibles (haut, droite, bas, gauche)
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            if (visite[y][x]) continue;
            visite[y][x] = true;

            // Si on a atteint la destination
            if (x == endX && y == endY) {
                break;
            }

            // Vérifier chaque direction
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                // Vérifier si la nouvelle position est valide
                if (newX >= 0 && newX < largeur && newY >= 0 && newY < hauteur &&
                        grille.getElement(newY, newX) != Grille.ROCHER &&
                        grille.getElement(newY, newX) != Grille.LOUP && !visite[newY][newX]) {

                    // Calculer la nouvelle distance
                    int poids = getPoids(grille.getElement(newY, newX));
                    int newDist = distance[y][x] + poids;

                    // Si cette route est meilleure, mettre à jour la distance
                    if (newDist < distance[newY][newX]) {
                        distance[newY][newX] = newDist;
                        predecesseur[newY][newX][0] = x;
                        predecesseur[newY][newX][1] = y;
                        queue.add(new int[]{newX, newY, newDist});
                    }
                }
            }
        }

        // Reconstruire le chemin
        List<int[]> chemin = new ArrayList<>();
        if (distance[endY][endX] != Integer.MAX_VALUE) {
            int x = endX;
            int y = endY;

            // Ajouter tous les points du chemin, y compris le point de départ
            while (!(x == startX && y == startY)) {
                chemin.add(0, new int[]{x, y});
                int tempX = predecesseur[y][x][0];
                int tempY = predecesseur[y][x][1];
                x = tempX;
                y = tempY;
            }
            // Ajouter le point de départ
            chemin.add(0, new int[]{startX, startY});
        }

        return chemin.isEmpty() ? null : chemin;
    }

    private int getPoids(int element) {
        switch (element) {
            case Grille.HERBE: return 2;
            case Grille.MARGUERITE: return 1;
            case Grille.CACTUS: return 3;
            case Grille.LOUP: return 10;
            default: return 2;
        }
    }

    public List<int[]> trouverCheminIntelligent(int startX, int startY, int endX, int endY) {
        // Réinitialisation
        for (int i = 0; i < hauteur; i++) {
            Arrays.fill(distance[i], Integer.MAX_VALUE);
        }

        int[][][] predecesseur = new int[hauteur][largeur][2];
        boolean[][] visite = new boolean[hauteur][largeur];
        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        distance[startY][startX] = 0;
        queue.add(new int[]{startX, startY, 0});

        int[][] directions = {{0,-1},{1,0},{0,1},{-1,0}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1];

            if (visite[y][x]) continue;
            visite[y][x] = true;

            if (x == endX && y == endY) break;

            for (int[] dir : directions) {
                int newX = x + dir[0], newY = y + dir[1];

                if (newX >= 0 && newX < largeur && newY >= 0 && newY < hauteur &&
                        grille.getElement(newY, newX) != Grille.ROCHER && !visite[newY][newX]) {

                    int poids = getPoids(grille.getElement(newY, newX));
                    int newDist = distance[y][x] + poids;

                    if (newDist < distance[newY][newX]) {
                        distance[newY][newX] = newDist;
                        predecesseur[newY][newX][0] = x;
                        predecesseur[newY][newX][1] = y;
                        queue.add(new int[]{newX, newY, newDist});
                    }
                }
            }
        }

        List<int[]> chemin = new ArrayList<>();
        if (distance[endY][endX] != Integer.MAX_VALUE) {
            int x = endX, y = endY;
            while (!(x == startX && y == startY)) {
                chemin.add(0, new int[]{x, y});
                int tempX = predecesseur[y][x][0];
                int tempY = predecesseur[y][x][1];
                x = tempX;
                y = tempY;
            }
            chemin.add(0, new int[]{startX, startY});
        }

        return chemin.isEmpty() ? null : chemin;
    }
}