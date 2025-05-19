package com.groupesae.sae;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class OptionsScreen extends BorderPane {

    private App app;
    private GameSettings settings;

    public OptionsScreen(App app, GameSettings settings) {
        this.app = app;
        this.settings = settings;

        Text title = new Text("Options");
        title.setFont(new Font("Arial Bold", 28));

        GridPane optionsGrid = new GridPane();
        optionsGrid.setHgap(20);
        optionsGrid.setVgap(15);
        optionsGrid.setPadding(new Insets(30));
        optionsGrid.setAlignment(Pos.CENTER);

        Label windowSizeLabel = new Label("Taille de la fenêtre:");
        ComboBox<String> windowSizeComboBox = new ComboBox<>();
        windowSizeComboBox.getItems().addAll("800x600", "1024x768", "1280x720");
        windowSizeComboBox.setValue(settings.getWindowSize());
        windowSizeComboBox.setOnAction(e -> {
            settings.setWindowSize(windowSizeComboBox.getValue());
            String[] dimensions = windowSizeComboBox.getValue().split("x");
            app.resizeWindow(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]));
        });


        Label controlsLabel = new Label("Contrôles:");
        ComboBox<String> controlsComboBox = new ComboBox<>();
        controlsComboBox.getItems().addAll("Flèches directionnelles", "ZQSD", "WASD");
        controlsComboBox.setValue(settings.getControls());
        controlsComboBox.setOnAction(e -> settings.setControls(controlsComboBox.getValue()));

        Label gridSizeLabel = new Label("Taille de la grille:");
        Slider gridSlider = new Slider(5, 20, settings.getGridSize());
        gridSlider.setShowTickMarks(true);
        gridSlider.setShowTickLabels(true);
        gridSlider.setMajorTickUnit(5);
        gridSlider.setMinorTickCount(4);
        gridSlider.setSnapToTicks(true);
        gridSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                settings.setGridSize(newVal.intValue()));

        Label difficultyLabel = new Label("Difficulté:");
        ToggleGroup difficultyGroup = new ToggleGroup();
        RadioButton easyButton = new RadioButton("Facile");
        RadioButton mediumButton = new RadioButton("Moyen");
        RadioButton hardButton = new RadioButton("Difficile");

        easyButton.setToggleGroup(difficultyGroup);
        mediumButton.setToggleGroup(difficultyGroup);
        hardButton.setToggleGroup(difficultyGroup);

        HBox difficultyBox = new HBox(20);
        difficultyBox.getChildren().addAll(easyButton, mediumButton, hardButton);

        switch (settings.getDifficulty()) {
            case "Facile": easyButton.setSelected(true); break;
            case "Moyen": mediumButton.setSelected(true); break;
            case "Difficile": hardButton.setSelected(true); break;
        }

        difficultyGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            RadioButton selectedButton = (RadioButton) newVal;
            settings.setDifficulty(selectedButton.getText());
        });


        optionsGrid.add(windowSizeLabel, 0, 0);
        optionsGrid.add(windowSizeComboBox, 1, 0);
        optionsGrid.add(controlsLabel, 0, 1);
        optionsGrid.add(controlsComboBox, 1, 1);
        optionsGrid.add(gridSizeLabel, 0, 2);
        optionsGrid.add(gridSlider, 1, 2);
        optionsGrid.add(difficultyLabel, 0, 3);
        optionsGrid.add(difficultyBox, 1, 3);

        Button backButton = new Button("Retour au menu");
        backButton.setOnAction(e -> app.showMainMenu());

        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        bottomBox.setPadding(new Insets(20));

        setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(30));

        setCenter(optionsGrid);
        setBottom(bottomBox);
    }
}