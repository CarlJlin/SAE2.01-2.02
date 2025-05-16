package com.groupesae.sae;

public class Mouton extends Personnage{


    public Mouton(int x, int y) {
        this.x = x;
        this.y = y;
        this.force = 2;
    }

    @Override
    public void deplacer(Grille grille, String direction){
        if (isDeplacementValide(x, y, direction)) {
            switch (direction) {
                case "haut":
                    x-=this.force;
                    break;
                case "bas":
                    x+=this.force;
                    break;
                case "gauche":
                    y-=this.force;
                    break;
                case "droite":
                    y+=this.force;
                    break;
            }
        } else {
            System.out.println("DÃ©placement invalide !");
        }
        return;
    }
    @Override
    public boolean isDeplacementValide(int x, int y, String direction){
        int newX = x;
        int newY = y;

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
                return false;
        }

        if (newX < 0 || newX >= this.x || newY < 0 || newY >= this.y) {
            return false;
        }

        if (this.grille[newY][newX] == ROCHER) {
            return false;
        }

        return true;
    }


}
