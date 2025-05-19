package com.groupesae.sae;

public class Loup extends Personnage {

    public Loup(int x, int y) {
        this.x = x;
        this.y = y;
        this.force = 3;
    }

    public Loup() {
        this.force = 3;
    }

    @Override
    public void deplacer(Grille grille, String direction) {
        int currentX = this.x;
        int currentY = this.y;
        int Element = grille.getElement(currentY, currentX);


        for (int i = 0; i < this.force; i++) {
            int nextX = currentX;
            int nextY = currentY;

            switch (direction) {
                case "haut":
                    if (grille.getElement(currentY - 1, currentX) == Grille.ROCHER) {
                        break;
                    } else {
                        nextY--;
                    }
                    nextY--;
                case "bas":
                    if (grille.getElement(currentY + 1, currentX) == Grille.ROCHER) {
                        break;
                    } else {
                        nextY++;
                    }
                case "gauche":
                    if (grille.getElement(currentY, currentX-1) == Grille.ROCHER) {
                        break;
                    } else {
                        nextX--;
                    }
                case "droite":
                    if (grille.getElement(currentY, currentX+1) == Grille.ROCHER) {
                        break;
                    } else {
                        nextX++;
                    }
                default:
                    grille.getGrille()[this.y][this.x] = Grille.LOUP;
                    return;
            }

            if (nextX >= grille.getX() || nextY < 0 || nextY >= grille.getY()) {
                break;
            }

            currentX = nextX;
            currentY = nextY;
        }

        this.x = currentX;
        this.y = currentY;
        grille.getGrille()[this.y][this.x] = Grille.LOUP;
    }
}