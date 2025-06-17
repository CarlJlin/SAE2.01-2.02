package com.groupesae.sae;

import java.util.*;

public class Mouton extends Personnage {
    private int forceRestante;
    private int elementSousMouton = Grille.HERBE;
    private Random random = new Random();
    private boolean enFuite = false;

    // Constantes
    public final int VISION = 5;
    private final int FORCE_HERBE = 2;
    private final int FORCE_MARGUERITE = 4;
    private final int FORCE_CACTUS = 1;

    // Pour le pathfinding en mode fuite
    private Dijkstra dijkstra;
    private Astar astar;
    private String algorithmeChoisi = "Dijkstra"; // Par défaut

    public Mouton(int x, int y) {
        this.x = x;
        this.y = y;
        this.force = FORCE_HERBE; // Force initiale
        this.forceRestante = this.force;
    }

    public void setAlgorithme(String algo) {
        this.algorithmeChoisi = algo;
    }

    @Override
    public void deplacer(Grille grille, String direction, boolean automatique) {
        if (forceRestante <= 0) return;

        int ancienX = x;
        int ancienY = y;

        if (automatique) {
            deplacerAutomatiquement(grille);
        } else {
            deplacerManuel(grille, direction);
        }

        if (x != ancienX || y != ancienY) {
            forceRestante--;

            if (forceRestante == 0) {
                mangerNourriture();
            }
        }
    }

    @Override
    public boolean aLigneDeVue(Grille grille, int cibleX, int cibleY) {
        // Vérifier d'abord la distance de Manhattan
        int distance = Math.abs(x - cibleX) + Math.abs(y - cibleY);
        if (distance > VISION) {
            return false;
        }

        // Utiliser l'algorithme de Bresenham pour tracer la ligne de vue
        int x0 = this.x;
        int y0 = this.y;
        int x1 = cibleX;
        int y1 = cibleY;

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int currentX = x0;
        int currentY = y0;

        while (true) {
            if (currentX == x1 && currentY == y1) {
                return true;
            }

            if (!(currentX == x0 && currentY == y0)) {
                int element = grille.getElement(currentY, currentX);

                // Les rochers bloquent la vue
                if (element == Grille.ROCHER) {
                    return false;
                }
            }

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

    private void deplacerAutomatiquement(Grille grille) {
        // Trouver la position du loup
        int loupX = -1, loupY = -1;
        for (int i = 0; i < grille.getY(); i++) {
            for (int j = 0; j < grille.getX(); j++) {
                if (grille.getElement(i, j) == Grille.LOUP) {
                    loupX = j;
                    loupY = i;
                    break;
                }
            }
            if (loupX != -1) break;
        }

        if (loupX != -1 && loupY != -1) {
            // Vérifier si on voit le loup
            boolean voitLeLoup = aLigneDeVue(grille, loupX, loupY);

            if (voitLeLoup) {
                // Mode fuite - utiliser le pathfinding pour aller vers la sortie
                if (!enFuite) {
                    System.out.println("Le mouton entre en mode fuite!");
                }
                enFuite = true;

                // Utiliser l'algorithme choisi pour trouver le chemin vers la sortie
                List<int[]> chemin = null;
                if ("A*".equals(algorithmeChoisi)) {
                    if (astar == null) astar = new Astar(grille);
                    chemin = astar.trouverCheminVersSortie(x, y);
                } else {
                    if (dijkstra == null) dijkstra = new Dijkstra(grille);
                    chemin = dijkstra.trouverCheminVersSortie(x, y);
                }

                if (chemin != null && !chemin.isEmpty()) {
                    // Prendre le premier pas du chemin
                    int[] prochainePas = chemin.get(0);
                    int dx = Integer.compare(prochainePas[0], x);
                    int dy = Integer.compare(prochainePas[1], y);

                    if (peutSeDeplacer(grille, x + dx, y + dy)) {
                        deplacerAvecDirection(grille, dx, dy);
                        return;
                    }
                }
                // Si pas de chemin ou chemin bloqué, fuir dans la direction opposée au loup
                int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
                int meilleurDx = 0, meilleurDy = 0;
                int meilleureDistance = -1;

                for (int[] dir : directions) {
                    int nextX = x + dir[0];
                    int nextY = y + dir[1];
                    if (peutSeDeplacer(grille, nextX, nextY)) {
                        int distance = Math.abs(nextX - loupX) + Math.abs(nextY - loupY);
                        if (distance > meilleureDistance) {
                            meilleureDistance = distance;
                            meilleurDx = dir[0];
                            meilleurDy = dir[1];
                        }
                    }
                }

                if (meilleurDx != 0 || meilleurDy != 0) {
                    deplacerAvecDirection(grille, meilleurDx, meilleurDy);
                }
            } else {
                // Pas de loup visible, exploration normale
                if (enFuite) {
                    System.out.println("Le mouton ne voit plus le loup, retour au mode exploration");
                }
                enFuite = false;
                deplacerAleatoirement(grille);
            }
        } else {
            // Pas de loup sur la grille
            enFuite = false;
            deplacerAleatoirement(grille);
        }
    }

    private void deplacerAleatoirement(Grille grille) {
        // Préférer les cases avec de la nourriture intéressante
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        List<int[]> directionsList = Arrays.asList(directions);
        Collections.shuffle(directionsList, random);

        // D'abord essayer de trouver une marguerite
        for (int[] dir : directionsList) {
            int nextX = x + dir[0];
            int nextY = y + dir[1];
            if (peutSeDeplacer(grille, nextX, nextY) &&
                    grille.getElement(nextY, nextX) == Grille.MARGUERITE) {
                deplacerAvecDirection(grille, dir[0], dir[1]);
                return;
            }
        }

        // Sinon chercher de l'herbe
        for (int[] dir : directionsList) {
            int nextX = x + dir[0];
            int nextY = y + dir[1];
            if (peutSeDeplacer(grille, nextX, nextY) &&
                    grille.getElement(nextY, nextX) == Grille.HERBE) {
                deplacerAvecDirection(grille, dir[0], dir[1]);
                return;
            }
        }

        // Si pas d'autre choix, accepter les cactus
        for (int[] dir : directionsList) {
            int nextX = x + dir[0];
            int nextY = y + dir[1];
            if (peutSeDeplacer(grille, nextX, nextY)) {
                deplacerAvecDirection(grille, dir[0], dir[1]);
                return;
            }
        }
    }



    private void deplacerManuel(Grille grille, String direction) {
        int dx = 0, dy = 0;
        switch (direction) {
            case "haut": dy = -1; break;
            case "bas": dy = 1; break;
            case "gauche": dx = -1; break;
            case "droite": dx = 1; break;
        }
        deplacerAvecDirection(grille, dx, dy);
    }

    private boolean deplacerAvecDirection(Grille grille, int dx, int dy) {
        if (dx == 0 && dy == 0) return false;

        int nextX = x + dx;
        int nextY = y + dy;

        if (!peutSeDeplacer(grille, nextX, nextY)) {
            return false;
        }

        // Restaurer l'élément précédent sur la case qu'on quitte
        grille.getGrille()[y][x] = elementSousMouton;

        // Sauvegarder le type de la case où on va
        int typeCase = grille.getElement(nextY, nextX);

        // Si on mange une marguerite ou un cactus, enregistrer pour la repousse
        if (typeCase == Grille.MARGUERITE || typeCase == Grille.CACTUS) {
            grille.enregistrerElementMange(nextX, nextY, typeCase);
            elementSousMouton = Grille.HERBE; // La case devient herbe temporairement
        } else {
            elementSousMouton = typeCase;
        }

        // Se déplacer
        x = nextX;
        y = nextY;
        grille.getGrille()[y][x] = Grille.MOUTON;

        return true;
    }

    private boolean peutSeDeplacer(Grille grille, int nextX, int nextY) {
        if (nextX < 0 || nextX >= grille.getX() || nextY < 0 || nextY >= grille.getY()) {
            return false;
        }
        int element = grille.getElement(nextY, nextX);
        return element != Grille.ROCHER && element != Grille.LOUP;
    }

    private void mangerNourriture() {
        switch (elementSousMouton) {
            case Grille.MARGUERITE:
                this.force = FORCE_MARGUERITE;
                break;
            case Grille.HERBE:
                this.force = FORCE_HERBE;
                break;
            case Grille.CACTUS:
                this.force = FORCE_CACTUS;
                break;
        }
    }

    public void reinitialiserForce() {
        this.forceRestante = this.force;
    }

    public boolean aEncoreForce() {
        return forceRestante > 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getForce() {
        return force;
    }

    public boolean estEnFuite() {
        return enFuite;
    }
}