package com.groupesae.sae;
import java.util.*;

public class Grille {

    protected int[][] grille;
    protected int x;
    protected int y;

    protected static final int ROCHER = -1;
    protected static final int HERBE = 0;
    protected static final int MARGUERITE = 1;
    protected static final int CACTUS = 2;

    protected static final int MOUTON = 8;
    protected static final int LOUP = 9;

    private Map<String, Integer> tempsRepousse = new HashMap<>();
    private final int DELAI_REPOUSSE = 3; // tours

    protected boolean estGeneree = false;

    public Grille(int x, int y, boolean graphique) {
        if (!graphique){
            this.x = x;
            this.y = y;
            this.grille = new int[this.y][this.x];
            genererGrille();
            chooseExit();
            chooseElements();
        } else {
            this.x = x;
            this.y = y;
            this.grille = new int[this.y][this.x];
            genererLabyrinthe();
        }

    }

    public Grille() {
        this(10, 10, false);
    }

    public int[][] getGrille() {
        return this.grille;
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

    public void genererGrille() {
        for (int i = 0; i < this.y; i++) {
            for (int j = 0; j < this.x; j++) {
                if (i == 0 || i == this.y - 1 || j == 0 || j == this.x - 1) {
                    this.grille[i][j] = ROCHER;
                } else {
                    this.grille[i][j] = HERBE;
                }
            }
        }
    }

    public void chooseExit() {
        Scanner scanner = new Scanner(System.in);
        int x = -1, y = -1;
        boolean valide = false;
        while (!valide) {
            System.out.println("Choisissez les coordonnées de la sortie (sur le bord, hors coins) :");
            System.out.print("x (1 à " + this.x + "): ");
            x = scanner.nextInt();
            System.out.print("y (1 à " + this.y + "): ");
            y = scanner.nextInt();

            boolean surBord = (x == 1 || x == this.x || y == 1 || y == this.y);
            boolean pasCoin = !((x == 1 || x == this.x) && (y == 1 || y == this.y));
            boolean dansGrille = (x >= 1 && x <= this.x && y >= 1 && y <= this.y);

            if (dansGrille && surBord && pasCoin) {
                valide = true;
            } else {
                System.out.println("Coordonnées invalides. La sortie doit être sur un bord mais pas dans un coin.");
            }
        }
        this.grille[y - 1][x - 1] = HERBE;
    }

    public void chooseElements() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Pour chaque case intérieure, entrez l'élément à placer :");
        System.out.println("0 = Herbe, 1 = Marguerite, 2 = Cactus, 3 = Rocher, -1 = Quitter");
        placementElements:
        for (int i = 1; i < this.y - 1; i++) {
            for (int j = 1; j < this.x - 1; j++) {
                int choix;
                while (true) {
                    System.out.print("Case (" + (j + 1) + "," + (i + 1) + ") : ");
                    choix = scanner.nextInt();
                    if (choix == -1) {
                        break placementElements;
                    }
                    if (choix >= 0 && choix <= 3) {
                        break;
                    }
                    System.out.println("Choix invalide, recommencez.");
                }
                switch (choix){
                    case 0 :
                        grille[i][j] = HERBE;
                        break;
                    case 1 :
                        grille[i][j] = MARGUERITE;
                        break;
                    case 2 :
                        grille[i][j] = CACTUS;
                        break;
                    case 3:
                        grille[i][j] = ROCHER;
                        break;
                }
            }
        }
        System.out.println("Quelle coordonnées du mouton ?");
        afficherGrille();
        System.out.println("x (1 à " + this.x + "): ");
        int x = scanner.nextInt();
        System.out.println("y (1 à " + this.y + "): ");
        int y = scanner.nextInt();
        while (x < 1 || x > this.x || y < 1 || y > this.y) {
            System.out.println("Coordonnées invalides. Veuillez entrer des coordonnées valides.");
            System.out.print("x (1 à " + this.x + "): ");
            x = scanner.nextInt();
            System.out.print("y (1 à " + this.y + "): ");
            y = scanner.nextInt();
        }
        while (grille[y - 1][x - 1] == ROCHER) {
            System.out.println("Coordonnées invalides. Veuillez entrer des coordonnées valides.");
            System.out.print("x (1 à " + this.x + "): ");
            x = scanner.nextInt();
            System.out.print("y (1 à " + this.y + "): ");
            y = scanner.nextInt();
        }
        grille[y - 1][x - 1] = MOUTON;
        System.out.println("Quelle coordonnées du loup ?");
        afficherGrille();
        System.out.println("x (1 à " + this.x + "): ");
        x = scanner.nextInt();
        System.out.println("y (1 à " + this.y + "): ");
        y = scanner.nextInt();
        while (x < 1 || x > this.x || y < 1 || y > this.y) {
            System.out.println("Coordonnées invalides. Veuillez entrer des coordonnées valides.");
            System.out.print("x (1 à " + this.x + "): ");
            x = scanner.nextInt();
            System.out.print("y (1 à " + this.y + "): ");
            y = scanner.nextInt();
        }
        while (grille[y - 1][x - 1] == ROCHER || grille[y - 1][x - 1] == MOUTON) {
            System.out.println("Coordonnées invalides. Veuillez entrer des coordonnées valides.");
            System.out.print("x (1 à " + this.x + "): ");
            x = scanner.nextInt();
            System.out.print("y (1 à " + this.y + "): ");
            y = scanner.nextInt();
        }
        grille[y - 1][x - 1] = LOUP;
        System.out.println("Grille initialisée avec succès !");
    }

    public void afficherGrille() {
        System.out.print("+");
        for (int j = 0; j < x; j++) {
            System.out.print("---+");
        }
        System.out.println();

        for (int i = 0; i < y; i++) {
            System.out.print("|");
            for (int j = 0; j < x; j++) {
                System.out.printf("%3d|", grille[i][j]);
            }
            System.out.println();

            System.out.print("+");
            for (int j = 0; j < x; j++) {
                System.out.print("---+");
            }
            System.out.println();
        }
    }

    public void jouer() {
        Scanner scanner = new Scanner(System.in);
        boolean partieTerminee = false;

        int positionMoutonX = -1, positionMoutonY = -1;
        int positionLoupX = -1, positionLoupY = -1;

        for (int i = 0; i < getY(); i++) {
            for (int j = 0; j < getX(); j++) {
                if (getElement(i, j) == MOUTON) {
                    positionMoutonY = i;
                    positionMoutonX = j;
                } else if (getElement(i, j) == LOUP) {
                    positionLoupY = i;
                    positionLoupX = j;
                }
            }
        }

        int forceMouton = 2;
        int forceLoup = 3;

        int elementSousMouton = HERBE;
        int elementSousLoup = HERBE;
        boolean premierTourMouton = true;
        boolean premierTourLoup = true;

        System.out.println("Bienvenue dans le jeu du mouton et du loup !");
        System.out.println("Le mouton (8) doit atteindre la sortie sans être mangé par le loup (9).");
        System.out.println("Le mouton mange ce qu'il y a sur la case où il se déplace, et au prochain tour :");
        System.out.println("- S'il a mangé de l'herbe (0) : déplacement de 2 cases");
        System.out.println("- S'il a mangé une marguerite (1) : déplacement de 4 cases");
        System.out.println("- S'il a mangé un cactus (2) : déplacement d'une seule case");

        while (!partieTerminee) {
            System.out.println("\nÉtat actuel de la grille :");
            afficherGrille();

            boolean mouvementMoutonValide = false;
            while (!mouvementMoutonValide) {
                System.out.println("Tour du mouton (vitesse: " + forceMouton + " cases)");
                System.out.print("Entrez une direction (haut, bas, gauche, droite) : ");
                String directionMouton = scanner.nextLine().toLowerCase();

                int dx = 0, dy = 0;
                switch (directionMouton) {
                    case "haut": dy = -1; break;
                    case "bas": dy = 1; break;
                    case "gauche": dx = -1; break;
                    case "droite": dx = 1; break;
                    default:
                        System.out.println("Direction invalide pour le mouton ! Réessayez.");
                        continue;
                }

                int tempX = positionMoutonX, tempY = positionMoutonY;
                int casesParcourues = 0;
                for (int i = 0; i < forceMouton; i++) {
                    int nextX = tempX + dx;
                    int nextY = tempY + dy;
                    if (nextX < 0 || nextX >= getX() || nextY < 0 || nextY >= getY()
                            || getElement(nextY, nextX) == ROCHER) {
                        break;
                    }
                    tempX = nextX;
                    tempY = nextY;
                    casesParcourues++;
                }

                if (casesParcourues > 0) {
                    int typeCaseMangee = getElement(tempY, tempX);

                    if (premierTourMouton) {
                        grille[positionMoutonY][positionMoutonX] = HERBE;
                        premierTourMouton = false;
                    } else {
                        grille[positionMoutonY][positionMoutonX] = elementSousMouton;
                    }
                    elementSousMouton = typeCaseMangee;

                    grille[tempY][tempX] = MOUTON;
                    positionMoutonX = tempX;
                    positionMoutonY = tempY;

                    mouvementMoutonValide = true;

                    switch (typeCaseMangee) {
                        case HERBE:
                            System.out.println("Le mouton a mangé de l'herbe ! Au prochain tour, il se déplacera de 2 cases.");
                            forceMouton = 2;
                            break;
                        case MARGUERITE:
                            System.out.println("Le mouton a mangé une marguerite ! Au prochain tour, il se déplacera de 4 cases.");
                            forceMouton = 4;
                            break;
                        case CACTUS:
                            System.out.println("Le mouton a mangé un cactus ! Au prochain tour, il se déplacera d'une seule case.");
                            forceMouton = 1;
                            break;
                    }

                    if (positionMoutonX == 0 || positionMoutonX == getX() - 1 ||
                            positionMoutonY == 0 || positionMoutonY == getY() - 1) {
                        System.out.println("Le mouton a atteint la sortie ! VICTOIRE !");
                        partieTerminee = true;
                        break;
                    }
                } else {
                    System.out.println("Mouvement invalide pour le mouton ! Réessayez.");
                }
            }

            if (partieTerminee) {
                break;
            }

            boolean mouvementLoupValide = false;
            while (!mouvementLoupValide) {
                System.out.println("Tour du loup (vitesse: " + forceLoup + " cases)");
                System.out.print("Entrez une direction (haut, bas, gauche, droite) : ");
                String directionLoup = scanner.nextLine().toLowerCase();

                int dx = 0, dy = 0;
                switch (directionLoup) {
                    case "haut": dy = -1; break;
                    case "bas": dy = 1; break;
                    case "gauche": dx = -1; break;
                    case "droite": dx = 1; break;
                    default:
                        System.out.println("Direction invalide pour le loup ! Réessayez.");
                        continue;
                }

                int tempX = positionLoupX, tempY = positionLoupY;
                int casesParcourues = 0;
                boolean moutonAttrape = false;
                for (int i = 0; i < forceLoup; i++) {
                    int nextX = tempX + dx;
                    int nextY = tempY + dy;
                    if (nextX < 0 || nextX >= getX() || nextY < 0 || nextY >= getY()
                            || getElement(nextY, nextX) == ROCHER) {
                        break;
                    }
                    if (nextX == positionMoutonX && nextY == positionMoutonY) {
                        moutonAttrape = true;
                        tempX = nextX;
                        tempY = nextY;
                        casesParcourues++;
                        break;
                    }
                    tempX = nextX;
                    tempY = nextY;
                    casesParcourues++;
                }

                if (casesParcourues > 0) {
                    if (premierTourLoup) {
                        grille[positionLoupY][positionLoupX] = HERBE;
                        premierTourLoup = false;
                    } else {
                        grille[positionLoupY][positionLoupX] = elementSousLoup;
                    }
                    elementSousLoup = getElement(tempY, tempX);

                    grille[tempY][tempX] = LOUP;
                    positionLoupX = tempX;
                    positionLoupY = tempY;

                    mouvementLoupValide = true;

                    if (moutonAttrape || (positionLoupX == positionMoutonX && positionLoupY == positionMoutonY)) {
                        System.out.println("Le loup a attrapé le mouton ! DÉFAITE !");
                        partieTerminee = true;
                    }
                } else {
                    System.out.println("Mouvement invalide pour le loup ! Réessayez.");
                }
            }
        }

        System.out.println("\nÉtat final de la grille :");
        afficherGrille();
        System.out.println("Fin de la partie !");
    }

    public void genererLabyrinthe() {
        // Remplir la grille avec des rochers
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                grille[i][j] = ROCHER;
            }
        }

        // Initialisation pour l'algorithme de fusion aléatoire
        int cellRows = (y - 1) / 2;
        int cellCols = (x - 1) / 2;

        // Créer un tableau de taille correcte pour éviter les dépassements d'indices
        int totalCells = cellRows * cellCols;
        int[] parent = new int[totalCells];
        Arrays.fill(parent, -1);

        List<int[]> murs = new ArrayList<>();

        // Création des cellules et identification des murs
        for (int i = 1; i < y - 1; i += 2) {
            for (int j = 1; j < x - 1; j += 2) {
                // Créer une cellule (passage)
                grille[i][j] = HERBE;

                // Identifier les murs voisins (droite et bas seulement)
                if (j + 2 < x - 1) { // Mur à droite
                    murs.add(new int[]{i, j + 1});
                }
                if (i + 2 < y - 1) { // Mur en bas
                    murs.add(new int[]{i + 1, j});
                }
            }
        }

        // Mélanger les murs pour la sélection aléatoire
        Collections.shuffle(murs);

        // Fonction pour trouver la racine d'un ensemble
        // (pour l'algorithme de fusion)
        class DisjointSet {
            int find(int[] parent, int x) {
                if (parent[x] == -1) return x;
                return parent[x] = find(parent, parent[x]); // Compression de chemin
            }

            void union(int[] parent, int x, int y) {
                int rootX = find(parent, x);
                int rootY = find(parent, y);
                if (rootX != rootY) {
                    parent[rootX] = rootY;
                }
            }
        }

        DisjointSet ds = new DisjointSet();

        // Algorithme de fusion pour générer le labyrinthe parfait
        for (int[] mur : murs) {
            int i = mur[0];
            int j = mur[1];

            // Trouver les cellules adjacentes à ce mur
            int[] cell1 = new int[2];
            int[] cell2 = new int[2];

            if (i % 2 == 0) { // Mur horizontal
                cell1[0] = i - 1;
                cell1[1] = j;
                cell2[0] = i + 1;
                cell2[1] = j;
            } else { // Mur vertical
                cell1[0] = i;
                cell1[1] = j - 1;
                cell2[0] = i;
                cell2[1] = j + 1;
            }

            // Vérifier que les cellules sont dans les limites
            if (cell1[0] < 0 || cell1[0] >= y || cell1[1] < 0 || cell1[1] >= x ||
                    cell2[0] < 0 || cell2[0] >= y || cell2[1] < 0 || cell2[1] >= x) {
                continue;
            }

            // Calculer les indices des cellules dans le tableau parent
            int id1 = ((cell1[0] - 1) / 2) * cellCols + ((cell1[1] - 1) / 2);
            int id2 = ((cell2[0] - 1) / 2) * cellCols + ((cell2[1] - 1) / 2);

            // Vérifier que les indices sont dans les limites
            if (id1 < 0 || id1 >= totalCells || id2 < 0 || id2 >= totalCells) {
                continue;
            }

            // Si les cellules ne sont pas déjà connectées, abattre le mur
            if (ds.find(parent, id1) != ds.find(parent, id2)) {
                grille[i][j] = HERBE; // Abattre le mur
                ds.union(parent, id1, id2); // Fusionner les ensembles
            }
        }

        placerSortie();
        placerElements();
        placerPersonnagesAleatoirement();

        // Marquer le labyrinthe comme généré
        estGeneree = true;
    }

    private void placerSortie() {
        // Ne pas placer de sortie pour les grilles trop petites
        if (x <= 3 || y <= 3) {
            return;
        }

        Random rand = new Random();
        int cote = rand.nextInt(4); // 0: haut, 1: droite, 2: bas, 3: gauche

        switch (cote) {
            case 0: // Haut
                int posHaut = 1 + rand.nextInt(x - 2);
                grille[0][posHaut] = HERBE;
                break;

            case 1: // Droite
                int posDroite = 1 + rand.nextInt(y - 2);
                grille[posDroite][x - 1] = HERBE;
                break;

            case 2: // Bas
                int posBas = 1 + rand.nextInt(x - 2);
                grille[y - 1][posBas] = HERBE;
                break;

            case 3: // Gauche
                int posGauche = 1 + rand.nextInt(y - 2);
                grille[posGauche][0] = HERBE;
                break;
        }
    }

    // Méthode pour placer des éléments aléatoirement
    private void placerElements() {
        Random rand = new Random();
        for (int i = 1; i < y - 1; i++) {
            for (int j = 1; j < x - 1; j++) {
                if (grille[i][j] == HERBE) {
                    // 20% de chance d'avoir une marguerite
                    // 10% de chance d'avoir un cactus
                    int chance = rand.nextInt(10);
                    if (chance < 2) {
                        grille[i][j] = MARGUERITE;
                    } else if (chance < 3) {
                        grille[i][j] = CACTUS;
                    }
                }
            }
        }
    }

    private void placerPersonnagesAleatoirement() {
        Random rand = new Random();

        // Vérifier que la grille est assez grande
        if (x <= 2 || y <= 2) {
            return;
        }

        // Placer le mouton
        int moutonX, moutonY;
        int tentatives = 0;
        do {
            moutonX = 1 + rand.nextInt(Math.max(1, x - 2));
            moutonY = 1 + rand.nextInt(Math.max(1, y - 2));
            tentatives++;
            // Éviter une boucle infinie
            if (tentatives > 100) {
                return;
            }
        } while (grille[moutonY][moutonX] == ROCHER);

        grille[moutonY][moutonX] = MOUTON;

        // Placer le loup à une distance minimale du mouton
        int loupX, loupY;
        tentatives = 0;
        do {
            loupX = 1 + rand.nextInt(Math.max(1, x - 2));
            loupY = 1 + rand.nextInt(Math.max(1, y - 2));
            tentatives++;
            // Pour les petites grilles, réduire la distance minimale
            int distanceMin = Math.min(5, (x + y) / 4);
            if (tentatives > 100) {
                // Si on ne trouve pas de place, placer le loup n'importe où
                if (grille[loupY][loupX] != ROCHER && grille[loupY][loupX] != MOUTON) {
                    break;
                }
            }
        } while (grille[loupY][loupX] == ROCHER ||
                grille[loupY][loupX] == MOUTON ||
                (Math.abs(loupX - moutonX) + Math.abs(loupY - moutonY)) < Math.min(5, (x + y) / 4));

        grille[loupY][loupX] = LOUP;
    }

    private int find(int[] parent, int x) {
        if (parent[x] < 0) return x;
        parent[x] = find(parent, parent[x]);
        return parent[x];
    }

    private void union(int[] parent, int x, int y) {
        int rootX = find(parent, x);
        int rootY = find(parent, y);
        if (rootX != rootY) {
            if (parent[rootX] < parent[rootY]) {
                parent[rootX] += parent[rootY];
                parent[rootY] = rootX;
            } else {
                parent[rootY] += parent[rootX];
                parent[rootX] = rootY;
            }
        }
    }

    public boolean estGeneree() {
        return estGeneree;
    }

    public Grille copier() {
        Grille copie = new Grille(this.getX(), this.getY(), true);
        int[][] grilleCopie = copie.getGrille();
        int[][] grilleOriginale = this.getGrille();

        for (int y = 0; y < this.getY(); y++) {
            for (int x = 0; x < this.getX(); x++) {
                grilleCopie[y][x] = grilleOriginale[y][x];
            }
        }

        return copie;
    }

    public List<int[]> getSorties() {
        List<int[]> sorties = new ArrayList<>();

        // Bord haut et bas
        for (int x = 1; x < getX() - 1; x++) {
            if (getElement(0, x) != ROCHER) {
                sorties.add(new int[]{x, 0});
            }
            if (getElement(getY() - 1, x) != ROCHER) {
                sorties.add(new int[]{x, getY() - 1});
            }
        }

        // Bord gauche et droit
        for (int y = 1; y < getY() - 1; y++) {
            if (getElement(y, 0) != ROCHER) {
                sorties.add(new int[]{0, y});
            }
            if (getElement(y, getX() - 1) != ROCHER) {
                sorties.add(new int[]{getX() - 1, y});
            }
        }

        return sorties;
    }

    public void genererLabyrintheImparfait(double tauxSuppression) {
        genererLabyrinthe(); // Génère d'abord un labyrinthe parfait
        Random rand = new Random();
        int mursSupprimes = 0;
        int nbMurs = 0;
        List<int[]> murs = new ArrayList<>();
        for (int i = 1; i < y - 1; i++) {
            for (int j = 1; j < x - 1; j++) {
                if (grille[i][j] == ROCHER) {
                    // Vérifie s'il s'agit d'un mur interne
                    if ((i % 2 == 1 && j % 2 == 0) || (i % 2 == 0 && j % 2 == 1)) {
                        murs.add(new int[]{i, j});
                        nbMurs++;
                    }
                }
            }
        }
        int nbASupprimer = (int)(tauxSuppression * nbMurs);
        Collections.shuffle(murs);
        for (int k = 0; k < nbASupprimer; k++) {
            int[] mur = murs.get(k);
            grille[mur[0]][mur[1]] = HERBE;
            mursSupprimes++;
        }
    }

    public void gererRepousse() {
        Map<String, Integer> nouveauTemps = new HashMap<>();
        for (Map.Entry<String, Integer> entry : tempsRepousse.entrySet()) {
            String[] coords = entry.getKey().split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            int type = Integer.parseInt(coords[2]);
            int temps = entry.getValue() - 1;

            if (temps <= 0 && grille[y][x] == HERBE) {
                grille[y][x] = type; // Repousse
            } else if (temps > 0) {
                nouveauTemps.put(entry.getKey(), temps);
            }
        }
        tempsRepousse = nouveauTemps;
    }
}