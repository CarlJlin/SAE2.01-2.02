package com.groupesae.sae;
import java.util.Scanner;

public class Grille {

    private int[][] grille;
    private int x;
    private int y;

    private static final int ROCHER = -1;
    private static final int HERBE = 0;
    private static final int MARGUERITE = 1;
    private static final int CACTUS = 2;

    public Grille(int x, int y) {
        this.x = x;
        this.y = y;
        this.grille = new int[this.y][this.x];
        genererGrille();
        chooseExit();
        chooseElements();
    }

    public Grille() {
        this(10, 10);
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
        for (int i = 1; i < this.y - 1; i++) {
            for (int j = 1; j < this.x - 1; j++) {
                int choix;
                while (true) {
                    System.out.print("Case (" + (j + 1) + "," + (i + 1) + ") : ");
                    choix = scanner.nextInt();
                    if (choix == -1) {
                        return;
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

    }

    public void afficherGrille() {
        // Ligne supérieure
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

            // Ligne de séparation
            System.out.print("+");
            for (int j = 0; j < x; j++) {
                System.out.print("---+");
            }
            System.out.println();
        }
    }
}