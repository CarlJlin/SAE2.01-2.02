package com.groupesae.sae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GrilleTest {
    private Grille grille;

    @BeforeEach
    void setUp() {
        grille = new Grille(10, 10, true);
    }

    @Test
    @DisplayName("Test création de grille avec dimensions correctes")
    void testCreationGrille() {
        assertEquals(10, grille.getX());
        assertEquals(10, grille.getY());
        assertNotNull(grille.getGrille());
    }

    @Test
    @DisplayName("Test génération de labyrinthe")
    void testGenererLabyrinthe() {
        grille.genererLabyrinthe();

        // Vérifier les coins (toujours des rochers)
        assertEquals(Grille.ROCHER, grille.getElement(0, 0));
        assertEquals(Grille.ROCHER, grille.getElement(0, grille.getX() - 1));
        assertEquals(Grille.ROCHER, grille.getElement(grille.getY() - 1, 0));
        assertEquals(Grille.ROCHER, grille.getElement(grille.getY() - 1, grille.getX() - 1));

        // Vérifier qu'il y a au moins une sortie
        List<int[]> sorties = grille.getSorties();
        assertNotNull(sorties);
        assertFalse(sorties.isEmpty(), "Le labyrinthe doit avoir au moins une sortie");

        // Vérifier la présence du mouton et du loup
        boolean moutonTrouve = false;
        boolean loupTrouve = false;
        for (int i = 0; i < grille.getY(); i++) {
            for (int j = 0; j < grille.getX(); j++) {
                if (grille.getElement(i, j) == Grille.MOUTON) moutonTrouve = true;
                if (grille.getElement(i, j) == Grille.LOUP) loupTrouve = true;
            }
        }
        assertTrue(moutonTrouve, "Le mouton doit être placé");
        assertTrue(loupTrouve, "Le loup doit être placé");
    }

    @Test
    @DisplayName("Test génération de labyrinthe imparfait")
    void testGenererLabyrintheImparfait() {
        grille.genererLabyrintheImparfait(0.25);

        // Compter les passages pour vérifier qu'il y a plus de chemins
        int passages = 0;
        for (int i = 1; i < grille.getY() - 1; i++) {
            for (int j = 1; j < grille.getX() - 1; j++) {
                if (grille.getElement(i, j) != Grille.ROCHER) {
                    passages++;
                }
            }
        }
        assertTrue(passages > 0, "Le labyrinthe imparfait doit avoir des passages");
    }

    @Test
    @DisplayName("Test copie de grille")
    void testCopierGrille() {
        grille.genererLabyrinthe();
        Grille copie = grille.copier();

        assertEquals(grille.getX(), copie.getX());
        assertEquals(grille.getY(), copie.getY());

        // Vérifier que c'est une copie profonde
        for (int i = 0; i < grille.getY(); i++) {
            for (int j = 0; j < grille.getX(); j++) {
                assertEquals(grille.getElement(i, j), copie.getElement(i, j));
            }
        }

        // Modifier la copie ne doit pas affecter l'original
        copie.getGrille()[1][1] = Grille.CACTUS;
        assertNotEquals(grille.getElement(1, 1), copie.getElement(1, 1));
    }

    @Test
    @DisplayName("Test placement des éléments")
    void testPlacementElements() {
        grille.getGrille()[5][5] = Grille.MARGUERITE;
        assertEquals(Grille.MARGUERITE, grille.getElement(5, 5));

        grille.getGrille()[3][3] = Grille.CACTUS;
        assertEquals(Grille.CACTUS, grille.getElement(3, 3));

        grille.getGrille()[7][7] = Grille.MOUTON;
        assertEquals(Grille.MOUTON, grille.getElement(7, 7));
    }

    @Test
    @DisplayName("Test récupération des sorties")
    void testGetSorties() {
        // Créer une grille avec des sorties explicites
        grille.getGrille()[0][5] = Grille.HERBE; // Sortie en haut
        grille.getGrille()[9][5] = Grille.HERBE; // Sortie en bas
        grille.getGrille()[5][0] = Grille.HERBE; // Sortie à gauche
        grille.getGrille()[5][9] = Grille.HERBE; // Sortie à droite

        List<int[]> sorties = grille.getSorties();
        assertNotNull(sorties);
        assertTrue(sorties.size() >= 4, "Doit trouver au moins 4 sorties");

        // Vérifier que les sorties sont sur les bords
        for (int[] sortie : sorties) {
            int x = sortie[0];
            int y = sortie[1];
            boolean surBord = (x == 0 || x == grille.getX() - 1 ||
                    y == 0 || y == grille.getY() - 1);
            assertTrue(surBord, "Les sorties doivent être sur les bords");
        }
    }

    @Test
    @DisplayName("Test création de grille avec différentes dimensions")
    void testDifferentesDimensions() {
        // Test avec dimensions minimales supportées (5x5)
        Grille petiteGrille = new Grille(5, 5, true);
        assertEquals(5, petiteGrille.getX());
        assertEquals(5, petiteGrille.getY());
        assertNotNull(petiteGrille.getGrille());

        // Test avec dimensions moyennes
        Grille moyenneGrille = new Grille(15, 15, true);
        assertEquals(15, moyenneGrille.getX());
        assertEquals(15, moyenneGrille.getY());

        // Test avec grandes dimensions
        Grille grandeGrille = new Grille(30, 30, true);
        assertEquals(30, grandeGrille.getX());
        assertEquals(30, grandeGrille.getY());
    }
}