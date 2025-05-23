package com.groupesae.sae;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import static javafx.scene.layout.Region.USE_PREF_SIZE;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GridCustomizationScreen {
    private Stage primaryStage;
    private TextField widthField;
    private TextField heightField;
    private int selectedElement = Grille.HERBE;
    private GridPane gameGrid;
    private int[][] gridData;
    private int gridWidth = 10;
    private int gridHeight = 10;
    private Scene scene;

    private final int CELL_MAX_SIZE = 40;
    private final int GRID_PADDING = 10;
    private final int ELEMENT_BUTTON_SIZE = 60;
    private final int ELEMENT_IMAGE_SIZE = 40;

    private final int SORTIE = -5;
    private int moutonX = -1, moutonY = -1;
    private int loupX = -1, loupY = -1;
    private int sortieX = -1, sortieY = -1;

    private Map<Integer, Image> elementImages = new HashMap<>();

    public GridCustomizationScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadImages();
    }

    private void loadImages() {
        try {
            elementImages.put(Grille.HERBE, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/groupesae/sae/Elements/Herbe.png"))));
            elementImages.put(Grille.MARGUERITE, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/groupesae/sae/Elements/Marguerite.png"))));
            elementImages.put(Grille.CACTUS, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/groupesae/sae/Elements/Cactus.png"))));
            elementImages.put(Grille.ROCHER, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/groupesae/sae/Elements/Rocher.jpg"))));
            elementImages.put(Grille.MOUTON, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/groupesae/sae/Elements/Mouton.png"))));
            elementImages.put(Grille.LOUP, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/groupesae/sae/Elements/Loup.png"))));
            elementImages.put(SORTIE, new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/groupesae/sae/Elements/Sortie.png"))));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images: " + e.getMessage());
            e.printStackTrace();

            createFallbackImages();
        }
    }

    private void createFallbackImages() {
        elementImages.clear();

        elementImages.put(Grille.HERBE, createColoredImage(Color.LIGHTGREEN));
        elementImages.put(Grille.MARGUERITE, createColoredImage(Color.YELLOW));
        elementImages.put(Grille.CACTUS, createColoredImage(Color.DARKGREEN));
        elementImages.put(Grille.ROCHER, createColoredImage(Color.GRAY));
        elementImages.put(Grille.MOUTON, createColoredImage(Color.WHITE));
        elementImages.put(Grille.LOUP, createColoredImage(Color.DARKRED));
        elementImages.put(SORTIE, createColoredImage(Color.BLUE));
    }

    private Image createColoredImage(Color color) {
        Canvas canvas = new Canvas(ELEMENT_IMAGE_SIZE, ELEMENT_IMAGE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(0, 0, ELEMENT_IMAGE_SIZE, ELEMENT_IMAGE_SIZE);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, ELEMENT_IMAGE_SIZE, ELEMENT_IMAGE_SIZE);

        WritableImage image = new WritableImage(ELEMENT_IMAGE_SIZE, ELEMENT_IMAGE_SIZE);
        canvas.snapshot(null, image);
        return image;
    }

    private VBox createGridContainer() {
        gameGrid = new GridPane();
        gameGrid.setAlignment(Pos.CENTER);
        gameGrid.setHgap(1);
        gameGrid.setVgap(1);

        StackPane gridBackground = new StackPane(gameGrid);
        gridBackground.setStyle("-fx-background-color: black; -fx-padding: 1px;");
        gridBackground.setMaxWidth(USE_PREF_SIZE);
        gridBackground.setMaxHeight(USE_PREF_SIZE);

        resetGrid();

        VBox gridContainer = new VBox(gridBackground);
        gridContainer.setAlignment(Pos.CENTER);
        gridContainer.setPadding(new Insets(GRID_PADDING));
        gridContainer.setMaxHeight(500);
        return gridContainer;
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("Personnalisation de la grille");
        titleLabel.setFont(new Font(24));
        HBox titleBox = new HBox(titleLabel);
        titleBox.setAlignment(Pos.TOP_CENTER);

        HBox dimensionsBox = createDimensionsBox();
        dimensionsBox.setPadding(new Insets(10, 0, 10, 0));

        VBox gridContainer = createGridContainer();

        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(20, 0, 20, 0));
        Button validateButton = new Button("Valider");
        validateButton.setPrefWidth(120);
        validateButton.setPrefHeight(40);
        validateButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        validateButton.setOnAction(event -> validateAndStartGame());

        Button resetButton = new Button("Réinitialiser");
        resetButton.setPrefWidth(120);
        resetButton.setPrefHeight(40);
        resetButton.setStyle("-fx-font-size: 14px;");
        resetButton.setOnAction(event -> {
            gridWidth = 10;
            gridHeight = 10;
            widthField.setText(String.valueOf(gridWidth));
            heightField.setText(String.valueOf(gridHeight));
            resetGrid();
        });
        buttonsBox.getChildren().addAll(validateButton, resetButton);

        VBox centerContent = new VBox(10);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.getChildren().addAll(titleBox, dimensionsBox, gridContainer, buttonsBox);
        root.setCenter(centerContent);

        VBox elementsPanel = createElementsPanel();
        root.setRight(elementsPanel);

        scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        return scene;
    }

    private VBox createElementsPanel() {
        VBox elementsPanel = new VBox(10);
        elementsPanel.setAlignment(Pos.CENTER);
        elementsPanel.setPadding(new Insets(10));

        Label panelTitle = new Label("Éléments");
        panelTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        for (Map.Entry<Integer, Image> entry : elementImages.entrySet()) {
            int elementType = entry.getKey();
            Image elementImage = entry.getValue();

            if (elementImage != null) {
                ImageView imageView = new ImageView(elementImage);
                imageView.setFitWidth(ELEMENT_IMAGE_SIZE);
                imageView.setFitHeight(ELEMENT_IMAGE_SIZE);
                imageView.setPreserveRatio(true);

                Button elementButton = new Button();
                elementButton.setGraphic(imageView);
                elementButton.setPrefSize(ELEMENT_BUTTON_SIZE, ELEMENT_BUTTON_SIZE);
                elementButton.setStyle("-fx-background-color: transparent;");

                elementButton.setOnAction(event -> {
                    selectedElement = elementType;
                    System.out.println("Élément sélectionné : " + elementType);
                });

                elementsPanel.getChildren().add(elementButton);
            } else {
                System.err.println("Image introuvable pour l'élément : " + elementType);
            }
        }

        elementsPanel.getChildren().add(0, panelTitle);
        return elementsPanel;
    }

    private HBox createDimensionsBox() {
        GridPane dimensionsForm = new GridPane();
        dimensionsForm.setHgap(10);
        dimensionsForm.setVgap(10);
        dimensionsForm.setPadding(new Insets(10));
        dimensionsForm.setAlignment(Pos.CENTER);

        Label widthLabel = new Label("Largeur:");
        widthField = new TextField(String.valueOf(gridWidth));
        widthField.setPrefWidth(60);

        Label heightLabel = new Label("Hauteur:");
        heightField = new TextField(String.valueOf(gridHeight));
        heightField.setPrefWidth(60);

        Button applyButton = new Button("Appliquer");
        applyButton.setOnAction(event -> {
            try {
                int newWidth = Integer.parseInt(widthField.getText());
                int newHeight = Integer.parseInt(heightField.getText());

                newWidth = Math.min(Math.max(newWidth, 5), 30);
                newHeight = Math.min(Math.max(newHeight, 5), 30);

                gridWidth = newWidth;
                gridHeight = newHeight;

                widthField.setText(String.valueOf(gridWidth));
                heightField.setText(String.valueOf(gridHeight));

                resetGrid();
            } catch (NumberFormatException e) {
                widthField.setText(String.valueOf(gridWidth));
                heightField.setText(String.valueOf(gridHeight));
            }
        });

        dimensionsForm.add(widthLabel, 0, 0);
        dimensionsForm.add(widthField, 1, 0);
        dimensionsForm.add(heightLabel, 2, 0);
        dimensionsForm.add(heightField, 3, 0);
        dimensionsForm.add(applyButton, 4, 0);

        HBox dimensionsBox = new HBox(dimensionsForm);
        dimensionsBox.setAlignment(Pos.CENTER);
        return dimensionsBox;
    }

    private void resetGrid() {
        gameGrid.getChildren().clear();
        gridData = new int[gridHeight][gridWidth];

        int cellSize = calculateCellSize();

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                if (y == 0 || y == gridHeight - 1 || x == 0 || x == gridWidth - 1) {
                    gridData[y][x] = Grille.ROCHER;
                } else {
                    gridData[y][x] = Grille.HERBE;
                }
                addGridCell(x, y, cellSize);
            }
        }

        moutonX = moutonY = -1;
        loupX = loupY = -1;
        sortieX = sortieY = -1;
    }

    private void addGridCell(int x, int y, int cellSize) {
        ImageView elementView = new ImageView(elementImages.get(gridData[y][x]));
        elementView.setFitWidth(cellSize);
        elementView.setFitHeight(cellSize);
        elementView.setSmooth(true);
        elementView.setPreserveRatio(false);

        StackPane cellContainer = new StackPane(elementView);
        cellContainer.setMinSize(cellSize, cellSize);
        cellContainer.setPrefSize(cellSize, cellSize);
        cellContainer.setMaxSize(cellSize, cellSize);

        cellContainer.setOnMouseClicked(event -> {
            placeElement(x, y);
            event.consume();
        });

        gameGrid.add(cellContainer, x, y);
    }

    private int calculateCellSize() {
        int availableWidth = 600;
        int availableHeight = 400;

        return Math.min(CELL_MAX_SIZE, Math.min(availableWidth / gridWidth, availableHeight / gridHeight));
    }

    private void placeElement(int x, int y) {
        if ((y == 0 || y == gridHeight - 1 || x == 0 || x == gridWidth - 1) && selectedElement != SORTIE) {
            if (gridData[y][x] == Grille.ROCHER) {
                return;
            }
        }

        if (selectedElement == Grille.MOUTON) {
            if (moutonX >= 0 && moutonY >= 0) {
                gridData[moutonY][moutonX] = Grille.HERBE;
                updateCellAppearance(moutonX, moutonY);
            }
            moutonX = x;
            moutonY = y;
        } else if (selectedElement == Grille.LOUP) {
            if (loupX >= 0 && loupY >= 0) {
                gridData[loupY][loupX] = Grille.HERBE;
                updateCellAppearance(loupX, loupY);
            }
            loupX = x;
            loupY = y;
        } else if (selectedElement == SORTIE) {
            boolean surBord = (x == 0 || x == gridWidth - 1 || y == 0 || y == gridHeight - 1);
            boolean pasCoin = !((x == 0 || x == gridWidth - 1) && (y == 0 || y == gridHeight - 1));

            if (!surBord || !pasCoin) {
                System.out.println("La sortie doit être sur un bord mais pas dans un coin");
                return;
            }

            if (sortieX >= 0 && sortieY >= 0) {
                gridData[sortieY][sortieX] = Grille.ROCHER;
                updateCellAppearance(sortieX, sortieY);
            }
            sortieX = x;
            sortieY = y;
        }

        gridData[y][x] = selectedElement;
        updateCellAppearance(x, y);
    }

    private void updateCellAppearance(int x, int y) {
        StackPane cellContainer = (StackPane) getNodeFromGridPane(gameGrid, x, y);
        if (cellContainer != null && !cellContainer.getChildren().isEmpty()) {
            ImageView elementView = (ImageView) cellContainer.getChildren().get(0);
            int element = gridData[y][x];
            if (element == SORTIE) {
                elementView.setImage(elementImages.get(Grille.HERBE));
            } else {
                elementView.setImage(elementImages.get(element));
            }
        }
    }

    private javafx.scene.Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private Grille createGrilleFromData() {
        Grille grille = new Grille(gridWidth, gridHeight, true);

        int[][] grilleData = grille.getGrille();
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                grilleData[y][x] = gridData[y][x];
            }
        }

        return grille;
    }

    private void validateAndStartGame() {
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        if (moutonX == -1 || moutonY == -1) {
            isValid = false;
            errorMessage.append("• Vous devez placer un mouton sur la grille.\n");
        }

        if (loupX == -1 || loupY == -1) {
            isValid = false;
            errorMessage.append("• Vous devez placer un loup sur la grille.\n");
        }

        if (sortieX == -1 || sortieY == -1) {
            isValid = false;
            errorMessage.append("• Vous devez placer une sortie sur la grille.\n");
        }

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Configuration incomplète");
            alert.setHeaderText("Impossible de lancer le jeu");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
        } else {
            Grille grille = createGrilleFromData();
            GamePlayScreen gamePlayScreen = new GamePlayScreen(primaryStage, grille);
            primaryStage.setScene(gamePlayScreen.getScene());
            primaryStage.show();
        }
    }
}