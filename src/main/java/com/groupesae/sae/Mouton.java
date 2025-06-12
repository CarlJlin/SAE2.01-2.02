package com.groupesae.sae;

import java.util.List;

public class Mouton extends Personnage {
    private Dijkstra pathfinder;
    private List<int[]> cheminOptimal;

    public Mouton(int x, int y) {
        this.x = x;
        this.y = y;
        this.force = 2; // Force de déplacement par défaut du mouton
    }

    @Override
    public void deplacer(Grille grille, String direction, boolean automatique) {
        if (!automatique) {
            deplacerManuel(grille, direction);
        } else {
            deplacerAutomatique(grille);
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

    private void deplacerAutomatique(Grille grille) {
        if (pathfinder == null) {
            pathfinder = new Dijkstra(grille);
        }

        // Trouver le chemin optimal vers la sortie
        cheminOptimal = pathfinder.trouverCheminVersSortie(x, y);

        if (cheminOptimal.isEmpty()) {
            System.out.println("Aucun chemin trouvé pour le mouton");
            return;
        }

        // Prendre le premier pas du chemin
        int[] prochainePas = cheminOptimal.get(0);
        int dx = Integer.compare(prochainePas[0], x);
        int dy = Integer.compare(prochainePas[1], y);

        deplacerAvecDirection(grille, dx, dy);
    }

    private void deplacerAvecDirection(Grille grille, int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        int currentX = x;
        int currentY = y;
        int casesParcourues = 0;

        for (int i = 0; i < force; i++) {
            int nextX = currentX + dx;
            int nextY = currentY + dy;

            // Vérifier si on sort de la grille ou si on rencontre un rocher
            if (nextX < 0 || nextX >= grille.getX() || nextY < 0 || nextY >= grille.getY() ||
                    grille.getElement(nextY, nextX) == Grille.ROCHER) {
                break;
            }

            // Vérifier si on rencontre le loup
            if (grille.getElement(nextY, nextX) == Grille.LOUP) {
                // Le mouton est mangé par le loup
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
            // Sauvegarder le type de case que le mouton va manger
            int typeCaseMangee = grille.getElement(currentY, currentX);

            // Effacer l'ancienne position du mouton
            grille.getGrille()[y][x] = Grille.HERBE;

            // Mettre à jour la position du mouton
            x = currentX;
            y = currentY;
            grille.getGrille()[y][x] = Grille.MOUTON;

            // Mettre à jour la vitesse du mouton en fonction de ce qu'il a mangé
            switch (typeCaseMangee) {
                case Grille.HERBE:
                    force = 2;
                    break;
                case Grille.MARGUERITE:
                    force = 4;
                    break;
                case Grille.CACTUS:
                    force = 1;
                    break;
            }
        }
    }

    public boolean estArriveSortie(Grille grille) {
        return x == 0 || x == grille.getX() - 1 || y == 0 || y == grille.getY() - 1;
    }

    public int getForce() {
        return force;
    }

    public boolean estMange(Grille grille) {
        return grille.getElement(y, x) == Grille.LOUP;
    }
}