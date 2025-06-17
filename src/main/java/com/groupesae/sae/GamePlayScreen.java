package com.groupesae.sae;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

public class GamePlayScreen {
    private static final int CELL_MAX_SIZE = 40;
    private ComboBox<String> pathfindingMode;
    private Stage primaryStage;
    private int width;
    private int height;
    private Grille grille;
    private Map<Integer, Image> elementImages = new HashMap<>();
    private final int CELL_SIZE = 25;
    private final int SORTIE = -5;

    private int positionMoutonX = -1, positionMoutonY = -1;
    private int positionLoupX = -1, positionLoupY = -1;
    private Canvas gameCanvas;

    private boolean isMoutonTurn = true;
    private Label turnLabel;
    private boolean partieTerminee = false;

    private Set<String> casesVisiteesMouton = new HashSet<>();

    // Chemins en surbrillance
    private List<int[]> cheminMouton = new ArrayList<>(); // Chemin emprunté par le mouton
    private List<int[]> cheminVersLoup = new ArrayList<>(); // Chemin vers le loup
    private boolean afficherChemins = true; // Option pour activer/désactiver l'affichage

    private int forceMouton = 2;
    private int forceLoup = 3;

    private Scene scene;

    private boolean automaticMouton = false;
    private boolean automaticLoup = false;
    private Loup loup;
    private Mouton mouton;
    private Dijkstra dijkstra;
    private Astar astar;
    private List<int[]> cheminOptimal;
    private int indexCheminActuel = 0;

    private int deplacementsRestants = 2;
    private Label deplacementsLabel;

    public GamePlayScreen(Stage primaryStage, Grille grille) {
        this.primaryStage = primaryStage;
        this.grille = grille;
        this.width = grille.getX();
        this.height = grille.getY();
        primaryStage.setWidth(1300);
        primaryStage.setHeight(700);

        loadImages();
        findCharacters();

        // Initialiser les personnages
        if (positionLoupX != -1 && positionLoupY != -1) {
            this.loup = new Loup(positionLoupX, positionLoupY);
        }
        if (positionMoutonX != -1 && positionMoutonY != -1) {
            this.mouton = new Mouton(positionMoutonX, positionMoutonY);
            // Initialiser le chemin du mouton avec sa position initiale
            cheminMouton.add(new int[]{positionMoutonX, positionMoutonY});
        }
    }

    public GamePlayScreen(Stage primaryStage, Grille grille, boolean automatic, String pathfindingMode) {
        this(primaryStage, grille);
        this.automaticMouton = automatic;
        this.automaticLoup = automatic;
        this.pathfindingMode = new ComboBox<>();
        this.pathfindingMode.getItems().addAll("Dijkstra", "A*");
        this.pathfindingMode.setValue(pathfindingMode);

        if (automaticMouton) {
            if ("A*".equals(pathfindingMode)) {
                astar = new Astar(grille);
                cheminOptimal = astar.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
            } else {
                dijkstra = new Dijkstra(grille);
                cheminOptimal = dijkstra.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
            }
            indexCheminActuel = 0;

            // Calculer le chemin initial vers le loup
            calculerCheminVersLoup();
        }
    }

    private void loadImages() {
        try {
            String basePath = "src/main/resources/com/groupesae/sae/Elements/";
            elementImages.put(Grille.HERBE, new Image(new File(basePath + "Herbe.png").toURI().toString()));
            elementImages.put(Grille.MARGUERITE, new Image(new File(basePath + "Marguerite.png").toURI().toString()));
            elementImages.put(Grille.CACTUS, new Image(new File(basePath + "Cactus.png").toURI().toString()));
            elementImages.put(Grille.ROCHER, new Image(new File(basePath + "Rocher.jpg").toURI().toString()));
            elementImages.put(Grille.MOUTON, new Image(new File(basePath + "Mouton.png").toURI().toString()));
            elementImages.put(Grille.LOUP, new Image(new File(basePath + "Loup.png").toURI().toString()));
            elementImages.put(SORTIE, new Image(new File(basePath + "Sortie.png").toURI().toString()));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images: " + e.getMessage());
            e.printStackTrace();
            createFallbackImages();
        }
    }

    private void createFallbackImages() {
        elementImages.clear();
        Canvas canvas = new Canvas(CELL_SIZE, CELL_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Herbe
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.HERBE, canvas.snapshot(null, null));

        // Marguerite
        gc.setFill(Color.YELLOW);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.MARGUERITE, canvas.snapshot(null, null));

        // Cactus
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.CACTUS, canvas.snapshot(null, null));

        // Rocher
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.ROCHER, canvas.snapshot(null, null));

        // Mouton
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.MOUTON, canvas.snapshot(null, null));

        // Loup
        gc.setFill(Color.RED);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.LOUP, canvas.snapshot(null, null));

        // Sortie
        gc.setFill(Color.BLUE);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(SORTIE, canvas.snapshot(null, null));
    }

    private void findCharacters() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grille.getElement(y, x) == Grille.MOUTON) {
                    positionMoutonX = x;
                    positionMoutonY = y;
                } else if (grille.getElement(y, x) == Grille.LOUP) {
                    positionLoupX = x;
                    positionLoupY = y;
                }
            }
        }
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("Mange moi si tu peux !");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        gameCanvas = new Canvas(width * CELL_SIZE, height * CELL_SIZE);
        drawGrid();

        turnLabel = new Label("Tour du mouton");
        turnLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        deplacementsLabel = new Label("Déplacements restants: 0");
        deplacementsLabel.setStyle("-fx-font-size: 16px;");

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);

        Button upButton = new Button("↑");
        Button downButton = new Button("↓");
        Button leftButton = new Button("←");
        Button rightButton = new Button("→");
        Button menuButton = new Button("Menu");

        // Bouton pour activer/désactiver l'affichage des chemins
        Button togglePathsButton = new Button("Afficher/Masquer chemins");
        togglePathsButton.setOnAction(e -> {
            afficherChemins = !afficherChemins;
            drawGrid();
        });

        menuButton.setOnAction(e -> {
            MainMenuScreen menu = new MainMenuScreen(primaryStage);
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
            primaryStage.setScene(menu.getScene());
        });

        VBox dirButtons = new VBox(5);
        HBox hButtons = new HBox(5, leftButton, rightButton);
        hButtons.setAlignment(Pos.CENTER);
        dirButtons.getChildren().addAll(upButton, hButtons, downButton);
        dirButtons.setAlignment(Pos.CENTER);

        // Ajouter le bouton des chemins seulement en mode automatique
        if (automaticMouton) {
            controls.getChildren().addAll(dirButtons, togglePathsButton, menuButton);
        } else {
            controls.getChildren().addAll(dirButtons, menuButton);
        }

        VBox contentBox = new VBox(15, titleLabel, gameCanvas, turnLabel, deplacementsLabel, controls);
        contentBox.setAlignment(Pos.CENTER);
        root.setCenter(contentBox);

        scene = new Scene(root, 800, 600);

        upButton.setOnAction(e -> {
            if (isMoutonTurn) {
                deplacerMouton(0, -1);
            } else {
                deplacerLoup(0, -1);
            }
        });

        downButton.setOnAction(e -> {
            if (isMoutonTurn) {
                deplacerMouton(0, 1);
            } else {
                deplacerLoup(0, 1);
            }
        });

        leftButton.setOnAction(e -> {
            if (isMoutonTurn) {
                deplacerMouton(-1, 0);
            } else {
                deplacerLoup(-1, 0);
            }
        });

        rightButton.setOnAction(e -> {
            if (isMoutonTurn) {
                deplacerMouton(1, 0);
            } else {
                deplacerLoup(1, 0);
            }
        });

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    if (isMoutonTurn) {
                        deplacerMouton(0, -1);
                    } else {
                        deplacerLoup(0, -1);
                    }
                    break;
                case DOWN:
                    if (isMoutonTurn) {
                        deplacerMouton(0, 1);
                    } else {
                        deplacerLoup(0, 1);
                    }
                    break;
                case LEFT:
                    if (isMoutonTurn) {
                        deplacerMouton(-1, 0);
                    } else {
                        deplacerLoup(-1, 0);
                    }
                    break;
                case RIGHT:
                    if (isMoutonTurn) {
                        deplacerMouton(1, 0);
                    } else {
                        deplacerLoup(1, 0);
                    }
                    break;
            }
        });

        scene.setOnMouseClicked(e -> scene.getRoot().requestFocus());
        root.requestFocus();

        if (automaticMouton && isMoutonTurn) {
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(e -> {
                // Initialisation dynamique de l'algorithme selon le choix utilisateur
                if (pathfindingMode != null && "A*".equals(pathfindingMode.getValue())) {
                    if (astar == null) astar = new Astar(grille);
                    cheminOptimal = astar.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
                } else {
                    if (dijkstra == null) dijkstra = new Dijkstra(grille);
                    cheminOptimal = dijkstra.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
                }
                indexCheminActuel = 0;

                // Réinitialiser le chemin du mouton au début du tour
                if (deplacementsRestants == forceMouton) {
                    cheminMouton.clear();
                    cheminMouton.add(new int[]{positionMoutonX, positionMoutonY});
                }

                deplacerMoutonAutomatiquement();
            });
            pause.play();
        }

        return scene;
    }

    private void deplacerMouton(int dx, int dy) {
        if (!isMoutonTurn || automaticMouton) return;

        int nextX = positionMoutonX + dx;
        int nextY = positionMoutonY + dy;
        String key = nextX + "," + nextY;

        if (deplacementsRestants == forceMouton) {
            casesVisiteesMouton.clear();
            casesVisiteesMouton.add(positionMoutonX + "," + positionMoutonY);
        }

        if (casesVisiteesMouton.contains(key)) {
            return;
        }

        // Enregistrer l'élément mangé si c'est une marguerite ou un cactus
        int elementActuel = grille.getElement(nextY, nextX);
        if (elementActuel == Grille.MARGUERITE || elementActuel == Grille.CACTUS) {
            grille.enregistrerElementMange(nextX, nextY, elementActuel);
        }

        String direction = getDirection(dx, dy);
        mouton.deplacer(grille, direction, false);
        positionMoutonX = mouton.getX();
        positionMoutonY = mouton.getY();
        casesVisiteesMouton.add(key);
        deplacementsRestants--;
        deplacementsLabel.setText("Déplacements restants: " + deplacementsRestants);

        if (deplacementsRestants <= 0) {
            changeTurn();
        }
        drawGrid();
    }

    private void deplacerMoutonAutomatiquement() {
        if (deplacementsRestants == forceMouton) {
            casesVisiteesMouton.clear();
            casesVisiteesMouton.add(positionMoutonX + "," + positionMoutonY);

            // Réinitialiser le chemin du mouton au début du tour
            cheminMouton.clear();
            cheminMouton.add(new int[]{positionMoutonX, positionMoutonY});
        }

        int distanceLoup = Math.abs(positionMoutonX - positionLoupX) + Math.abs(positionMoutonY - positionLoupY);

        if (distanceLoup <= mouton.VISION) {
            if (cheminOptimal == null || cheminOptimal.isEmpty() || indexCheminActuel >= cheminOptimal.size()) {
                if (pathfindingMode != null && "A*".equals(pathfindingMode.getValue())) {
                    cheminOptimal = astar.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
                } else {
                    cheminOptimal = dijkstra.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
                    System.out.println("Chemin optimal recalculé vers la sortie.");
                }
                indexCheminActuel = 0;
            }
            if (cheminOptimal == null || cheminOptimal.isEmpty()) {
                changeTurn();
                return;
            }
            int[] prochainePas = cheminOptimal.get(indexCheminActuel);
            int dx = Integer.compare(prochainePas[0], positionMoutonX);
            int dy = Integer.compare(prochainePas[1], positionMoutonY);
            int nouvelleX = positionMoutonX + dx;
            int nouvelleY = positionMoutonY + dy;

            String key = nouvelleX + "," + nouvelleY;
            if (casesVisiteesMouton.contains(key)) {
                changeTurn();
                return;
            }
            indexCheminActuel++;

            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(e -> {
                mouton.deplacer(grille, getDirection(dx, dy), true);
                positionMoutonX = mouton.getX();
                positionMoutonY = mouton.getY();
                casesVisiteesMouton.add(key);

                // Mettre à jour la force du mouton
                forceMouton = mouton.getForce();

                // Ajouter la nouvelle position au chemin du mouton
                cheminMouton.add(new int[]{positionMoutonX, positionMoutonY});
                // Recalculer le chemin vers le loup
                calculerCheminVersLoup();

                deplacementsRestants--;
                deplacementsLabel.setText("Déplacements restants: " + deplacementsRestants);
                drawGrid();
                if (deplacementsRestants > 0) {
                    PauseTransition nextMove = new PauseTransition(Duration.seconds(0.5));
                    nextMove.setOnFinished(event -> deplacerMoutonAutomatiquement());
                    nextMove.play();
                } else {
                    changeTurn();
                }
            });
            pause.play();
            return;
        }

        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        int meilleurScore = -1;
        int meilleurDx = 0;
        int meilleurDy = 0;

        for (int[] dir : directions) {
            int nextX = positionMoutonX + dir[0];
            int nextY = positionMoutonY + dir[1];
            String key = nextX + "," + nextY;

            if (nextX >= 0 && nextX < grille.getX() && nextY >= 0 && nextY < grille.getY()
                    && !casesVisiteesMouton.contains(key)) {
                int element = grille.getElement(nextY, nextX);

                if (element != Grille.ROCHER && element != Grille.LOUP) {
                    int score = 0;
                    if (element == Grille.MARGUERITE) score = 3;
                    else if (element == Grille.HERBE) score = 2;
                    else if (element == Grille.CACTUS) score = 1;

                    if (score > meilleurScore) {
                        meilleurScore = score;
                        meilleurDx = dir[0];
                        meilleurDy = dir[1];
                    }
                }
            }
        }

        if (meilleurScore == -1) {
            // Si aucun chemin trouvé, on essaie de se déplacer n'importe où
            for (int[] dir : directions) {
                int nextX = positionMoutonX + dir[0];
                int nextY = positionMoutonY + dir[1];
                if (nextX >= 0 && nextX < grille.getX() && nextY >= 0 && nextY < grille.getY()) {
                    int element = grille.getElement(nextY, nextX);
                    if (element != Grille.ROCHER && element != Grille.LOUP) {
                        meilleurDx = dir[0];
                        meilleurDy = dir[1];
                        break;
                    }
                }
            }
        }

        if (meilleurDx != 0 || meilleurDy != 0) {
            final int dx = meilleurDx;
            final int dy = meilleurDy;
            int nouvelleX = positionMoutonX + dx;
            int nouvelleY = positionMoutonY + dy;
            String key = nouvelleX + "," + nouvelleY;

            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(e -> {
                mouton.deplacer(grille, getDirection(dx, dy), true);
                positionMoutonX = mouton.getX();
                positionMoutonY = mouton.getY();
                casesVisiteesMouton.add(key);

                // Ajouter la nouvelle position au chemin du mouton
                cheminMouton.add(new int[]{positionMoutonX, positionMoutonY});

                // Recalculer le chemin vers le loup
                calculerCheminVersLoup();

                deplacementsRestants--;
                deplacementsLabel.setText("Déplacements restants: " + deplacementsRestants);
                drawGrid();
                if (deplacementsRestants > 0) {
                    PauseTransition nextMove = new PauseTransition(Duration.seconds(0.5));
                    nextMove.setOnFinished(event -> deplacerMoutonAutomatiquement());
                    nextMove.play();
                } else {
                    changeTurn();
                }
            });
            pause.play();
        } else {
            changeTurn();
        }
    }

    // Méthode pour calculer le chemin vers le loup
    private void calculerCheminVersLoup() {
        if (positionLoupX != -1 && positionLoupY != -1 && positionMoutonX != -1 && positionMoutonY != -1) {
            if (pathfindingMode != null && "A*".equals(pathfindingMode.getValue())) {
                if (astar == null) astar = new Astar(grille);
                cheminVersLoup = astar.trouverChemin(positionMoutonX, positionMoutonY, positionLoupX, positionLoupY);
            } else {
                if (dijkstra == null) dijkstra = new Dijkstra(grille);
                cheminVersLoup = dijkstra.trouverChemin(positionMoutonX, positionMoutonY, positionLoupX, positionLoupY);
            }
            if (cheminVersLoup == null) cheminVersLoup = new ArrayList<>();
        }
    }

    private void drawGrid() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width * CELL_SIZE, height * CELL_SIZE);

        // Dessiner la grille de base
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int element = grille.getElement(y, x);
                Image img = elementImages.get(element);

                if (img != null) {
                    gc.drawImage(img, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    // Code de secours pour dessiner si l'image n'est pas disponible
                    switch (element) {
                        case Grille.HERBE:
                            gc.setFill(Color.LIGHTGREEN);
                            break;
                        case Grille.ROCHER:
                            gc.setFill(Color.GRAY);
                            break;
                        case Grille.MARGUERITE:
                            gc.setFill(Color.YELLOW);
                            break;
                        case Grille.CACTUS:
                            gc.setFill(Color.DARKGREEN);
                            break;
                        case Grille.MOUTON:
                            gc.setFill(Color.WHITE);
                            break;
                        case Grille.LOUP:
                            gc.setFill(Color.RED);
                            break;
                        default:
                            gc.setFill(Color.BLACK);
                            break;
                    }
                    gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }

                gc.setStroke(Color.BLACK);
                gc.strokeRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Afficher les chemins en surbrillance si activé
        if (afficherChemins) {
            // Dessiner le chemin vers le loup en rouge
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.setGlobalAlpha(0.7);

            if (cheminVersLoup != null && !cheminVersLoup.isEmpty()) {
                for (int i = 0; i < cheminVersLoup.size() - 1; i++) {
                    int[] point = cheminVersLoup.get(i);
                    int[] nextPoint = cheminVersLoup.get(i + 1);

                    double x1 = point[0] * CELL_SIZE + CELL_SIZE / 2;
                    double y1 = point[1] * CELL_SIZE + CELL_SIZE / 2;
                    double x2 = nextPoint[0] * CELL_SIZE + CELL_SIZE / 2;
                    double y2 = nextPoint[1] * CELL_SIZE + CELL_SIZE / 2;

                    gc.strokeLine(x1, y1, x2, y2);
                }
            }

            // Dessiner le chemin emprunté par le mouton en bleu
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(3);

            if (cheminMouton != null && cheminMouton.size() > 1) {
                for (int i = 0; i < cheminMouton.size() - 1; i++) {
                    int[] point = cheminMouton.get(i);
                    int[] nextPoint = cheminMouton.get(i + 1);

                    double x1 = point[0] * CELL_SIZE + CELL_SIZE / 2;
                    double y1 = point[1] * CELL_SIZE + CELL_SIZE / 2;
                    double x2 = nextPoint[0] * CELL_SIZE + CELL_SIZE / 2;
                    double y2 = nextPoint[1] * CELL_SIZE + CELL_SIZE / 2;

                    gc.strokeLine(x1, y1, x2, y2);
                }
            }

            gc.setGlobalAlpha(1.0);
        }
    }

    private void deplacerLoup(int dx, int dy) {
        if (isMoutonTurn || automaticLoup) return;

        loup.deplacer(grille, getDirection(dx, dy), false);
        positionLoupX = loup.getX();
        positionLoupY = loup.getY();

        calculerCheminVersLoup();

        deplacementsRestants--;
        deplacementsLabel.setText("Déplacements restants: " + deplacementsRestants);

        if (deplacementsRestants <= 0) {
            changeTurn();
        }
        drawGrid();
    }

    private void deplacerLoupAutomatiquement() {
        if (partieTerminee) return;

        // Trouver un chemin vers le mouton
        int dx = 0, dy = 0;
        boolean cheminTrouve = false;

        // Vérifier si pathfindingMode est initialisé et déterminer l'algorithme à utiliser
        boolean useAStar = pathfindingMode != null && "A*".equals(pathfindingMode.getValue());

        if (useAStar) {
            if (astar == null) astar = new Astar(grille);
            List<int[]> cheminVersMouton = astar.trouverChemin(positionLoupX, positionLoupY, positionMoutonX, positionMoutonY);
            if (cheminVersMouton != null && !cheminVersMouton.isEmpty()) {
                int[] prochainPas = cheminVersMouton.get(0);
                dx = Integer.compare(prochainPas[0], positionLoupX);
                dy = Integer.compare(prochainPas[1], positionLoupY);
                cheminTrouve = true;
            }
        } else {
            if (dijkstra == null) dijkstra = new Dijkstra(grille);
            List<int[]> cheminVersMouton = dijkstra.trouverCheminIntelligent(positionLoupX, positionLoupY, positionMoutonX, positionMoutonY);
            if (cheminVersMouton != null && !cheminVersMouton.isEmpty()) {
                int[] prochainPas = cheminVersMouton.get(0);
                dx = Integer.compare(prochainPas[0], positionLoupX);
                dy = Integer.compare(prochainPas[1], positionLoupY);
                cheminTrouve = true;
            }
        }

        // Si aucun chemin n'est trouvé, essayer de se déplacer directement vers le mouton
        if (!cheminTrouve) {
            int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

            // Trier les directions par proximité au mouton
            Arrays.sort(directions, (dir1, dir2) -> {
                int nextX1 = positionLoupX + dir1[0];
                int nextY1 = positionLoupY + dir1[1];
                int nextX2 = positionLoupX + dir2[0];
                int nextY2 = positionLoupY + dir2[1];

                int dist1 = Math.abs(nextX1 - positionMoutonX) + Math.abs(nextY1 - positionMoutonY);
                int dist2 = Math.abs(nextX2 - positionMoutonX) + Math.abs(nextY2 - positionMoutonY);

                return Integer.compare(dist1, dist2);
            });

            // Essayer chaque direction en commençant par la plus proche du mouton
            for (int[] dir : directions) {
                int nextX = positionLoupX + dir[0];
                int nextY = positionLoupY + dir[1];

                if (nextX >= 0 && nextX < grille.getX() && nextY >= 0 && nextY < grille.getY() &&
                        grille.getElement(nextY, nextX) != Grille.ROCHER) {
                    dx = dir[0];
                    dy = dir[1];
                    cheminTrouve = true;
                    break;
                }
            }
        }

        // Si on a trouvé un mouvement valide
        if (cheminTrouve) {
            final int finalDx = dx;
            final int finalDy = dy;

            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(e -> {
                loup.deplacer(grille, getDirection(finalDx, finalDy), true);
                positionLoupX = loup.getX();
                positionLoupY = loup.getY();

                // Recalculer le chemin vers le loup après son déplacement
                calculerCheminVersLoup();

                deplacementsRestants--;
                deplacementsLabel.setText("Déplacements restants: " + deplacementsRestants);
                drawGrid();

                // Vérifier si le loup a mangé le mouton
                if (positionLoupX == positionMoutonX && positionLoupY == positionMoutonY) {
                    partieTerminee = true;
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Fin de partie");
                        alert.setHeaderText("Le loup a gagné!");
                        alert.setContentText("Le mouton a été mangé.");
                        alert.showAndWait();
                    });
                    return;
                }

                // Continuer le tour si le loup a encore des déplacements
                if (deplacementsRestants > 0) {
                    deplacerLoupAutomatiquement();
                } else {
                    changeTurn();
                }
            });
            pause.play();
        } else {
            // Si aucun mouvement n'est possible
            changeTurn();
        }
    }

    private void changeTurn() {
        if (isMoutonTurn) {
            isMoutonTurn = false;
            turnLabel.setText("Tour du loup");
            deplacementsRestants = forceLoup;
            loup.reinitialiserForce();

            // Garder le chemin du mouton mais réinitialiser le chemin vers le loup
            cheminVersLoup.clear();
            calculerCheminVersLoup();

            if (automaticLoup) {
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> deplacerLoupAutomatiquement());
                pause.play();
            }
        } else {
            // Fin du tour complet (mouton + loup) - Gérer les repousses
            grille.gererRepousse();

            isMoutonTurn = true;
            turnLabel.setText("Tour du mouton");
            deplacementsRestants = forceMouton;
            mouton.reinitialiserForce();

            // Réinitialiser le chemin du mouton pour le nouveau tour
            if (forceMouton > 0) {
                cheminMouton.clear();
                if (positionMoutonX != -1 && positionMoutonY != -1) {
                    // Utiliser A* ou Dijkstra selon le mode sélectionné
                    if (pathfindingMode != null && "A*".equals(pathfindingMode.getValue())) {
                        if (astar == null) astar = new Astar(grille);
                        cheminMouton = astar.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
                    } else {
                        if (dijkstra == null) dijkstra = new Dijkstra(grille);
                        cheminMouton = dijkstra.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
                    }
                }
            }

            calculerCheminVersLoup();

            if (automaticMouton) {
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> deplacerMoutonAutomatiquement());
                pause.play();
            }
        }

        deplacementsLabel.setText("Déplacements restants: " + deplacementsRestants);
        drawGrid();
    }

    private String getDirection(int dx, int dy) {
        if (dx == 0 && dy == -1) return "haut";
        if (dx == 0 && dy == 1) return "bas";
        if (dx == -1 && dy == 0) return "gauche";
        if (dx == 1 && dy == 0) return "droite";
        return ""; // Direction invalide
    }
}