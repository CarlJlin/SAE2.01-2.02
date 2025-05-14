package com.groupesae.sae;

public abstract class Personnage extends Grille{

    protected int x;
    protected int y;
    protected int force;

    public abstract void deplacer(Grille grille, String direction);

    public abstract boolean isDeplacementValide(int x, int y, String direction);
}