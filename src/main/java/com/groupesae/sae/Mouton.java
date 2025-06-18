package com.groupesae.sae;

import java.util.*;

public class Mouton extends Personnage {
    private int forceRestante;
    private int elementSousMouton = Grille.HERBE;
    private Random random = new Random();
    private boolean enFuite = false;

    // Variable pour stocker ce qui sera mangé à la fin du déplacement
    private int elementAManger = Grille.HERBE;

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

        if (automatique) {
            deplacerAutomatiquement(grille);
        } else {
            deplacerManuel(grille, direction);
        }

        // Consommer toute la force restante d'un coup
        forceRestante = 0;
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
                    // Le chemin inclut la position actuelle, donc on regarde à partir de l'index 1
                    suivreCheminOptimal(grille, chemin);
                    return;
                }

                // Si pas de chemin optimal, fuir dans la direction opposée au loup
                fuirLoup(grille, loupX, loupY);
            } else {
                // Pas de loup visible, exploration normale
                if (enFuite) {
                    System.out.println("Le mouton ne voit plus le loup, retour au mode exploration");
                }
                enFuite = false;
                explorerAleatoirement(grille);
            }
        } else {
            // Pas de loup sur la grille
            enFuite = false;
            explorerAleatoirement(grille);
        }
    }

    private void suivreCheminOptimal(Grille grille, List<int[]> chemin) {
        // Déterminer jusqu'où on peut aller sur le chemin avec notre force
        int indexCible = Math.min(force, chemin.size() - 1);

        // Vérifier qu'on peut atteindre cette position sans obstacle
        boolean peutAtteindre = true;
        for (int i = 1; i <= indexCible; i++) {
            int[] pos = chemin.get(i);
            int element = grille.getElement(pos[1], pos[0]);
            if (element == Grille.ROCHER || element == Grille.LOUP) {
                indexCible = i - 1;
                peutAtteindre = false;
                break;
            }
        }

        if (indexCible > 0) {
            int[] positionCible = chemin.get(indexCible);
            deplacerVersPosition(grille, positionCible[0], positionCible[1]);
        } else if (!peutAtteindre) {
            // Si le chemin est bloqué, essayer une autre direction
            explorerAleatoirement(grille);
        }
    }

    private void deplacerVersPosition(Grille grille, int cibleX, int cibleY) {
        // Restaurer l'élément précédent sur la case de départ
        grille.getGrille()[y][x] = elementSousMouton;

        // Sauvegarder ce qu'on va manger sur la case d'arrivée
        elementAManger = grille.getElement(cibleY, cibleX);

        // Si on mange une marguerite ou un cactus, enregistrer pour la repousse
        if (elementAManger == Grille.MARGUERITE || elementAManger == Grille.CACTUS) {
            grille.enregistrerElementMange(cibleX, cibleY, elementAManger);
            elementSousMouton = Grille.HERBE; // La case devient herbe temporairement
        } else {
            elementSousMouton = elementAManger;
        }

        // Se déplacer à la position cible
        x = cibleX;
        y = cibleY;
        grille.getGrille()[y][x] = Grille.MOUTON;

        // Vérifier si on a atteint une sortie
        if ((x == 0 || x == grille.getX() - 1 || y == 0 || y == grille.getY() - 1) &&
                grille.getElement(y, x) != Grille.ROCHER) {
            System.out.println("Le mouton a atteint la sortie !");
        }
    }

    private void fuirLoup(Grille grille, int loupX, int loupY) {
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        int meilleurDx = 0, meilleurDy = 0;
        int meilleureDistance = -1;

        // Évaluer chaque direction
        for (int[] dir : directions) {
            int[] posFinal = simulerDeplacementComplet(grille, dir[0], dir[1]);
            if (posFinal != null) {
                int distance = Math.abs(posFinal[0] - loupX) + Math.abs(posFinal[1] - loupY);
                if (distance > meilleureDistance) {
                    meilleureDistance = distance;
                    meilleurDx = dir[0];
                    meilleurDy = dir[1];
                }
            }
        }

        if (meilleurDx != 0 || meilleurDy != 0) {
            deplacerAvecForce(grille, meilleurDx, meilleurDy);
        }
    }

    private void explorerAleatoirement(Grille grille) {
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        List<int[]> directionsList = Arrays.asList(directions);
        Collections.shuffle(directionsList, random);

        // Évaluer chaque direction selon ce qu'on peut manger
        int meilleurScore = -1;
        int meilleurDx = 0, meilleurDy = 0;

        for (int[] dir : directionsList) {
            int[] posFinal = simulerDeplacementComplet(grille, dir[0], dir[1]);
            if (posFinal != null) {
                int element = grille.getElement(posFinal[1], posFinal[0]);
                int score = 0;

                if (element == Grille.MARGUERITE) score = 3;
                else if (element == Grille.HERBE) score = 2;
                else if (element == Grille.CACTUS) score = 1;

                // Bonus si on se rapproche d'un bord (potentielle sortie)
                int distanceBord = Math.min(
                        Math.min(posFinal[0], grille.getX() - 1 - posFinal[0]),
                        Math.min(posFinal[1], grille.getY() - 1 - posFinal[1])
                );
                if (distanceBord == 0) score += 5; // Gros bonus si on atteint un bord

                if (score > meilleurScore) {
                    meilleurScore = score;
                    meilleurDx = dir[0];
                    meilleurDy = dir[1];
                }
            }
        }

        if (meilleurDx != 0 || meilleurDy != 0) {
            deplacerAvecForce(grille, meilleurDx, meilleurDy);
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
        deplacerAvecForce(grille, dx, dy);
    }

    // Méthode pour simuler où on arriverait en se déplaçant dans une direction
    private int[] simulerDeplacementComplet(Grille grille, int dx, int dy) {
        if (dx == 0 && dy == 0) return null;

        int tempX = x;
        int tempY = y;
        int distance = 0;

        // Simuler le déplacement sur toute la force
        while (distance < force) {
            int nextX = tempX + dx;
            int nextY = tempY + dy;

            // Vérifier les limites
            if (nextX < 0 || nextX >= grille.getX() ||
                    nextY < 0 || nextY >= grille.getY()) {
                break;
            }

            // Vérifier les obstacles (mais pas sur les bords qui peuvent être des sorties)
            int element = grille.getElement(nextY, nextX);
            if (element == Grille.ROCHER || element == Grille.LOUP) {
                break;
            }

            tempX = nextX;
            tempY = nextY;
            distance++;
        }

        // Si on n'a pas pu bouger du tout
        if (tempX == x && tempY == y) {
            return null;
        }

        return new int[]{tempX, tempY};
    }

    // Méthode pour se déplacer dans une direction avec toute la force disponible
    private void deplacerAvecForce(Grille grille, int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        // Restaurer l'élément précédent sur la case de départ
        grille.getGrille()[y][x] = elementSousMouton;

        int distance = 0;
        int dernierX = x;
        int dernierY = y;

        // Se déplacer dans la direction choisie jusqu'à la limite de la force
        while (distance < force) {
            int nextX = dernierX + dx;
            int nextY = dernierY + dy;

            // Vérifier si on peut continuer
            if (nextX < 0 || nextX >= grille.getX() ||
                    nextY < 0 || nextY >= grille.getY()) {
                break;
            }

            int element = grille.getElement(nextY, nextX);
            if (element == Grille.ROCHER || element == Grille.LOUP) {
                break;
            }

            dernierX = nextX;
            dernierY = nextY;
            distance++;
        }

        // Si on a pu se déplacer
        if (dernierX != x || dernierY != y) {
            // Sauvegarder ce qu'on mange sur la case d'arrivée
            elementAManger = grille.getElement(dernierY, dernierX);

            // Si on mange une marguerite ou un cactus, enregistrer pour la repousse
            if (elementAManger == Grille.MARGUERITE || elementAManger == Grille.CACTUS) {
                grille.enregistrerElementMange(dernierX, dernierY, elementAManger);
                elementSousMouton = Grille.HERBE; // La case devient herbe temporairement
            } else {
                elementSousMouton = elementAManger;
            }

            // Se déplacer à la position finale
            x = dernierX;
            y = dernierY;
            grille.getGrille()[y][x] = Grille.MOUTON;

            System.out.println("Le mouton s'est déplacé de " + distance + " case(s)");

            // Vérifier si on a atteint une sortie
            if ((x == 0 || x == grille.getX() - 1 || y == 0 || y == grille.getY() - 1) &&
                    grille.getElement(y, x) != Grille.ROCHER) {
                System.out.println("Le mouton a atteint la sortie !");
            }
        }
    }

    // Méthode publique pour déclencher le changement de force basé sur ce qui a été mangé
    public void finirTour() {
        // Ajuster la force basée sur ce qui a été mangé lors du dernier déplacement
        switch (elementAManger) {
            case Grille.MARGUERITE:
                this.force = FORCE_MARGUERITE;
                System.out.println("Le mouton a mangé une marguerite ! Au prochain tour, il se déplacera de 4 cases.");
                break;
            case Grille.HERBE:
                this.force = FORCE_HERBE;
                System.out.println("Le mouton a mangé de l'herbe ! Au prochain tour, il se déplacera de 2 cases.");
                break;
            case Grille.CACTUS:
                this.force = FORCE_CACTUS;
                System.out.println("Le mouton a mangé un cactus ! Au prochain tour, il se déplacera d'une seule case.");
                break;
            default:
                // Si elementAManger n'est pas défini, on considère qu'il a mangé de l'herbe
                this.force = FORCE_HERBE;
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