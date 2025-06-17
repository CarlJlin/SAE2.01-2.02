package com.groupesae.sae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {
    private Grille grille;
    private Mouton mouton;
    private Loup loup;

    @BeforeEach
    void setUp() {
        grille = new Grille(15, 15, true);
        grille.genererLabyrinthe();
    }

    @Test
    @DisplayName("Test scénario complet : mouton atteint la sortie")
    void testMoutonAtteintSortie() {
        // Trouver les positions du mouton et du loup
        int moutonX = -1, moutonY = -1;
        int loupX = -1, loupY = -1;

        for (int i = 0; i < grille.getY(); i++) {
            for (int j = 0; j < grille.getX(); j++) {
                if (grille.getElement(i, j) == Grille.MOUTON) {
                    moutonX = j;
                    moutonY = i;
                } else if (grille.getElement(i, j) == Grille.LOUP) {
                    loupX = j;
                    loupY = i;
                }
            }
        }

        assertTrue(moutonX != -1 && moutonY != -1, "Le mouton doit être présent");
        assertTrue(loupX != -1 && loupY != -1, "Le loup doit être présent");

        mouton = new Mouton(moutonX, moutonY);
        loup = new Loup(loupX, loupY);

        // Vérifier qu'il existe au moins une sortie
        assertFalse(grille.getSorties().isEmpty(), "Il doit y avoir au moins une sortie");
    }

    @Test
    @DisplayName("Test alternance des tours mouton/loup")
    void testAlternanceTours() {
        // Configuration basique
        grille = new Grille(10, 10, true);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille.getGrille()[i][j] = (i == 0 || i == 9 || j == 0 || j == 9) ?
                        Grille.ROCHER : Grille.HERBE;
            }
        }

        grille.getGrille()[5][5] = Grille.MOUTON;
        grille.getGrille()[7][7] = Grille.LOUP;
        grille.getGrille()[0][5] = Grille.HERBE; // Sortie

        mouton = new Mouton(5, 5);
        loup = new Loup(7, 7);

        // Tour du mouton (2 déplacements)
        mouton.reinitialiserForce();
        assertTrue(mouton.aEncoreForce());
        mouton.deplacer(grille, "haut", false);
        assertTrue(mouton.aEncoreForce(), "Devrait avoir encore 1 déplacement");
        mouton.deplacer(grille, "haut", false);
        assertFalse(mouton.aEncoreForce(), "Plus de force après 2 déplacements");

        // Tour du loup (3 déplacements)
        loup.reinitialiserForce();
        assertTrue(loup.aEncoreForce());
        for (int i = 0; i < 3; i++) {
            loup.deplacer(grille, "", true);
            if (i < 2) {
                assertTrue(loup.aEncoreForce(), "Le loup devrait encore avoir de la force");
            }
        }
        assertFalse(loup.aEncoreForce(), "Plus de force après 3 déplacements");
    }

    @Test
    @DisplayName("Test interaction mouton-marguerite")
    void testMoutonMangeMarguerite() {
        grille = new Grille(10, 10, true);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille.getGrille()[i][j] = (i == 0 || i == 9 || j == 0 || j == 9) ?
                        Grille.ROCHER : Grille.HERBE;
            }
        }

        // Placer le mouton et la marguerite à exactement 2 cases de distance
        grille.getGrille()[5][5] = Grille.MOUTON;
        grille.getGrille()[3][5] = Grille.MARGUERITE; // 2 cases plus haut

        mouton = new Mouton(5, 5);

        // Le mouton doit faire exactement 2 déplacements pour terminer sur la marguerite
        mouton.deplacer(grille, "haut", false); // Position (5,4), forceRestante = 1
        mouton.deplacer(grille, "haut", false); // Position (5,3) sur marguerite, forceRestante = 0

        // Vérifier que le mouton est bien sur l'ancienne position de la marguerite
        assertEquals(Grille.MOUTON, grille.getElement(3, 5));
        assertFalse(mouton.aEncoreForce(), "Le tour doit être terminé");

        // Au prochain tour, vérifier la force via le nombre de déplacements possibles
        mouton.reinitialiserForce();

        // Après avoir mangé une marguerite, le mouton devrait pouvoir faire 4 déplacements
        int deplacementsPossibles = 0;
        while (mouton.aEncoreForce()) {
            mouton.deplacer(grille, deplacementsPossibles % 2 == 0 ? "bas" : "gauche", false);
            deplacementsPossibles++;
        }

        assertEquals(4, deplacementsPossibles, "Le mouton devrait avoir pu faire 4 déplacements après avoir mangé une marguerite");
    }

    @Test
    @DisplayName("Test comportement loup-mouton proche")
    void testComportementLoupMoutonProche() {
        grille = new Grille(10, 10, true);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille.getGrille()[i][j] = (i == 0 || i == 9 || j == 0 || j == 9) ?
                        Grille.ROCHER : Grille.HERBE;
            }
        }

        // Placer mouton et loup proches
        grille.getGrille()[5][5] = Grille.MOUTON;
        grille.getGrille()[3][5] = Grille.LOUP; // Distance 2

        mouton = new Mouton(5, 5);
        loup = new Loup(3, 5);

        // Le mouton devrait fuir.
        int distanceAvant = Math.abs(mouton.getX() - loup.getX()) +
                Math.abs(mouton.getY() - loup.getY());

        mouton.deplacer(grille, "", true);

        int distanceApres = Math.abs(mouton.getX() - loup.getX()) +
                Math.abs(mouton.getY() - loup.getY());

        assertTrue(distanceApres >= distanceAvant, "Le mouton devrait s'éloigner du loup");

        // Le loup devrait poursuivre.
        loup.deplacer(grille, "", true);
        assertTrue(loup.estEnChasse(), "Le loup devrait être en mode chasse");
    }

    @Test
    @DisplayName("Test victoire du mouton")
    void testVictoireMouton() {
        grille = new Grille(5, 5, true);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grille.getGrille()[i][j] = (i == 0 || i == 4 || j == 0 || j == 4) ?
                        Grille.ROCHER : Grille.HERBE;
            }
        }

        grille.getGrille()[2][2] = Grille.MOUTON;
        grille.getGrille()[3][3] = Grille.LOUP;
        grille.getGrille()[0][2] = Grille.HERBE; // Sortie

        mouton = new Mouton(2, 2);

        // Déplacer le mouton vers la sortie (2 déplacements pour terminer le tour)
        mouton.deplacer(grille, "haut", false);
        mouton.deplacer(grille, "haut", false);

        // Vérifier que le mouton est sur une sortie
        boolean surSortie = (mouton.getX() == 2 && mouton.getY() == 0);
        assertTrue(surSortie, "Le mouton devrait être sur la sortie");
    }

    @Test
    @DisplayName("Test victoire du loup")
    void testVictoireLoup() {
        grille = new Grille(5, 5, true);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                grille.getGrille()[i][j] = (i == 0 || i == 4 || j == 0 || j == 4) ?
                        Grille.ROCHER : Grille.HERBE;
            }
        }

        grille.getGrille()[2][2] = Grille.MOUTON;
        grille.getGrille()[2][3] = Grille.LOUP;

        loup = new Loup(2, 3);

        // Le loup mange le mouton
        loup.deplacer(grille, "haut", false);

        assertEquals(2, loup.getX());
        assertEquals(2, loup.getY());
        assertEquals(Grille.LOUP, grille.getElement(2, 2));

        // Vérifier qu'il n'y a plus de mouton
        boolean moutonPresent = false;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (grille.getElement(i, j) == Grille.MOUTON) {
                    moutonPresent = true;
                }
            }
        }
        assertFalse(moutonPresent, "Le mouton devrait avoir été mangé");
    }

    @Test
    @DisplayName("Test labyrinthe complexe avec obstacles")
    void testLabyrintheComplexe() {
        grille = new Grille(20, 20, true);
        grille.genererLabyrintheImparfait(0.3);

        // Vérifier la structure du labyrinthe
        int rochers = 0;
        int passages = 0;
        int moutons = 0;
        int loups = 0;

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                switch (grille.getElement(i, j)) {
                    case Grille.ROCHER:
                        rochers++;
                        break;
                    case Grille.MOUTON:
                        moutons++;
                        break;
                    case Grille.LOUP:
                        loups++;
                        break;
                    default:
                        passages++;
                }
            }
        }

        assertTrue(rochers > 0, "Il doit y avoir des rochers");
        assertTrue(passages > 0, "Il doit y avoir des passages");
        assertEquals(1, moutons, "Il doit y avoir exactement un mouton");
        assertEquals(1, loups, "Il doit y avoir exactement un loup");

        // Vérifier qu'il y a au moins une sortie accessible
        assertFalse(grille.getSorties().isEmpty(), "Il doit y avoir au moins une sortie");
    }

    @Test
    @DisplayName("Test mouton mange cactus")
    void testMoutonMangeCactus() {
        grille = new Grille(10, 10, true);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grille.getGrille()[i][j] = (i == 0 || i == 9 || j == 0 || j == 9) ?
                        Grille.ROCHER : Grille.HERBE;
            }
        }

        // Placer le mouton et le cactus à 2 cases
        grille.getGrille()[5][5] = Grille.MOUTON;
        grille.getGrille()[3][5] = Grille.CACTUS;

        mouton = new Mouton(5, 5);

        // Faire 2 déplacements pour terminer sur le cactus
        mouton.deplacer(grille, "haut", false);
        mouton.deplacer(grille, "haut", false);

        assertFalse(mouton.aEncoreForce(), "Le tour doit être terminé");

        // Au prochain tour, le mouton ne devrait pouvoir faire qu'1 déplacement
        mouton.reinitialiserForce();

        int deplacementsPossibles = 0;
        while (mouton.aEncoreForce()) {
            mouton.deplacer(grille, "bas", false);
            deplacementsPossibles++;
        }

        assertEquals(1, deplacementsPossibles, "Le mouton ne devrait pouvoir faire qu'1 déplacement après avoir mangé un cactus");
    }

    @Test
    @DisplayName("Test cycle complet mouton-loup")
    void testCycleCompletMoutonLoup() {
        grille = new Grille(7, 7, true);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                grille.getGrille()[i][j] = (i == 0 || i == 6 || j == 0 || j == 6) ?
                        Grille.ROCHER : Grille.HERBE;
            }
        }

        // Configuration initiale
        grille.getGrille()[3][3] = Grille.MOUTON;
        grille.getGrille()[5][5] = Grille.LOUP;
        grille.getGrille()[1][3] = Grille.MARGUERITE;
        grille.getGrille()[0][3] = Grille.HERBE; // Sortie

        mouton = new Mouton(3, 3);
        loup = new Loup(5, 5);

        // Tour 1 : mouton va vers la marguerite
        mouton.reinitialiserForce();
        mouton.deplacer(grille, "haut", false);
        mouton.deplacer(grille, "haut", false);
        assertFalse(mouton.aEncoreForce());

        // Tour 1 : loup se rapproche
        loup.reinitialiserForce();
        for (int i = 0; i < 3; i++) {
            if (loup.aEncoreForce()) {
                loup.deplacer(grille, "", true);
            }
        }

        // Tour 2 : mouton avec force 4 peut atteindre la sortie
        mouton.reinitialiserForce();
        // Vérifier qu'il peut faire 4 déplacements
        int nbDeplacements = 0;
        while (mouton.aEncoreForce() && nbDeplacements < 4) {
            mouton.deplacer(grille, "haut", false);
            nbDeplacements++;
        }

        // Le mouton devrait être sur la sortie.
        assertEquals(0, mouton.getY(), "Le mouton devrait avoir atteint la sortie");
    }
}