package com.groupesae.sae;

public class Main {
    public static void main(String[] args){
        Grille grille = new Grille();

        for (int i = 0; i < grille.getX(); i++){
            for (int j = 0; j < grille.getY(); j++){
                System.out.print(grille.getElement(i, j) + " ");
            }
            System.out.println();
        }
    }
}
