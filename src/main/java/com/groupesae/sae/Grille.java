package com.groupesae.sae;
import java.util.Scanner;

public class Grille {

    protected int[][] grille;
    protected int x;
    protected int y;

    protected static final int ROCHER = -1;
    protected static final int HERBE = 0;
    protected static final int MARGUERITE = 1;
    protected static final int CACTUS = 2;

    protected static final int MOUTON = 8;
    protected static final int LOUP = 9;

    public Grille(int x, int y, boolean graphique) {
        if (!graphique){
        this.x = x;
        this.y = y;
        this.grille = new int[this.y][this.x];
        genererGrille();
        chooseExit();
        chooseElements();
        } else {
            this.x = x;
            this.y = y;
            this.grille = new int[this.y][this.x];
            genererGrille();
        }

    }

    public Grille() {
        this(10, 10, false);
    }

    public int[][] getGrille() {
        return this.grille;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getElement(int i, int j) {
        return this.grille[i][j];
    }

    public void genererGrille() {
        for (int i = 0; i < this.y; i++) {
            for (int j = 0; j < this.x; j++) {
                if (i == 0 || i == this.y - 1 || j == 0 || j == this.x - 1) {
                    this.grille[i][j] = ROCHER;
                } else {
                    this.grille[i][j] = HERBE;
                }
            }
        }
    }

    public void chooseExit() {
        Scanner scanner = new Scanner(System.in);
        int x = -1, y = -1;
        boolean valide = false;
        while (!valide) {
            System.out.println("Choisissez les coordonnées de la sortie (sur le bord, hors coins) :");
            System.out.print("x (1 à " + this.x + "): ");
            x = scanner.nextInt();
            System.out.print("y (1 à " + this.y + "): ");
            y = scanner.nextInt();

            boolean surBord = (x == 1 || x == this.x || y == 1 || y == this.y);
            boolean pasCoin = !((x == 1 || x == this.x) && (y == 1 || y == this.y));
            boolean dansGrille = (x >= 1 && x <= this.x && y >= 1 && y <= this.y);

            if (dansGrille && surBord && pasCoin) {
                valide = true;
            } else {
                System.out.println("Coordonnées invalides. La sortie doit être sur un bord mais pas dans un coin.");
            }
        }
        this.grille[y - 1][x - 1] = HERBE;
    }

    public void chooseElements() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Pour chaque case intérieure, entrez l'élément à placer :");
        System.out.println("0 = Herbe, 1 = Marguerite, 2 = Cactus, 3 = Rocher, -1 = Quitter");
        placementElements:
        for (int i = 1; i < this.y - 1; i++) {
            for (int j = 1; j < this.x - 1; j++) {
                int choix;
                while (true) {
                    System.out.print("Case (" + (j + 1) + "," + (i + 1) + ") : ");
                    choix = scanner.nextInt();
                    if (choix == -1) {
                        break placementElements;
                    }
                    if (choix >= 0 && choix <= 3) {
                        break;
                    }
                    System.out.println("Choix invalide, recommencez.");
                }
                switch (choix){
                    case 0 :
                        grille[i][j] = HERBE;
                        break;
                    case 1 :
                        grille[i][j] = MARGUERITE;
                        break;
                    case 2 :
                        grille[i][j] = CACTUS;
                        break;
                    case 3:
                        grille[i][j] = ROCHER;
                        break;
                }
            }
        }
        System.out.println("Quelle coordonnées du mouton ?");
        afficherGrille();
        System.out.println("x (1 à " + this.x + "): ");
        int x = scanner.nextInt();
        System.out.println("y (1 à " + this.y + "): ");
        int y = scanner.nextInt();
        while (x < 1 || x > this.x || y < 1 || y > this.y) {
            System.out.println("Coordonnées invalides. Veuillez entrer des coordonnées valides.");
            System.out.print("x (1 à " + this.x + "): ");
            x = scanner.nextInt();
            System.out.print("y (1 à " + this.y + "): ");
            y = scanner.nextInt();
        }
        while (grille[y - 1][x - 1] == ROCHER) {
            System.out.println("Coordonnées invalides. Veuillez entrer des coordonnées valides.");
            System.out.print("x (1 à " + this.x + "): ");
            x = scanner.nextInt();
            System.out.print("y (1 à " + this.y + "): ");
            y = scanner.nextInt();
        }
        grille[y - 1][x - 1] = MOUTON;
        System.out.println("Quelle coordonnées du loup ?");
        afficherGrille();
        System.out.println("x (1 à " + this.x + "): ");
        x = scanner.nextInt();
        System.out.println("y (1 à " + this.y + "): ");
        y = scanner.nextInt();
        while (x < 1 || x > this.x || y < 1 || y > this.y) {
            System.out.println("Coordonnées invalides. Veuillez entrer des coordonnées valides.");
            System.out.print("x (1 à " + this.x + "): ");
            x = scanner.nextInt();
            System.out.print("y (1 à " + this.y + "): ");
            y = scanner.nextInt();
        }
        while (grille[y - 1][x - 1] == ROCHER || grille[y - 1][x - 1] == MOUTON) {
            System.out.println("Coordonnées invalides. Veuillez entrer des coordonnées valides.");
            System.out.print("x (1 à " + this.x + "): ");
            x = scanner.nextInt();
            System.out.print("y (1 à " + this.y + "): ");
            y = scanner.nextInt();
        }
        grille[y - 1][x - 1] = LOUP;
        System.out.println("Grille initialisée avec succès !");
    }

    public void afficherGrille() {
        System.out.print("+");
        for (int j = 0; j < x; j++) {
            System.out.print("---+");
        }
        System.out.println();

        for (int i = 0; i < y; i++) {
            System.out.print("|");
            for (int j = 0; j < x; j++) {
                System.out.printf("%3d|", grille[i][j]);
            }
            System.out.println();

            System.out.print("+");
            for (int j = 0; j < x; j++) {
                System.out.print("---+");
            }
            System.out.println();
        }
    }

    public void jouer() {
        Scanner scanner = new Scanner(System.in);
        boolean partieTerminee = false;

        int positionMoutonX = -1, positionMoutonY = -1;
        int positionLoupX = -1, positionLoupY = -1;

        for (int i = 0; i < getY(); i++) {
            for (int j = 0; j < getX(); j++) {
                if (getElement(i, j) == MOUTON) {
                    positionMoutonY = i;
                    positionMoutonX = j;
                } else if (getElement(i, j) == LOUP) {
                    positionLoupY = i;
                    positionLoupX = j;
                }
            }
        }

        int forceMouton = 2;
        int forceLoup = 3;

        int elementSousMouton = HERBE;
        int elementSousLoup = HERBE;
        boolean premierTourMouton = true;
        boolean premierTourLoup = true;

        System.out.println("Bienvenue dans le jeu du mouton et du loup !");
        System.out.println("Le mouton (8) doit atteindre la sortie sans être mangé par le loup (9).");
        System.out.println("Le mouton mange ce qu'il y a sur la case où il se déplace, et au prochain tour :");
        System.out.println("- S'il a mangé de l'herbe (0) : déplacement de 2 cases");
        System.out.println("- S'il a mangé une marguerite (1) : déplacement de 4 cases");
        System.out.println("- S'il a mangé un cactus (2) : déplacement d'une seule case");

        while (!partieTerminee) {
            System.out.println("\nÉtat actuel de la grille :");
            afficherGrille();

            boolean mouvementMoutonValide = false;
            while (!mouvementMoutonValide) {
                System.out.println("Tour du mouton (vitesse: " + forceMouton + " cases)");
                System.out.print("Entrez une direction (haut, bas, gauche, droite) : ");
                String directionMouton = scanner.nextLine().toLowerCase();

                int dx = 0, dy = 0;
                switch (directionMouton) {
                    case "haut": dy = -1; break;
                    case "bas": dy = 1; break;
                    case "gauche": dx = -1; break;
                    case "droite": dx = 1; break;
                    default:
                        System.out.println("Direction invalide pour le mouton ! Réessayez.");
                        continue;
                }

                int tempX = positionMoutonX, tempY = positionMoutonY;
                int casesParcourues = 0;
                for (int i = 0; i < forceMouton; i++) {
                    int nextX = tempX + dx;
                    int nextY = tempY + dy;
                    if (nextX < 0 || nextX >= getX() || nextY < 0 || nextY >= getY()
                            || getElement(nextY, nextX) == ROCHER) {
                        break;
                    }
                    tempX = nextX;
                    tempY = nextY;
                    casesParcourues++;
                }

                if (casesParcourues > 0) {
                    int typeCaseMangee = getElement(tempY, tempX);

                    if (premierTourMouton) {
                        grille[positionMoutonY][positionMoutonX] = HERBE;
                        premierTourMouton = false;
                    } else {
                        grille[positionMoutonY][positionMoutonX] = elementSousMouton;
                    }
                    elementSousMouton = typeCaseMangee;

                    grille[tempY][tempX] = MOUTON;
                    positionMoutonX = tempX;
                    positionMoutonY = tempY;

                    mouvementMoutonValide = true;

                    switch (typeCaseMangee) {
                        case HERBE:
                            System.out.println("Le mouton a mangé de l'herbe ! Au prochain tour, il se déplacera de 2 cases.");
                            forceMouton = 2;
                            break;
                        case MARGUERITE:
                            System.out.println("Le mouton a mangé une marguerite ! Au prochain tour, il se déplacera de 4 cases.");
                            forceMouton = 4;
                            break;
                        case CACTUS:
                            System.out.println("Le mouton a mangé un cactus ! Au prochain tour, il se déplacera d'une seule case.");
                            forceMouton = 1;
                            break;
                    }

                    if (positionMoutonX == 0 || positionMoutonX == getX() - 1 ||
                            positionMoutonY == 0 || positionMoutonY == getY() - 1) {
                        System.out.println("Le mouton a atteint la sortie ! VICTOIRE !");
                        partieTerminee = true;
                        break;
                    }
                } else {
                    System.out.println("Mouvement invalide pour le mouton ! Réessayez.");
                }
            }

            if (partieTerminee) {
                break;
            }

            boolean mouvementLoupValide = false;
            while (!mouvementLoupValide) {
                System.out.println("Tour du loup (vitesse: " + forceLoup + " cases)");
                System.out.print("Entrez une direction (haut, bas, gauche, droite) : ");
                String directionLoup = scanner.nextLine().toLowerCase();

                int dx = 0, dy = 0;
                switch (directionLoup) {
                    case "haut": dy = -1; break;
                    case "bas": dy = 1; break;
                    case "gauche": dx = -1; break;
                    case "droite": dx = 1; break;
                    default:
                        System.out.println("Direction invalide pour le loup ! Réessayez.");
                        continue;
                }

                int tempX = positionLoupX, tempY = positionLoupY;
                int casesParcourues = 0;
                boolean moutonAttrape = false;
                for (int i = 0; i < forceLoup; i++) {
                    int nextX = tempX + dx;
                    int nextY = tempY + dy;
                    if (nextX < 0 || nextX >= getX() || nextY < 0 || nextY >= getY()
                            || getElement(nextY, nextX) == ROCHER) {
                        break;
                    }
                    if (nextX == positionMoutonX && nextY == positionMoutonY) {
                        moutonAttrape = true;
                        tempX = nextX;
                        tempY = nextY;
                        casesParcourues++;
                        break;
                    }
                    tempX = nextX;
                    tempY = nextY;
                    casesParcourues++;
                }

                if (casesParcourues > 0) {
                    if (premierTourLoup) {
                        grille[positionLoupY][positionLoupX] = HERBE;
                        premierTourLoup = false;
                    } else {
                        grille[positionLoupY][positionLoupX] = elementSousLoup;
                    }
                    elementSousLoup = getElement(tempY, tempX);

                    grille[tempY][tempX] = LOUP;
                    positionLoupX = tempX;
                    positionLoupY = tempY;

                    mouvementLoupValide = true;

                    if (moutonAttrape || (positionLoupX == positionMoutonX && positionLoupY == positionMoutonY)) {
                        System.out.println("Le loup a attrapé le mouton ! DÉFAITE !");
                        partieTerminee = true;
                    }
                } else {
                    System.out.println("Mouvement invalide pour le loup ! Réessayez.");
                }
            }
        }

        System.out.println("\nÉtat final de la grille :");
        afficherGrille();
        System.out.println("Fin de la partie !");
    }
}