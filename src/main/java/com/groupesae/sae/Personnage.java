package com.groupesae.sae;

public abstract class Personnage {
    protected int x;
    protected int y;
    protected int force;


    public abstract void deplacer(Grille grille, String direction);
}