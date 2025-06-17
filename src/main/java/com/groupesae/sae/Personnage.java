package com.groupesae.sae;

public abstract class Personnage {
    protected int x;
    protected int y;
    protected int force;
    protected final int VISION = 5;

    public abstract void deplacer(Grille grille, String direction, boolean automatique);

    public abstract boolean aLigneDeVue(Grille grille, int cibleX, int cibleY);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getForce() {
        return force;
    }
}