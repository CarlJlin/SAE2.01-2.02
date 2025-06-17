package com.groupesae.sae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class LoupTest {
    private Grille grille;
    private Loup loup;

    @BeforeEach
    void setUp() {
        grille = new Grille(10, 10, true);
        loup = new Loup(5, 5);

        // Initialiser la grille
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 || i == 9 || j == 0 || j == 9) {
                    grille.getGrille()[i][j] = Grille.ROCHER;
                } else {
                    grille.getGrille()[i][j] = Grille.HERBE;
                }
            }
        }

        // Placer le loup
        grille.getGrille()[5][5] = Grille.LOUP;
    }

    @Test
    @DisplayName("Test position initiale du loup")
    void testPositionInitiale() {
        assertEquals(5, loup.getX());
        assertEquals(5, loup.getY());
    }

    @Test
    @DisplayName("Test force du loup")
    void testForceLoup() {
        loup.reinitialiserForce();
        assertTrue(loup.aEncoreForce());

        // Le loup a une force de 3.
        for (int i = 0; i < 3; i++) {
            assertTrue(loup.aEncoreForce());
            loup.deplacer(grille, "haut", false);
        }

        assertFalse(loup.aEncoreForce(), "Le loup ne devrait plus avoir de force après 3 déplacements");
    }

    @Test
    @DisplayName("Test déplacement manuel du loup")
    void testDeplacementManuel() {
        loup.deplacer(grille, "haut", false);
        assertEquals(5, loup.getX());
        assertEquals(4, loup.getY());

        loup.deplacer(grille, "droite", false);
        assertEquals(6, loup.getX());
        assertEquals(4, loup.getY());
    }

    @Test
    @DisplayName("Test mode chasse activé")
    void testModeChasse() {
        // Placer un mouton dans le champ de vision
        grille.getGrille()[3][5] = Grille.MOUTON; // Distance 2

        assertFalse(loup.estEnChasse(), "Le loup ne devrait pas être en chasse au départ");

        loup.deplacer(grille, "", true);

        assertTrue(loup.estEnChasse(), "Le loup devrait être en chasse avec le mouton visible");
    }

    @Test
    @DisplayName("Test vision du loup")
    void testVisionLoup() {
        // Vision du loup = 5 (distance Manhattan)

        // Mouton à distance 5 (limite de vision)
        grille.getGrille()[2][3] = Grille.MOUTON; // Distance = |5-3| + |5-2| = 2 + 3 = 5
        loup.deplacer(grille, "", true);
        assertTrue(loup.estEnChasse(), "Le loup devrait voir le mouton à distance 5");

        // Réinitialiser
        grille.getGrille()[2][3] = Grille.HERBE;
        loup = new Loup(5, 5);

        // Mouton à distance 6 (hors vision)
        grille.getGrille()[1][3] = Grille.MOUTON; // Distance = |5-3| + |5-1| = 2 + 4 = 6
        loup.deplacer(grille, "", true);
        assertFalse(loup.estEnChasse(), "Le loup ne devrait pas voir le mouton à distance 6");
    }

    @Test
    @DisplayName("Test poursuite du mouton")
    void testPoursuiteMouton() {
        // Placer un mouton proche
        grille.getGrille()[3][5] = Grille.MOUTON;

        int distanceInitiale = Math.abs(loup.getX() - 5) + Math.abs(loup.getY() - 3);

        loup.deplacer(grille, "", true);

        int nouvelleDistance = Math.abs(loup.getX() - 5) + Math.abs(loup.getY() - 3);

        assertTrue(nouvelleDistance < distanceInitiale,
                "Le loup devrait se rapprocher du mouton en mode chasse");
    }

    @Test
    @DisplayName("Test déplacement aléatoire sans mouton")
    void testDeplacementAleatoire() {
        // Pas de mouton sur la grille
        int xInitial = loup.getX();
        int yInitial = loup.getY();

        loup.deplacer(grille, "", true);

        // Le loup devrait bouger.
        boolean aBouge = (loup.getX() != xInitial || loup.getY() != yInitial);
        assertTrue(aBouge, "Le loup devrait se déplacer aléatoirement");

        assertFalse(loup.estEnChasse(), "Le loup ne devrait pas être en chasse sans mouton");
    }

    @Test
    @DisplayName("Test déplacement bloqué par rocher")
    void testDeplacementBloqueRocher() {
        // Entourer le loup de rochers
        grille.getGrille()[4][5] = Grille.ROCHER;
        grille.getGrille()[6][5] = Grille.ROCHER;
        grille.getGrille()[5][4] = Grille.ROCHER;
        grille.getGrille()[5][6] = Grille.ROCHER;

        int xAvant = loup.getX();
        int yAvant = loup.getY();

        loup.deplacer(grille, "haut", false);

        assertEquals(xAvant, loup.getX());
        assertEquals(yAvant, loup.getY());
    }

    @Test
    @DisplayName("Test le loup mange le mouton")
    void testLoupMangeMouton() {
        // Placer un mouton adjacent
        grille.getGrille()[4][5] = Grille.MOUTON;

        loup.deplacer(grille, "haut", false);

        assertEquals(4, loup.getY());
        assertEquals(Grille.LOUP, grille.getElement(4, 5));

        // Vérifier qu'il n'y a plus de mouton sur la grille
        boolean moutonPresent = false;
        for (int i = 0; i < grille.getY(); i++) {
            for (int j = 0; j < grille.getX(); j++) {
                if (grille.getElement(i, j) == Grille.MOUTON) {
                    moutonPresent = true;
                    break;
                }
            }
        }
        assertFalse(moutonPresent, "Le mouton devrait avoir été mangé");
    }

    @Test
    @DisplayName("Test pathfinding du loup vers le mouton")
    void testPathfindingVersLeMouton() {
        // Mouton visible avec obstacle sur le côté
        grille.getGrille()[3][5] = Grille.MOUTON;
        grille.getGrille()[4][4] = Grille.ROCHER; // Obstacle latéral

        loup.deplacer(grille, "", true);

        // Le loup voit le mouton (pas de rocher entre eux).
        assertTrue(loup.estEnChasse(), "Le loup devrait être en chasse");

        // Le loup devrait bouger vers le mouton.
        int distance = Math.abs(loup.getX() - 5) + Math.abs(loup.getY() - 3);
        assertTrue(distance < 2, "Le loup devrait se rapprocher");
    }

    @Test
    @DisplayName("Test sortie du mode chasse")
    void testSortieModeChasse() {
        // Placer un mouton visible
        grille.getGrille()[3][5] = Grille.MOUTON;

        // Activer le mode chasse
        loup.deplacer(grille, "", true);
        assertTrue(loup.estEnChasse());

        // Déplacer le mouton hors de portée
        grille.getGrille()[3][5] = Grille.HERBE;
        grille.getGrille()[1][1] = Grille.MOUTON; // Très loin

        // Le loup devrait sortir du mode chasse
        loup.deplacer(grille, "", true);
        assertFalse(loup.estEnChasse(), "Le loup devrait sortir du mode chasse");
    }
}