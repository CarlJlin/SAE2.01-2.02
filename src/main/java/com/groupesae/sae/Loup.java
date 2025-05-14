package com.groupesae.sae;

public class Loup extends Personnage{

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
                return false; // Direction invalide
        }

        // Vérifier si la nouvelle position est dans les limites de la grille
        if (newX < 0 || newX >= this.x || newY < 0 || newY >= this.y) {
            return false; // Déplacement en dehors de la grille
        }

        // Vérifier si la nouvelle position est un rocher
        if (this.grille[newY][newX] == ROCHER) {
            return false; // Déplacement sur un rocher
        }

        return true; // Déplacement valide
    }
    @Override
    public void deplacer(Grille grille, String direction){
        // Vérifier si le mouvement est valide
        if (isDeplacementValide(x, y, direction)) {
            // Mettre à jour la position du mouton
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
            System.out.println("Déplacement invalide !");
        }
        return;
    }
}
