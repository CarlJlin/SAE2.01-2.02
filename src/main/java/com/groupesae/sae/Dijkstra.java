package com.groupesae.sae;

import java.util.*;

public class Dijkstra {
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

        // Rechercher la position de la sortie
        int endX = -1, endY = -1;

        // Parcourir les bords pour trouver la sortie
        for (int i = 0; i < hauteur; i++) {
            if (i == 0 || i == hauteur - 1) {
                for (int j = 0; j < largeur; j++) {
                    if (grille.getElement(i, j) != Grille.ROCHER) {
                        endX = j;
                        endY = i;
                        break;
                    }
                }
            } else {
                if (grille.getElement(i, 0) != Grille.ROCHER) {
                    endX = 0;
                    endY = i;
                    break;
                }
                if (grille.getElement(i, largeur - 1) != Grille.ROCHER) {
                    endX = largeur - 1;
                    endY = i;
                    break;
                }
            }
        }

        // Si aucune sortie n'est trouvée, retourner une liste vide
        if (endX == -1 || endY == -1) {
            return new ArrayList<>();
        }

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

        // Reconstruire le chemin
        List<int[]> chemin = new ArrayList<>();
        if (distance[endY][endX] != Integer.MAX_VALUE) {
            int x = endX;
            int y = endY;

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

    // Attribuer un poids à chaque type de case pour l'algorithme
    private int getPoids(int element) {
        switch (element) {
            case Grille.HERBE: return 2;
            case Grille.MARGUERITE: return 1; // Préférable (permet de se déplacer plus loin)
            case Grille.CACTUS: return 4; // À éviter (ralentit le mouton)
            case Grille.LOUP: return 10; // À éviter absolument
            default: return 2;
        }
    }
}