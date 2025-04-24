package com.groupesae.sae;

public class Grille{

    private int[][] grille;
    private int x;
    private int y;


    public Grille(int x, int y){
        this.x=x;
        this.y=y;
        this.grille=new int[this.x][this.y];
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
}
