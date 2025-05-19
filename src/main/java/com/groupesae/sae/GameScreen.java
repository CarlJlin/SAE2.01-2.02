package com.groupesae.sae;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class GameScreen extends BorderPane {

    private App app;
    private GridPane gameGrid;
    private int gridSize = 10;
    private String selectedElement = null;
    private ImageView draggingImageView = null;

    private final Image grassImage = new Image(getClass().getResourceAsStream("/images/grass.png"));
    private final Image wolfImage = new Image(getClass().getResourceAsStream("/images/wolf.png"));
    private final Image sheepImage = new Image(getClass().getResourceAsStream("/images/sheep.png"));
    private final Image rockImage = new Image(getClass().getResourceAsStream("/images/rock.png"));
    private final Image exitImage = new Image(getClass().getResourceAsStream("/images/exit.png"));

    public GameScreen(App app) {
        this.app = app;

        VBox topBox = new VBox();
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));

        initGameGrid();

        VBox rightPanel = initElementsPalette();
        setRight(rightPanel);

        Button backButton = new Button("Retour au menu");
        backButton.setOnAction(e -> app.showMainMenu());

        Button startGameButton = new Button("Démarrer le jeu");
        startGameButton.setOnAction(e -> {
            System.out.println("Jeu démarré!");
        });

        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
        bottomBox.setPadding(new Insets(10));
        bottomBox.getChildren().addAll(startGameButton, backButton);
        setBottom(bottomBox);

        setOnMouseMoved(this::handleMouseMove);
        setOnMouseClicked(this::handleMouseClick);
    }

    private void initGameGrid() {
        gameGrid = new GridPane();
        gameGrid.setHgap(1);
        gameGrid.setVgap(1);
        gameGrid.setPadding(new Insets(20));
        gameGrid.setAlignment(Pos.CENTER);

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(40, 40);

                if (row == 0 || row == gridSize - 1 || col == 0 || col == gridSize - 1) {
                    Rectangle wall = new Rectangle(40, 40, Color.DARKGRAY);
                    cell.getChildren().add(wall);
                } else {
                    ImageView grassView = new ImageView(grassImage);
                    grassView.setFitWidth(40);
                    grassView.setFitHeight(40);
                    cell.getChildren().add(grassView);
                }

                cell.setStyle("-fx-border-color: black; -fx-border-width: 0.5px;");

                cell.setId(row + "," + col);

                gameGrid.add(cell, col, row);
            }
        }

        setCenter(gameGrid);
    }

    private VBox initElementsPalette() {
        VBox palette = new VBox(10);
        palette.setPadding(new Insets(20));
        palette.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Éléments");
        title.setFont(new Font("Arial Bold", 16));

        String[] elements = {"Herbe", "Loup", "Mouton", "Rocher", "Sortie"};
        Image[] images = {grassImage, wolfImage, sheepImage, rockImage, exitImage};

        for (int i = 0; i < elements.length; i++) {
            final int index = i;

            BorderPane elementPane = new BorderPane();
            elementPane.setPrefSize(80, 80);
            elementPane.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

            ImageView imageView = new ImageView(images[i]);
            imageView.setFitWidth(60);
            imageView.setFitHeight(60);

            Label elementLabel = new Label(elements[i]);

            VBox elementBox = new VBox(5);
            elementBox.setAlignment(Pos.CENTER);
            elementBox.getChildren().addAll(imageView, elementLabel);

            elementPane.setCenter(elementBox);

            elementPane.setOnMouseClicked(e -> {
                selectedElement = elements[index];

                palette.getChildren().forEach(node -> {
                    if (node instanceof BorderPane) {
                        ((BorderPane) node).setStyle("-fx-border-color: black; -fx-border-width: 1px;");
                    }
                });

                elementPane.setStyle("-fx-border-color: red; -fx-border-width: 3px;");
            });

            palette.getChildren().add(elementPane);
        }

        return palette;
    }

    private void handleMouseMove(MouseEvent event) {
        if (selectedElement != null) {
            if (draggingImageView == null) {
                Image selectedImage = null;

                switch (selectedElement) {
                    case "Herbe": selectedImage = grassImage; break;
                    case "Loup": selectedImage = wolfImage; break;
                    case "Mouton": selectedImage = sheepImage; break;
                    case "Rocher": selectedImage = rockImage; break;
                    case "Sortie": selectedImage = exitImage; break;
                }

                if (selectedImage != null) {
                    draggingImageView = new ImageView(selectedImage);
                    draggingImageView.setFitWidth(30);
                    draggingImageView.setFitHeight(30);
                    draggingImageView.setOpacity(0.7);
                    getChildren().add(draggingImageView);
                }
            }

            draggingImageView.setX(event.getX());
            draggingImageView.setY(event.getY());
            draggingImageView.toFront();
        } else if (draggingImageView != null) {
            getChildren().remove(draggingImageView);
            draggingImageView = null;
        }
    }

    private void handleMouseClick(MouseEvent event) {
        if (selectedElement != null) {
            for (javafx.scene.Node node : gameGrid.getChildren()) {
                if (node instanceof StackPane) {
                    StackPane cell = (StackPane) node;
                    if (cell.getBoundsInParent().contains(
                            gameGrid.sceneToLocal(event.getSceneX(), event.getSceneY()))) {

                        String[] coords = cell.getId().split(",");
                        int row = Integer.parseInt(coords[0]);
                        int col = Integer.parseInt(coords[1]);

                        placeElement(cell, row, col, selectedElement);
                        break;
                    }
                }
            }
        }
    }

    private void placeElement(StackPane cell, int row, int col, String elementType) {
        if (row == 0 || row == gridSize - 1 || col == 0 || col == gridSize - 1) {
            return;
        }

        cell.getChildren().removeIf(node -> node instanceof ImageView &&
                !((ImageView)node).getImage().equals(grassImage));

        ImageView elementView = null;

        switch (elementType) {
            case "Loup":
                elementView = new ImageView(wolfImage);
                break;
            case "Mouton":
                elementView = new ImageView(sheepImage);
                break;
            case "Rocher":
                elementView = new ImageView(rockImage);
                break;
            case "Sortie":
                elementView = new ImageView(exitImage);
                break;
        }

        if (elementView != null && !elementType.equals("Herbe")) {
            elementView.setFitWidth(40);
            elementView.setFitHeight(40);
            cell.getChildren().add(elementView);
        }
    }
}