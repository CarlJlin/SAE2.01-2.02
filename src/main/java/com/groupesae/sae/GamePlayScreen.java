package com.groupesae.sae;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlayScreen {
    private static final int CELL_MAX_SIZE = 80;
    private Stage primaryStage;
    private int width;
    private int height;
    private Grille grille;
    private Map<Integer, Image> elementImages = new HashMap<>();
    private final int CELL_SIZE = 50;
    private final int SORTIE = -5;

    private int positionMoutonX = -1, positionMoutonY = -1;
    private int positionLoupX = -1, positionLoupY = -1;
    private Canvas gameCanvas;

    private boolean isMoutonTurn = true;
    private Label turnLabel;

    private int forceMouton = 2;
    private int forceLoup = 3;

    private Scene scene;

    private boolean automaticMouton = false;
    private Button autoMoutonButton;
    private Dijkstra dijkstra;
    private List<int[]> cheminOptimal;
    private int indexCheminActuel = 0;

    private int deplacementsRestants = 2;
    private Label deplacementsLabel;

    public GamePlayScreen(Stage primaryStage, Grille grille, boolean automatic) {
        System.out.println("Création de la fenêtre de jeu...");
        this.primaryStage = primaryStage;
        this.grille = grille;
        this.width = grille.getX();
        this.height = grille.getY();
        primaryStage.setWidth(1300);
        primaryStage.setHeight(900);

        // Générer le labyrinthe seulement si ce n'est pas déjà fait
        if (automatic && !grille.estGeneree()) {
            grille.genererLabyrinthe();
        }

        loadImages();
        findCharacters();

        // Initialiser Dijkstra
        this.dijkstra = new Dijkstra(grille);

        // Activer automatiquement le mode auto si demandé
        this.automaticMouton = automatic;
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

        // Bouton pour activer/désactiver le mode automatique du mouton
        autoMoutonButton = new Button(automaticMouton ? "Mode Auto: ON" : "Mode Auto: OFF");
        autoMoutonButton.setStyle(automaticMouton ? "-fx-background-color: #99ff99;" : "-fx-background-color: #ff9999;");
        autoMoutonButton.setOnAction(e -> toggleAutomaticMouton());

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

        // Ajouter le bouton Auto dans les contrôles seulement si on n'est pas en mode automatique initial
        boolean isInitialAutoMode = automaticMouton && dijkstra != null;
        if (!isInitialAutoMode) {
            controls.getChildren().addAll(dirButtons, autoMoutonButton, menuButton);
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
                cheminOptimal = dijkstra.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
                indexCheminActuel = 0;
                deplacerMoutonAutomatiquement();
            });
            pause.play();
        }

        return scene;
    }

    private void toggleAutomaticMouton() {
        automaticMouton = !automaticMouton;

        if (automaticMouton) {
            autoMoutonButton.setText("Mode Auto: ON");
            autoMoutonButton.setStyle("-fx-background-color: #99ff99;");
            if (isMoutonTurn) {
                deplacerMoutonAutomatiquement();
            }
        } else {
            autoMoutonButton.setText("Mode Auto: OFF");
            autoMoutonButton.setStyle("-fx-background-color: #ff9999;");
        }
    }

    private void deplacerMoutonAutomatiquement() {
        if (cheminOptimal == null || cheminOptimal.isEmpty()) {
            cheminOptimal = dijkstra.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
            indexCheminActuel = 0;
        }

        if (cheminOptimal == null || cheminOptimal.isEmpty()) {
            System.out.println("Aucun chemin trouvé pour le mouton");
            return;
        }

        if (indexCheminActuel >= cheminOptimal.size()) {
            indexCheminActuel = 0;
        }

        int[] prochainePas = cheminOptimal.get(indexCheminActuel);
        int dx = Integer.compare(prochainePas[0], positionMoutonX);
        int dy = Integer.compare(prochainePas[1], positionMoutonY);

        System.out.println("Mouton automatique: déplacement vers (" + dx + "," + dy + ")");

        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));

        // Capture les valeurs dans des variables finales
        final int finalDx = dx;
        final int finalDy = dy;

        pause.setOnFinished(e -> {
            if (deplacerAvecForce(positionMoutonX, positionMoutonY, finalDx, finalDy, Grille.MOUTON)) {
                indexCheminActuel++;
                cheminOptimal = dijkstra.trouverCheminVersSortie(positionMoutonX, positionMoutonY);
                indexCheminActuel = 0;

                drawGrid();
                changeTurn();
            }
        });
        pause.play();
    }

    private void drawGrid() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width * CELL_SIZE, height * CELL_SIZE);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int element = grille.getElement(y, x);
                Image img = elementImages.get(element);

                if (img != null) {
                    gc.drawImage(img, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }

                gc.setStroke(Color.BLACK);
                gc.strokeRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void deplacerMouton(int dx, int dy) {
        if (!isMoutonTurn || automaticMouton) return;

        if (deplacerAvecForce(positionMoutonX, positionMoutonY, dx, dy, Grille.MOUTON)) {
            deplacementsRestants--;
            deplacementsLabel.setText("Déplacements restants: " + deplacementsRestants);

            if (deplacementsRestants <= 0) {
                changeTurn();
            }
        }
    }

    private void deplacerLoup(int dx, int dy) {
        if (isMoutonTurn) return;

        if (deplacerAvecForce(positionLoupX, positionLoupY, dx, dy, Grille.LOUP)) {
            deplacementsRestants--;
            deplacementsLabel.setText("Déplacements restants: " + deplacementsRestants);

            if (deplacementsRestants <= 0) {
                changeTurn();
            }
        }
    }

    private void changeTurn() {
        isMoutonTurn = !isMoutonTurn;
        if (isMoutonTurn) {
            turnLabel.setText("Tour du mouton");
            deplacementsRestants = forceMouton;
            if (automaticMouton) {
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e -> deplacerMoutonAutomatiquement());
                pause.play();
            }
        } else {
            turnLabel.setText("Tour du loup");
            deplacementsRestants = forceLoup;
        }
        deplacementsLabel.setText("Déplacements restants: " + deplacementsRestants);
    }
    // Remplacer la méthode deplacerAvecForce par cette version simplifiée
    private boolean deplacerAvecForce(int startX, int startY, int dx, int dy, int personnage) {
        if (dx == 0 && dy == 0) return false;

        int nextX = startX + dx;
        int nextY = startY + dy;

        // Vérifier si on sort de la grille ou si on rencontre un rocher
        if (nextX < 0 || nextX >= width || nextY < 0 || nextY >= height ||
                grille.getElement(nextY, nextX) == Grille.ROCHER) {
            return false;
        }

        // Le loup a attrapé le mouton
        boolean moutonAttrape = false;
        if (personnage == Grille.LOUP && nextX == positionMoutonX && nextY == positionMoutonY) {
            moutonAttrape = true;
        }

        // Le mouton a rencontré le loup
        if (personnage == Grille.MOUTON && nextX == positionLoupX && nextY == positionLoupY) {
            moutonAttrape = true;
        }

        // Mettre à jour la grille et les positions
        if (personnage == Grille.MOUTON) {
            // Vérifier si le mouton s'est fait manger
            if (moutonAttrape) {
                // Effacer l'ancienne position du mouton
                grille.getGrille()[positionMoutonY][positionMoutonX] = Grille.HERBE;

                // Mettre à jour la position du mouton
                positionMoutonX = nextX;
                positionMoutonY = nextY;

                finPartie(false);
                return true;
            }

            // Le type de case que le mouton a mangé
            int typeCaseMangee = grille.getElement(nextY, nextX);

            // Mettre à jour la vitesse du mouton en fonction de ce qu'il a mangé
            switch (typeCaseMangee) {
                case Grille.HERBE:
                    forceMouton = 2;
                    break;
                case Grille.MARGUERITE:
                    forceMouton = 4;
                    break;
                case Grille.CACTUS:
                    forceMouton = 1;
                    break;
            }

            // Effacer l'ancienne position du mouton
            grille.getGrille()[positionMoutonY][positionMoutonX] = Grille.HERBE;

            // Mettre à jour la position du mouton
            positionMoutonX = nextX;
            positionMoutonY = nextY;
            grille.getGrille()[positionMoutonY][positionMoutonX] = Grille.MOUTON;

            // Vérifier si le mouton a atteint une sortie
            if (positionMoutonX == 0 || positionMoutonX == width - 1 || positionMoutonY == 0 || positionMoutonY == height - 1) {
                finPartie(true);
                return true;
            }
        } else if (personnage == Grille.LOUP) {
            // Effacer l'ancienne position du loup
            grille.getGrille()[positionLoupY][positionLoupX] = Grille.HERBE;

            // Mettre à jour la position du loup
            positionLoupX = nextX;
            positionLoupY = nextY;
            grille.getGrille()[positionLoupY][positionLoupX] = Grille.LOUP;

            if (moutonAttrape) {
                finPartie(false);
                return true;
            }
        }

        drawGrid();
        return true;
    }

    private void finPartie(boolean victoire) {
        PauseTransition pauseAvantAlerte = new PauseTransition(Duration.seconds(0.5));
        pauseAvantAlerte.setOnFinished(event -> {
            Alert alert = new Alert(victoire ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
            alert.setTitle(victoire ? "Victoire !" : "Défaite !");
            alert.setHeaderText(null);
            alert.setContentText(victoire
                    ? "Le mouton a atteint la sortie !"
                    : "Le loup a attrapé le mouton !");

            javafx.application.Platform.runLater(() -> {
                alert.show();

                alert.setOnHidden(e -> {
                    MainMenuScreen menu = new MainMenuScreen(primaryStage);
                    primaryStage.setScene(menu.getScene());
                    primaryStage.setWidth(1400);
                    primaryStage.setHeight(800);
                });
            });
        });
        pauseAvantAlerte.play();
    }
}