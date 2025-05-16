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
    public boolean isDeplacementValide(int currentX, int currentY, String direction) {
        int newX = currentX;
        int newY = currentY;

        switch (direction) {
            case "haut":
                newY-=this.force;
                break;
            case "bas":
                newY+= this.force;
                break;
            case "gauche":
                newX-= this.force;
                break;
            case "droite":
                newX+= this.force;
                break;
            default:
                return false;
        }

        if (newX < 0 || newX >= grille[0].length || newY < 0 || newY >= grille.length) {
            return false;
        }

        return grille[newY][newX] != ROCHER;
    }

    @Override
    public void deplacer(Grille grille, String direction) {
        int casesDeplacees = 0;
        int currentX = this.x;
        int currentY = this.y;

        grille.getGrille()[currentY][currentX] = HERBE;

        while (casesDeplacees < this.force) {
            int newX = currentX;
            int newY = currentY;

            switch (direction) {
                case "haut":
                    newY-= this.force;
                    break;
                case "bas":
                    newY+= this.force;
                    break;
                case "gauche":
                    newX-= this.force;
                    break;
                case "droite":
                    newX+= this.force;
                    break;
                default:
                    grille.getGrille()[this.y][this.x] = LOUP;
                    return;
            }

            if (newX < 0 || newX >= grille.getX() || newY < 0 || newY >= grille.getY() ||
                    grille.getElement(newY, newX) == ROCHER) {
                break;
            }

            currentX = newX;
            currentY = currentY;
            casesDeplacees++;

            if (grille.getElement(currentY, currentX) == MOUTON) {
                System.out.println("Le loup a mangÃ© le mouton !");
            }
        }

        this.x = currentX;
        this.y = currentY;
        grille.getGrille()[this.y][this.x] = LOUP;
    }

    public boolean deplacer(Grille grille, String direction, int moutonX, int moutonY) {
        int casesDeplacees = 0;
        int currentX = this.x;
        int currentY = this.y;
        boolean aMangeLesMouton = false;

        grille.getGrille()[currentY][currentX] = HERBE;

        while (casesDeplacees < this.force) {
            int newX = currentX;
            int newY = currentY;

            switch (direction) {
                case "haut":
                    newY--;
                    break;
                case "bas":
                    newY++;
                    break;
                case "gauche":
                    newX--;
                    break;
                case "droite":
                    newX++;
                    break;
                default:
                    grille.getGrille()[this.y][this.x] = LOUP;
                    return false;
            }

            if (newX < 0 || newX >= grille.getX() || newY < 0 || newY >= grille.getY() ||
                    grille.getElement(newY, newX) == ROCHER) {
                break;
            }

            currentX = newX;
            currentY = newY;
            casesDeplacees++;

            if (currentX == moutonX && currentY == moutonY) {
                aMangeLesMouton = true;
                break;
            }
        }

        this.x = currentX;
        this.y = currentY;
        grille.getGrille()[this.y][this.x] = LOUP;

        return aMangeLesMouton;
    }
}
