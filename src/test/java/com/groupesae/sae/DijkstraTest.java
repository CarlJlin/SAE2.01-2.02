package com.groupesae.sae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class DijkstraTest {
    private Grille grille;
    private Dijkstra dijkstra;

    @BeforeEach
    void setUp() {
        // Créer une grille simple pour les tests
        grille = new Grille(7, 7, true);
        dijkstra = new Dijkstra(grille);

        // Initialiser la grille avec un chemin clair
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == 0 || i == 6 || j == 0 || j == 6) {
                    grille.getGrille()[i][j] = Grille.ROCHER;
                } else {
                    grille.getGrille()[i][j] = Grille.HERBE;
                }
            }
        }

        // Créer une sortie
        grille.getGrille()[0][3] = Grille.HERBE; // Sortie en haut
    }

    @Test
    @DisplayName("Test chemin direct vers la sortie")
    void testCheminDirect() {
        List<int[]> chemin = dijkstra.trouverCheminVersSortie(3, 3);

        assertNotNull(chemin);
        assertFalse(chemin.isEmpty());

        // Vérifier que le chemin se termine à la sortie
        int[] dernierPoint = chemin.getLast();
        assertEquals(3, dernierPoint[0]);
        assertEquals(0, dernierPoint[1]);
    }

    @Test
    @DisplayName("Test chemin avec obstacles")
    void testCheminAvecObstacles() {
        // S'assurer qu'il y a plusieurs chemins possibles
        // Ajouter une deuxième sortie
        grille.getGrille()[3][6] = Grille.HERBE; // Sortie à droite

        // Ajouter des obstacles qui ne bloquent pas complètement
        grille.getGrille()[1][3] = Grille.ROCHER;
        grille.getGrille()[2][3] = Grille.ROCHER;
        // Mais laisser un passage par le côté

        List<int[]> chemin = dijkstra.trouverCheminVersSortie(3, 3);

        assertNotNull(chemin);
        assertFalse(chemin.isEmpty(), "Un chemin devrait être trouvé");

        // Vérifier que le chemin contourne les obstacles
        for (int[] point : chemin) {
            assertNotEquals(Grille.ROCHER, grille.getElement(point[1], point[0]),
                    "Le chemin ne doit pas passer par des rochers");
        }
    }

    @Test
    @DisplayName("Test pas de chemin possible")
    void testPasDeCheminPossible() {
        // Bloquer toutes les sorties
        grille.getGrille()[0][3] = Grille.ROCHER;

        // Entourer le point de départ de rochers
        grille.getGrille()[2][2] = Grille.ROCHER;
        grille.getGrille()[2][3] = Grille.ROCHER;
        grille.getGrille()[2][4] = Grille.ROCHER;
        grille.getGrille()[3][2] = Grille.ROCHER;
        grille.getGrille()[3][4] = Grille.ROCHER;
        grille.getGrille()[4][2] = Grille.ROCHER;
        grille.getGrille()[4][3] = Grille.ROCHER;
        grille.getGrille()[4][4] = Grille.ROCHER;

        List<int[]> chemin = dijkstra.trouverCheminVersSortie(3, 3);

        assertNotNull(chemin);
        assertTrue(chemin.isEmpty());
    }

    @Test
    @DisplayName("Test poids marguerite vs cactus")
    void testPoidsMargueriteVsCactus() {
        // Créer un couloir avec deux chemins parallèles
        grille = new Grille(7, 5, true);

        // Initialiser avec des rochers partout
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                grille.getGrille()[i][j] = Grille.ROCHER;
            }
        }

        // Créer deux chemins parallèles
        // Chemin du haut : cactus
        grille.getGrille()[1][1] = Grille.HERBE;
        grille.getGrille()[1][2] = Grille.CACTUS;
        grille.getGrille()[1][3] = Grille.CACTUS;
        grille.getGrille()[1][4] = Grille.CACTUS;
        grille.getGrille()[1][5] = Grille.HERBE;

        // Chemin du bas : marguerites
        grille.getGrille()[3][1] = Grille.HERBE;
        grille.getGrille()[3][2] = Grille.MARGUERITE;
        grille.getGrille()[3][3] = Grille.MARGUERITE;
        grille.getGrille()[3][4] = Grille.MARGUERITE;
        grille.getGrille()[3][5] = Grille.HERBE;

        // Sortie
        grille.getGrille()[2][6] = Grille.HERBE;
        grille.getGrille()[1][6] = Grille.HERBE;
        grille.getGrille()[3][6] = Grille.HERBE;

        // Point de départ
        grille.getGrille()[2][1] = Grille.HERBE;

        dijkstra = new Dijkstra(grille);
        List<int[]> chemin = dijkstra.trouverCheminVersSortie(1, 2);

        assertNotNull(chemin);

        // Compter les marguerites et cactus sur le chemin
        int marguerites = 0;
        int cactus = 0;
        for (int[] point : chemin) {
            int element = grille.getElement(point[1], point[0]);
            if (element == Grille.MARGUERITE) marguerites++;
            if (element == Grille.CACTUS) cactus++;
        }

        assertTrue(marguerites > cactus,
                "Le chemin devrait passer par plus de marguerites que de cactus");
    }

    @Test
    @DisplayName("Test chemin intelligent avec évitement du loup")
    void testCheminIntelligent() {
        // Placer un loup
        grille.getGrille()[2][3] = Grille.LOUP;

        List<int[]> chemin = dijkstra.trouverCheminIntelligent(3, 5, 2, 3);

        assertNotNull(chemin);

        // Vérifier que le chemin évite le loup
        for (int[] point : chemin) {
            int distanceLoup = Math.abs(point[0] - 2) + Math.abs(point[1] - 3);
            assertTrue(distanceLoup > 0, "Le chemin ne doit pas passer par le loup");
        }
    }

    @Test
    @DisplayName("Test performance avec grande grille")
    void testPerformanceGrandeGrille() {
        Grille grandeGrille = new Grille(30, 30, true);
        grandeGrille.genererLabyrinthe();
        Dijkstra dijkstraGrand = new Dijkstra(grandeGrille);

        long debut = System.currentTimeMillis();
        List<int[]> chemin = dijkstraGrand.trouverCheminVersSortie(15, 15);
        long fin = System.currentTimeMillis();

        assertNotNull(chemin);
        assertTrue((fin - debut) < 1000, "L'algorithme doit s'exécuter en moins d'1 seconde");
    }

    @Test
    @DisplayName("Test départ sur la sortie")
    void testDepartSurSortie() {
        List<int[]> chemin = dijkstra.trouverCheminVersSortie(3, 1);

        assertNotNull(chemin);
        // Le chemin devrait être très court si on est près de la sortie.
        assertTrue(chemin.size() <= 2);
    }
}