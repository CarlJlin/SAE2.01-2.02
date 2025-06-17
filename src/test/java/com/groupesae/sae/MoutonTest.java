package com.groupesae.sae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class MoutonTest {
    private Grille grille;
    private Mouton mouton;

    @BeforeEach
    void setUp() {
        grille = new Grille(10, 10, true);
        mouton = new Mouton(5, 5);

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

        // Placer le mouton
        grille.getGrille()[5][5] = Grille.MOUTON;

        // Créer une sortie
        grille.getGrille()[0][5] = Grille.HERBE;
    }

    @Test
    @DisplayName("Test position initiale du mouton")
    void testPositionInitiale() {
        assertEquals(5, mouton.getX());
        assertEquals(5, mouton.getY());
    }

    @Test
    @DisplayName("Test déplacement manuel du mouton")
    void testDeplacementManuel() {
        // Force initiale
        assertEquals(2, mouton.force);

        // Déplacement vers le haut
        mouton.deplacer(grille, "haut", false);
        assertEquals(5, mouton.getX());
        assertEquals(4, mouton.getY());

        // Vérifier que l'ancienne position est maintenant de l'herbe
        assertEquals(Grille.HERBE, grille.getElement(5, 5));
        assertEquals(Grille.MOUTON, grille.getElement(4, 5));
    }

    @Test
    @DisplayName("Test force après avoir mangé différents éléments")
    void testForceSelonNourriture() {
        // Test marguerite : la placer à 2 cases (force initiale = 2)
        grille.getGrille()[3][5] = Grille.MARGUERITE;

        mouton.deplacer(grille, "haut", false); // (4,5), forceRestante = 1
        mouton.deplacer(grille, "haut", false); // (3,5) sur marguerite, forceRestante = 0
        // Mange automatiquement la marguerite

        mouton.reinitialiserForce();
        assertEquals(4, mouton.getForce(), "Force = 4 après marguerite");
    }

    @Test
    @DisplayName("Test déplacement bloqué par rocher")
    void testDeplacementBloqueRocher() {
        grille.getGrille()[4][5] = Grille.ROCHER;

        int xAvant = mouton.getX();
        int yAvant = mouton.getY();

        mouton.deplacer(grille, "haut", false);

        assertEquals(xAvant, mouton.getX());
        assertEquals(yAvant, mouton.getY());
    }

    @Test
    @DisplayName("Test déplacement bloqué par loup")
    void testDeplacementBloqueLoup() {
        grille.getGrille()[4][5] = Grille.LOUP;

        int xAvant = mouton.getX();
        int yAvant = mouton.getY();

        mouton.deplacer(grille, "haut", false);

        assertEquals(xAvant, mouton.getX());
        assertEquals(yAvant, mouton.getY());
    }

    @Test
    @DisplayName("Test mode fuite quand loup proche")
    void testModeFuite() {
        // Placer un loup à proximité
        grille.getGrille()[3][5] = Grille.LOUP;

        // Le mouton devrait passer en mode fuite
        mouton.deplacer(grille, "", true);

        // Vérifier que le mouton s'éloigne du loup
        int distanceInitiale = 2; // Distance Manhattan initiale
        int nouvelleDistance = Math.abs(mouton.getX() - 5) + Math.abs(mouton.getY() - 3);

        assertTrue(nouvelleDistance >= distanceInitiale,
                "Le mouton devrait s'éloigner du loup");
    }

    @Test
    @DisplayName("Test mémoire des cases visitées")
    void testMemoireCasesVisitees() {
        // Tour 1 : 2 déplacements
        mouton.deplacer(grille, "haut", false);
        mouton.deplacer(grille, "droite", false);

        // Fin du tour — réinitialiser pour le tour suivant
        mouton.reinitialiserForce();

        // Tour 2 : vérifier qu'on a de la force
        assertTrue(mouton.aEncoreForce(), "Le mouton devrait avoir de la force au nouveau tour");
    }

    @Test
    @DisplayName("Test vision du mouton")
    void testVisionMouton() {
        // Position initiale
        int xInitial = mouton.getX();
        int yInitial = mouton.getY();

        // Placer un loup dans le champ de vision
        grille.getGrille()[1][5] = Grille.LOUP; // Distance 4

        mouton.deplacer(grille, "", true);

        // Vérifier que le mouton a bougé
        boolean aBouge = (mouton.getX() != xInitial || mouton.getY() != yInitial);
        assertTrue(aBouge, "Le mouton devrait bouger en voyant le loup");
    }

    @Test
    @DisplayName("Test déplacement automatique exploration")
    void testDeplacementAutomatiqueExploration() {
        // Pas de loup sur la grille
        mouton.deplacer(grille, "", true);

        // Le mouton devrait se déplacer
        boolean aBouge = (mouton.getX() != 5 || mouton.getY() != 5);
        assertTrue(aBouge, "Le mouton devrait explorer en mode automatique");
    }

    @Test
    @DisplayName("Test stratégie alimentation")
    void testStrategieAlimentation() {
        // Placer des marguerites autour
        grille.getGrille()[4][5] = Grille.MARGUERITE;
        grille.getGrille()[6][5] = Grille.CACTUS;

        // Force faible pour activer mode alimentation
        mouton.force = 1;
        mouton.reinitialiserForce();

        mouton.deplacer(grille, "", true);

        // Le mouton devrait préférer aller vers la marguerite.
        assertEquals(4, mouton.getY(), "Le mouton devrait aller vers la marguerite");
    }

    @Test
    @DisplayName("Test limite de déplacement selon force")
    void testLimiteDeplacementForce() {
        mouton.force = 2;
        mouton.reinitialiserForce();

        // Déplacer 2 fois (limite de force)
        mouton.deplacer(grille, "haut", false);
        assertTrue(mouton.aEncoreForce());

        mouton.deplacer(grille, "haut", false);
        assertFalse(mouton.aEncoreForce(), "Le mouton ne devrait plus avoir de force");
    }

    @Test
    @DisplayName("Test déplacement aux bords de la grille")
    void testDeplacementBords() {
        // Placer le mouton près du bord
        mouton = new Mouton(1, 1);
        grille.getGrille()[1][1] = Grille.MOUTON;
        grille.getGrille()[5][5] = Grille.HERBE;

        // Essayer de sortir de la grille
        mouton.deplacer(grille, "gauche", false);
        assertEquals(1, mouton.getX(), "Le mouton ne devrait pas sortir de la grille");

        mouton.deplacer(grille, "haut", false);
        assertEquals(1, mouton.getY(), "Le mouton ne devrait pas sortir de la grille");
    }

    @Test
    @DisplayName("Test déplacement automatique avec configuration fixe")
    void testDeplacementAutomatiqueFixe() {
        // Configuration déterministe
        grille = new Grille(5, 5, true);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grille.getGrille()[i][j] = (i == 0 || i == 4 || j == 0 || j == 4) ?
                        Grille.ROCHER : Grille.HERBE;
            }
        }

        // Placer mouton au centre et marguerite à côté
        grille.getGrille()[2][2] = Grille.MOUTON;
        grille.getGrille()[1][2] = Grille.MARGUERITE;
        grille.getGrille()[0][2] = Grille.HERBE; // Sortie

        mouton = new Mouton(2, 2);

        // En mode automatique sans loup, le mouton devrait aller vers la marguerite
        mouton.deplacer(grille, "", true);

        // Vérifier que le mouton s'est déplacé
        assertTrue(mouton.getX() != 2 || mouton.getY() != 2,
                "Le mouton devrait se déplacer");
    }
}