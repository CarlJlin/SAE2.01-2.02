package com.groupesae.sae;

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

import java.io.File;
import java.util.HashMap;
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

    public GamePlayScreen(Stage primaryStage, Grille grille) {
        this.primaryStage = primaryStage;
        this.grille = grille;
        this.width = grille.getX();
        this.height = grille.getY();
        primaryStage.setWidth(1300);
        primaryStage.setHeight(800);
        loadImages();
        findCharacters();
    }

    private void loadImages() {
        try {
            // Utilisation de chemins d'accès au système de fichiers
            String basePath = "src/main/resources/com/groupesae/sae/Elements/";
            elementImages.put(Grille.HERBE, new Image(new File(basePath + "Herbe.png").toURI().toString()));
            elementImages.put(Grille.MARGUERITE, new Image(new File(basePath + "Marguerite.png").toURI().toString()));
            elementImages.put(Grille.CACTUS, new Image(new File(basePath + "Cactus.png").toURI().toString()));
            elementImages.put(Grille.ROCHER, new Image(new File(basePath + "Rocher.jpg").toURI().toString()));
            elementImages.put(Grille.MOUTON, new Image(new File(basePath + "Mouton.png").toURI().toString()));
            elementImages.put(Grille.LOUP, new Image(new File(basePath + "Loup.png").toURI().toString()));
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

        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.HERBE, canvas.snapshot(null, null));

        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(SORTIE, canvas.snapshot(null, null));

        gc.setFill(Color.YELLOW);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.MARGUERITE, canvas.snapshot(null, null));

        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.CACTUS, canvas.snapshot(null, null));

        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.ROCHER, canvas.snapshot(null, null));

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.MOUTON, canvas.snapshot(null, null));

        gc.setFill(Color.DARKRED);
        gc.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        elementImages.put(Grille.LOUP, canvas.snapshot(null, null));
    }

    private void findCharacters() {
        for (int i = 0; i < grille.getY(); i++) {
            for (int j = 0; j < grille.getX(); j++) {
                if (grille.getElement(i, j) == Grille.MOUTON) {
                    positionMoutonY = i;
                    positionMoutonX = j;
                } else if (grille.getElement(i, j) == Grille.LOUP) {
                    positionLoupY = i;
                    positionLoupX = j;
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

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);

        Button upButton = new Button("↑");
        Button downButton = new Button("↓");
        Button leftButton = new Button("←");
        Button rightButton = new Button("→");
        Button menuButton = new Button("Menu");

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

        controls.getChildren().addAll(dirButtons, menuButton);

        VBox contentBox = new VBox(15, titleLabel, gameCanvas, turnLabel, controls);
        contentBox.setAlignment(Pos.CENTER);
        root.setCenter(contentBox);

        scene = new Scene(root, 800, 600);

        upButton.setOnAction(e -> {
            deplacerAnimal(0, -1);
            scene.getRoot().requestFocus();
        });
        downButton.setOnAction(e -> {
            deplacerAnimal(0, 1);
            scene.getRoot().requestFocus();
        });
        leftButton.setOnAction(e -> {
            deplacerAnimal(-1, 0);
            scene.getRoot().requestFocus();
        });
        rightButton.setOnAction(e -> {
            deplacerAnimal(1, 0);
            scene.getRoot().requestFocus();
        });

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    deplacerAnimal(0, -1);
                    break;
                case DOWN:
                    deplacerAnimal(0, 1);
                    break;
                case LEFT:
                    deplacerAnimal(-1, 0);
                    break;
                case RIGHT:
                    deplacerAnimal(1, 0);
                    break;
                default:
                    break;
            }
            event.consume();
        });

        scene.setOnMouseClicked(e -> scene.getRoot().requestFocus());
        root.requestFocus();

        return scene;
    }

    private void deplacerAnimal(int dx, int dy) {
        if (isMoutonTurn) {
            if (deplacerAvecForce(positionMoutonX, positionMoutonY, dx, dy, forceMouton, Grille.MOUTON)) {
                turnLabel.setText("Tour du loup");
                isMoutonTurn = false;
            }
        } else {
            if (deplacerAvecForce(positionLoupX, positionLoupY, dx, dy, forceLoup, Grille.LOUP)) {
                turnLabel.setText("Tour du mouton");
                isMoutonTurn = true;
            }
        }

        if (scene != null) {
            scene.getRoot().requestFocus();
        }
    }

    private boolean deplacerAvecForce(int startX, int startY, int dx, int dy, int force, int animalType) {
        int newX = startX, newY = startY;

        for (int i = 0; i < force; i++) {
            int nextX = newX + dx;
            int nextY = newY + dy;

            if (nextX < 0 || nextX >= width || nextY < 0 || nextY >= height || grille.getElement(nextY, nextX) == Grille.ROCHER) {
                break;
            }

            newX = nextX;
            newY = nextY;
        }

        if (newX != startX || newY != startY) {
            if (animalType == Grille.MOUTON && (newX == 0 || newX == width-1 || newY == 0 || newY == height-1)) {
                grille.getGrille()[startY][startX] = Grille.HERBE;
                grille.getGrille()[newY][newX] = Grille.MOUTON;
                drawGrid();
                afficherMessage("Victoire!", "Le mouton a atteint la sortie!");
                return true;
            }

            int elementSous = grille.getElement(newY, newX);

            grille.getGrille()[startY][startX] = Grille.HERBE;
            grille.getGrille()[newY][newX] = animalType;

            if (animalType == Grille.MOUTON) {
                positionMoutonX = newX;
                positionMoutonY = newY;

                switch (elementSous) {
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
            } else if (animalType == Grille.LOUP) {
                positionLoupX = newX;
                positionLoupY = newY;

                if (positionLoupX == positionMoutonX && positionLoupY == positionMoutonY) {
                    afficherMessage("Défaite!", "Le loup a mangé le mouton!");
                }
            }

            drawGrid();
            return true;
        }
        return false;
    }

    private void afficherMessage(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait().ifPresent(response -> {
            MainMenuScreen menu = new MainMenuScreen(primaryStage);
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
            primaryStage.setScene(menu.getScene());
        });
    }

    private void drawGrid() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Fond noir pour toute la grille
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * calculateCellSize(), height * calculateCellSize());

        int cellSize = calculateCellSize();
        int spacing = 1;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int element = grille.getElement(i, j);

                // Vérifier si la case est sur un bord (sortie potentielle)
                boolean estSurBord = (i == 0 || i == height - 1 || j == 0 || j == width - 1);
                boolean estSortie = estSurBord && element != Grille.ROCHER && element != Grille.LOUP && element != Grille.MOUTON;

                Image img;
                if (estSortie) {
                    // Utiliser l'image d'herbe pour la sortie
                    img = elementImages.get(Grille.HERBE);
                } else {
                    img = elementImages.get(element);
                }

                if (img != null) {
                    gc.drawImage(img,
                            j * cellSize + spacing,
                            i * cellSize + spacing,
                            cellSize - 2 * spacing,
                            cellSize - 2 * spacing);

                    // Optionnel : ajouter un indicateur visuel pour les sorties
                    if (estSortie) {
                        gc.setStroke(Color.BLUE);
                        gc.setLineWidth(2);
                        gc.strokeRect(
                                j * cellSize + spacing,
                                i * cellSize + spacing,
                                cellSize - 2 * spacing,
                                cellSize - 2 * spacing);
                    }
                }
            }
        }
    }

    private int calculateCellSize() {
        int availableWidth = 600;
        int availableHeight = 400;

        return Math.min(CELL_MAX_SIZE, Math.min(availableWidth / grille.getX(), availableHeight / grille.getY()));
    }

}