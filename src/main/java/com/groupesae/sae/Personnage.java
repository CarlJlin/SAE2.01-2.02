public abstract class Personnage {

    protected int x;
    protected int y;
    protected double force;

    public abstract void deplacer(Grille grille, String direction);
}