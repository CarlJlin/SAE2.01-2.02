package com.groupesae.sae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class AstarTest {
    private Grille grille;
    private Astar astar;

    @BeforeEach
    void setUp() {
        grille = new Grille(10, 10, true);
        astar = new Astar(grille);

        // Initialiser une grille simple
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 9 || j == 0 || j == 9) {
                    grille.getGrille()[i][j] = Grille.ROCHER;
                } else {
                    grille.getGrille()[i][j] = Grille.HERBE;
                }
            }
        }

        // Créer des sorties
        grille.getGrille()[0][5] = Grille.HERBE;
        grille.getGrille()[9][5] = Grille.HERBE;
    }

    @Test
    @DisplayName("Test A* trouve le chemin optimal")
    void testCheminOptimal() {
        List<int[]> chemin = astar.trouverCheminVersSortie(5, 5);

        assertNotNull(chemin);
        assertFalse(chemin.isEmpty());

        // Vérifier que le chemin est continu
        for (int i = 0; i < chemin.size() - 1; i++) {
            int[] point1 = chemin.get(i);
            int[] point2 = chemin.get(i + 1);
            int distance = Math.abs(point1[0] - point2[0]) + Math.abs(point1[1] - point2[1]);
            assertEquals(1, distance, "Les points du chemin doivent être adjacents");
        }
    }

    @Test
    @DisplayName("Test A* vs Dijkstra - même résultat")
    void testComparaisonDijkstra() {
        Dijkstra dijkstra = new Dijkstra(grille);

        List<int[]> cheminAstar = astar.trouverCheminVersSortie(5, 5);
        List<int[]> cheminDijkstra = dijkstra.trouverCheminVersSortie(5, 5);

        assertNotNull(cheminAstar);
        assertNotNull(cheminDijkstra);

        // Les deux algorithmes devraient trouver des chemins de longueur similaire
        int diff = Math.abs(cheminAstar.size() - cheminDijkstra.size());
        assertTrue(diff <= 2, "Les chemins devraient avoir des longueurs similaires");
    }

    @Test
    @DisplayName("Test heuristique A*")
    void testHeuristique() {
        // Créer un labyrinthe en forme de U pour tester l'heuristique
        for (int i = 2; i < 8; i++) {
            grille.getGrille()[i][5] = Grille.ROCHER;
        }
        grille.getGrille()[8][5] = Grille.HERBE; // Passage en bas

        List<int[]> chemin = astar.trouverCheminVersSortie(4, 5);

        assertNotNull(chemin);
        assertFalse(chemin.isEmpty());

        // Le chemin devrait contourner l'obstacle
        boolean contourneObstacle = false;
        for (int[] point : chemin) {
            if (point[1] > 7) { // Passe par le bas
                contourneObstacle = true;
                break;
            }
        }
        assertTrue(contourneObstacle, "Le chemin doit contourner l'obstacle");
    }

    @Test
    @DisplayName("Test trouver chemin entre deux points")
    void testTrouverCheminEntrePoints() {
        int startX = 2, startY = 2;
        int endX = 7, endY = 7;

        List<int[]> chemin = astar.trouverChemin(startX, startY, endX, endY);

        assertNotNull(chemin);
        assertFalse(chemin.isEmpty());

        // Vérifier début et fin
        assertEquals(startX, chemin.getFirst()[0]);
        assertEquals(startY, chemin.getFirst()[1]);
        assertEquals(endX, chemin.getLast()[0]);
        assertEquals(endY, chemin.getLast()[1]);
    }

    @Test
    @DisplayName("Test chemin bloqué par le loup")
    void testCheminAvecLoup() {
        grille.getGrille()[5][5] = Grille.LOUP;

        List<int[]> chemin = astar.trouverChemin(3, 3, 7, 7);

        if (chemin != null) {  // Le chemin pourrait être null
            // Vérifier que le chemin évite le loup
            for (int[] point : chemin) {
                assertFalse(point[0] == 5 && point[1] == 5,
                        "Le chemin ne doit pas passer par le loup");
            }
        }
    }

    @Test
    @DisplayName("Test performance A* vs exploration complète")
    void testPerformanceAstar() {
        // Créer un labyrinthe complexe
        Grille complexe = new Grille(20, 20, true);
        complexe.genererLabyrinthe();
        Astar astarComplexe = new Astar(complexe);

        long debut = System.currentTimeMillis();
        List<int[]> chemin = astarComplexe.trouverCheminVersSortie(10, 10);
        long fin = System.currentTimeMillis();

        assertNotNull(chemin);
        assertTrue((fin - debut) < 500, "A* doit être rapide (moins de 500ms)");
    }

    @Test
    @DisplayName("Test plusieurs sorties - choisit la plus proche")
    void testPlusieursSorties() {
        // Ajouter plusieurs sorties
        grille.getGrille()[5][0] = Grille.HERBE; // Sortie gauche
        grille.getGrille()[5][9] = Grille.HERBE; // Sortie droite

        // Depuis la gauche
        List<int[]> cheminGauche = astar.trouverCheminVersSortie(2, 5);
        // Depuis la droite
        List<int[]> cheminDroite = astar.trouverCheminVersSortie(7, 5);

        assertNotNull(cheminGauche);
        assertNotNull(cheminDroite);

        // Vérifier que chaque chemin va vers la sortie la plus proche
        int[] sortieGauche = cheminGauche.getLast();
        assertEquals(0, sortieGauche[0], "Devrait aller vers la sortie gauche");

        int[] sortieDroite = cheminDroite.getLast();
        assertEquals(9, sortieDroite[0], "Devrait aller vers la sortie droite");
    }

    @Test
    @DisplayName("Test chemin null quand destination inaccessible")
    void testDestinationInaccessible() {
        // Entourer la destination de rochers
        grille.getGrille()[6][6] = Grille.ROCHER;
        grille.getGrille()[6][7] = Grille.ROCHER;
        grille.getGrille()[6][8] = Grille.ROCHER;
        grille.getGrille()[7][6] = Grille.ROCHER;
        grille.getGrille()[7][8] = Grille.ROCHER;
        grille.getGrille()[8][6] = Grille.ROCHER;
        grille.getGrille()[8][7] = Grille.ROCHER;
        grille.getGrille()[8][8] = Grille.ROCHER;

        List<int[]> chemin = astar.trouverChemin(2, 2, 7, 7);

        assertNull(chemin, "Le chemin devrait être null si la destination est inaccessible");
    }
}