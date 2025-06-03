package com.groupesae.sae;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainMenuScreen {

    private Button playButton;
    private Button exitButton;
    private Stage primaryStage;

    public MainMenuScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setHeight(700);
        primaryStage.setWidth(800);

        playButton = new Button("Jouer");
        playButton.setPrefSize(150, 50);

        exitButton = new Button("Quitter");
        exitButton.setPrefSize(120, 40);

        setupButtonActions();
    }

    private void setupButtonActions() {
        playButton.setOnAction(event -> {
            GridCustomizationScreen gridCustomizationScreen = new GridCustomizationScreen(primaryStage);
            primaryStage.setScene(gridCustomizationScreen.getScene());
        });

        exitButton.setOnAction(event -> {
            System.exit(0);
        });
    }

    public Button getPlayButton() {
        return playButton;
    }

    public Button getExitButton() {
        return exitButton;
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Mange moi si tu peux !");
        titleLabel.setFont(new Font(28));
        centerBox.getChildren().addAll(titleLabel, playButton);
        root.setCenter(centerBox);

        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.BOTTOM_CENTER);
        bottomBox.setPadding(new Insets(20, 0, 20, 0));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomBox.getChildren().addAll( spacer, exitButton);
        root.setBottom(bottomBox);

        playButton.setStyle("-fx-font-size: 18px;");
        exitButton.setStyle("-fx-font-size: 14px;");

        return new Scene(root, 800, 700);
    }
}