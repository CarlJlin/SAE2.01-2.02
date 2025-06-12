package com.groupesae.sae;

public class Loup extends Personnage {
    private boolean enChasse;
    private final int VISION = 5;

    public Loup(int x, int y) {
        this.x = x;
        this.y = y;
        this.force = 3;
        this.enChasse = false;
    }

    @Override
    public void deplacer(Grille grille, String direction, boolean automatique) {
        if (automatique) {
            deplacerAutomatiquement(grille);
        } else {
            deplacerManuellement(grille, direction);
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

        // Vérifier si le mouton est visible (distance Manhattan <= VISION)
        if (moutonX != -1 && moutonY != -1) {
            int distance = Math.abs(moutonX - x) + Math.abs(moutonY - y);
            if (distance <= VISION) {
                enChasse = true;
                poursuivreMouton(grille, moutonX, moutonY);
            } else {
                enChasse = false;
                deplacerAleatoirement(grille);
            }
        } else {
            enChasse = false;
            deplacerAleatoirement(grille);
        }
    }

    private void poursuivreMouton(Grille grille, int moutonX, int moutonY) {
        // Déterminer la direction pour se rapprocher du mouton
        int dx = Integer.compare(moutonX, x);
        int dy = Integer.compare(moutonY, y);

        // Priorité à la direction avec la plus grande différence
        if (Math.abs(dx) > Math.abs(dy)) {
            if (peutDeplacer(grille, dx, 0)) {
                deplacerAvecDirection(grille, dx, 0);
            } else if (peutDeplacer(grille, 0, dy)) {
                deplacerAvecDirection(grille, 0, dy);
            } else {
                deplacerAleatoirement(grille);
            }
        } else {
            if (peutDeplacer(grille, 0, dy)) {
                deplacerAvecDirection(grille, 0, dy);
            } else if (peutDeplacer(grille, dx, 0)) {
                deplacerAvecDirection(grille, dx, 0);
            } else {
                deplacerAleatoirement(grille);
            }
        }
    }

    private void deplacerAleatoirement(Grille grille) {
        // Directions possibles: haut, droite, bas, gauche
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        // Mélanger les directions pour un mouvement aléatoire
        java.util.List<int[]> directionsList = new java.util.ArrayList<>();
        for (int[] dir : directions) {
            directionsList.add(dir);
        }
        java.util.Collections.shuffle(directionsList);

        // Essayer chaque direction jusqu'à en trouver une valide
        for (int[] dir : directionsList) {
            if (peutDeplacer(grille, dir[0], dir[1])) {
                deplacerAvecDirection(grille, dir[0], dir[1]);
                return;
            }
        }
    }

    private boolean peutDeplacer(Grille grille, int dx, int dy) {
        for (int i = 1; i <= force; i++) {
            int newX = x + (dx * i);
            int newY = y + (dy * i);

            // Vérifier si on sort de la grille
            if (newX < 0 || newX >= grille.getX() || newY < 0 || newY >= grille.getY()) {
                return false;
            }

            // Vérifier si on rencontre un rocher
            if (grille.getElement(newY, newX) == Grille.ROCHER) {
                return false;
            }

            // Si on trouve le mouton, on peut se déplacer
            if (grille.getElement(newY, newX) == Grille.MOUTON) {
                return true;
            }
        }
        return true;
    }

    private void deplacerAvecDirection(Grille grille, int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        int currentX = x;
        int currentY = y;
        int casesParcourues = 0;
        boolean moutonAttrape = false;

        for (int i = 0; i < force; i++) {
            int nextX = currentX + dx;
            int nextY = currentY + dy;

            // Vérifier si on sort de la grille ou si on rencontre un rocher
            if (nextX < 0 || nextX >= grille.getX() || nextY < 0 || nextY >= grille.getY() ||
                    grille.getElement(nextY, nextX) == Grille.ROCHER) {
                break;
            }

            // Vérifier si on a attrapé le mouton
            if (grille.getElement(nextY, nextX) == Grille.MOUTON) {
                moutonAttrape = true;
                currentX = nextX;
                currentY = nextY;
                casesParcourues++;
                break;
            }

            currentX = nextX;
            currentY = nextY;
            casesParcourues++;
        }

        if (casesParcourues > 0) {
            int elementSousLoup = grille.getElement(y, x);
            if (elementSousLoup == Grille.LOUP) {
                elementSousLoup = Grille.HERBE;
            }

            // Mettre à jour la grille
            grille.getGrille()[y][x] = elementSousLoup;

            // Mettre à jour les coordonnées du loup
            x = currentX;
            y = currentY;

            // Placer le loup à sa nouvelle position
            grille.getGrille()[y][x] = Grille.LOUP;
        }
    }

    public boolean estEnChasse() {
        return enChasse;
    }
}