package com.groupesae.sae;


import java.util.Scanner;

public class Grille{

    private int[][] grille;
    private int x;
    private int y;

    private static final int ROCHER = -1;
    private static final int HERBE = 0;
    private static final int MARGUERITE = 1;
    private static final int CACTUS = 2;

    public Grille(int x, int y){
        this.x=x;
        this.y=y;
        this.grille=new int[this.x][this.y];
        genererGrille();
        chooseExit(10,9);
        chooseElements();
    }

    public Grille(){
        this(10,10);
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
        for (int i = 0; i < this.x; i++) {
            for (int j = 0; j < this.y; j++) {
                if (i == 0 || i == this.x - 1 || j == 0 || j == this.y - 1) {
                    this.grille[i][j] = ROCHER;
                } else {
                    this.grille[i][j] = HERBE;
                }
            }
        }
    }

    public void chooseExit(int x, int y) {
        if (x >= 0 && x <= this.x && y >= 0 && y <= this.y) {
            this.grille[x-1][y-1] = HERBE;
        }
    }

    public void chooseElements() {
        Scanner scanner = new Scanner(System.in);
        int x = 0;
        int y = 0;
        while (x == 0) {
            System.out.println("Veuillez choisir les coordonnées x de l'element :");
            x = scanner.nextInt();
            if (x < 0 || x >= this.x) {
                System.out.println("Mauvaises coordonnées veuillez réessayer");
            }
        }
        while (y == 0) {
            System.out.println("Veuillez choisir les coordonnées y de l'element :");
            y = scanner.nextInt();
            if (y < 0 || y >= this.y) {
                System.out.println("Mauvaises coordonnées veuillez réessayer");
            }
        }
        int choix = -10;
        while (choix < 0 || choix > 3) {
            System.out.println("Choisissez un élément :\n1.Herbe\n2. Marguerite\n3. Cactus");
            choix = scanner.nextInt();
            if (choix < 0 || choix > 3) {
                System.out.println("Mauvais choix veuillez réessayer");
            }
        }
            this.grille[x - 1][y - 1] = choix;
        }
    }