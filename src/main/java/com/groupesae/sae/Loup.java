package com.groupesae.sae;

import java.util.*;

public class Loup extends Personnage {
    private boolean enChasse;
    private final int VISION = 5;
    private int forceRestante;
    private final int force;

    private int elementSousLoup = Grille.HERBE;
    private Random random = new Random();

    public Loup(int x, int y) {
        this.x = x;
        this.y = y;
        this.force = 3;
        this.forceRestante = this.force;
        this.enChasse = false;
    }

    @Override
    public void deplacer(Grille grille, String direction, boolean automatique) {
        if (forceRestante <= 0) return;

        if (automatique) {
            deplacerAutomatiquement(grille);
        } else {
            deplacerManuellement(grille, direction);
        }
        forceRestante--;
    }

    @Override
    public boolean aLigneDeVue(Grille grille, int cibleX, int cibleY) {
        // Vérifier d'abord la distance de Manhattan
        int distance = Math.abs(x - cibleX) + Math.abs(y - cibleY);
        if (distance > VISION) {
            return false; // Trop loin pour voir
        }

        // Vérifier la ligne de vue avec les obstacles
        int x0 = this.x;
        int y0 = this.y;
        int x1 = cibleX;
        int y1 = cibleY;

        // Algorithme de Bresenham pour tracer la ligne
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int currentX = x0;
        int currentY = y0;

        while (true) {
            // Si on est arrivé à la cible
            if (currentX == x1 && currentY == y1) {
                return true;
            }

            // Vérifier les obstacles (sauf sur la case de départ)
            if (!(currentX == x0 && currentY == y0)) {
                int element = grille.getElement(currentY, currentX);

                // Les rochers bloquent la vue
                if (element == Grille.ROCHER) {
                    return false;
                }
            }

            // Passer à la case suivante
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                currentX += sx;
            }
            if (e2 < dx) {
                err += dx;
                currentY += sy;
            }
        }
    }

    private void deplacerManuellement(Grille grille, String direction) {
        int dx = 0, dy = 0;

        switch (direction) {
            case "haut": dy = -1; break;
            case "bas": dy = 1; break;
            case "gauche": dx = -1; break;
            case "droite": dx = 1; break;
        }

        deplacerAvecDirection(grille, dx, dy);
    }

    private void deplacerAutomatiquement(Grille grille) {
        // Trouver la position du mouton
        int moutonX = -1, moutonY = -1;
        for (int i = 0; i < grille.getY(); i++) {
            for (int j = 0; j < grille.getX(); j++) {
                if (grille.getElement(i, j) == Grille.MOUTON) {
                    moutonY = i;
                    moutonX = j;
                    break;
                }
            }
            if (moutonX != -1) break;
        }

        if (moutonX != -1 && moutonY != -1) {
            // Vérifier si on voit le mouton (distance ET ligne de vue)
            boolean voitLeMouton = aLigneDeVue(grille, moutonX, moutonY);

            if (voitLeMouton) {
                // Mode chasse activé
                if (!enChasse) {
                    System.out.println("Le loup entre en mode chasse!");
                }
                enChasse = true;
                poursuivreMouton(grille, moutonX, moutonY);
            } else {
                // Mode aléatoire
                if (enChasse) {
                    System.out.println("Le loup perd le mouton de vue, retour au mode aléatoire");
                }
                enChasse = false;
                deplacerAleatoirement(grille);
            }
        } else {
            enChasse = false;
            deplacerAleatoirement(grille);
        }
    }

    private void poursuivreMouton(Grille grille, int moutonX, int moutonY) {
        // Utiliser un algorithme simple pour poursuivre le mouton
        // Choisir la direction qui rapproche le plus du mouton
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        int meilleurDx = 0, meilleurDy = 0;
        int meilleureDistance = Integer.MAX_VALUE;

        for (int[] dir : directions) {
            int nextX = x + dir[0];
            int nextY = y + dir[1];

            if (peutDeplacer(grille, dir[0], dir[1])) {
                int distance = Math.abs(nextX - moutonX) + Math.abs(nextY - moutonY);
                if (distance < meilleureDistance) {
                    meilleureDistance = distance;
                    meilleurDx = dir[0];
                    meilleurDy = dir[1];
                }
            }
        }

        if (meilleurDx != 0 || meilleurDy != 0) {
            deplacerAvecDirection(grille, meilleurDx, meilleurDy);
        } else {
            // Si aucune direction ne rapproche, se déplacer aléatoirement
            deplacerAleatoirement(grille);
        }
    }

    private void deplacerAleatoirement(Grille grille) {
        // Mélanger les directions pour un déplacement aléatoire
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        List<int[]> directionsList = Arrays.asList(directions);
        Collections.shuffle(directionsList, random);

        // Essayer chaque direction dans un ordre aléatoire
        for (int[] dir : directionsList) {
            if (peutDeplacer(grille, dir[0], dir[1])) {
                deplacerAvecDirection(grille, dir[0], dir[1]);
                return;
            }
        }
    }

    private boolean peutDeplacer(Grille grille, int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;

        if (newX < 0 || newX >= grille.getX() || newY < 0 || newY >= grille.getY()) {
            return false;
        }

        return grille.getElement(newY, newX) != Grille.ROCHER;
    }

    private void deplacerAvecDirection(Grille grille, int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        int nextX = x + dx;
        int nextY = y + dy;

        if (nextX < 0 || nextX >= grille.getX() || nextY < 0 || nextY >= grille.getY() ||
                grille.getElement(nextY, nextX) == Grille.ROCHER) {
            return;
        }

        // Sauvegarder l'élément sous le loup
        grille.getGrille()[y][x] = elementSousLoup;

        // Sauvegarder le nouvel élément
        int typeCase = grille.getElement(nextY, nextX);
        elementSousLoup = (typeCase == Grille.MOUTON) ? Grille.HERBE : typeCase;

        // Déplacer le loup
        x = nextX;
        y = nextY;
        grille.getGrille()[y][x] = Grille.LOUP;
    }

    public boolean aEncoreForce() {
        return forceRestante > 0;
    }

    public void reinitialiserForce() {
        this.forceRestante = this.force;
    }

    public boolean estEnChasse() {
        return enChasse;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}