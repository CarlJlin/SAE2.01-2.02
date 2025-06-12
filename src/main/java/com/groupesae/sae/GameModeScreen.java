package com.groupesae.sae;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;


public class GameModeScreen {
    private Stage primaryStage;

    public GameModeScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void setupButtonActions(Stage primaryStage, Button manualButton, Button automaticButton, Button backButton) {
        manualButton.setOnAction(event -> {
            GridCustomizationScreen gridCustomizationScreen = new GridCustomizationScreen(primaryStage);
            primaryStage.setScene(gridCustomizationScreen.getScene());
        });

        automaticButton.setOnAction(event -> {

            // Désactiver le bouton pendant le chargement
            automaticButton.setDisable(true);
            automaticButton.setText("Chargement...");
            System.out.println("Génération de la grille en cours...");

            // Créer et exécuter la tâche dans un thread séparé
            Thread task = new Thread(() -> {
                try {
                    Grille grid = new Grille(11, 11, true);
                    System.out.println("Grille générée avec succès");

                    // Revenir au thread JavaFX pour mettre à jour l'interface
                    javafx.application.Platform.runLater(() -> {
                        try {
                            GamePlayScreen gamePlayScreen = new GamePlayScreen(primaryStage, grid, true);
                            primaryStage.setScene(gamePlayScreen.getScene());
                        } catch (Exception e) {
                            System.err.println("Erreur lors de la création de l'écran de jeu: " + e.getMessage());
                            e.printStackTrace();

                            // Réactiver le bouton en cas d'erreur
                            automaticButton.setDisable(false);
                            automaticButton.setText("Mode Automatique");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Erreur lors de la génération de la grille: " + e.getMessage());
                    e.printStackTrace();

                    // Réactiver le bouton en cas d'erreur
                    javafx.application.Platform.runLater(() -> {
                        automaticButton.setDisable(false);
                        automaticButton.setText("Mode Automatique");
                    });
                }
            });

            task.setDaemon(true);
            task.start();
        });

        backButton.setOnAction(event -> {
            MainMenuScreen mainMenuScreen = new MainMenuScreen(primaryStage);
            primaryStage.setScene(mainMenuScreen.getScene());
        });
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Titre
        Text titleText = new Text("Sélection du mode de jeu");
        titleText.setFont(new Font(24));
        StackPane titlePane = new StackPane(titleText);
        titlePane.setPadding(new Insets(0, 0, 30, 0));

        // Boutons des modes
        Button manualButton = new Button("Mode Manuel");
        manualButton.setPrefSize(200, 60);
        manualButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button automaticButton = new Button("Mode Automatique");
        automaticButton.setPrefSize(200, 60);
        automaticButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox buttonsBox = new VBox(20, manualButton, automaticButton);
        buttonsBox.setAlignment(Pos.CENTER);

        // Bouton retour
        Button backButton = new Button("Retour");
        backButton.setPrefSize(120, 40);
        backButton.setStyle("-fx-font-size: 14px;");
        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(30, 0, 0, 0));

        // Layout principal
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(titlePane, buttonsBox, bottomBox);

        root.setCenter(mainLayout);

        setupButtonActions(primaryStage, manualButton, automaticButton, backButton);

        return new Scene(root, 1400, 800);
    }
}