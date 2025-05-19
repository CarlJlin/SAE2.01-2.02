package com.groupesae.sae;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class MainMenuScreen extends BorderPane {

    private App app;

    public MainMenuScreen(App app) {
        this.app = app;

        Text title = new Text("Jeu du Loup et du Mouton");
        title.setFont(new Font("Arial Bold", 36));
        title.setTextAlignment(TextAlignment.CENTER);

        Button playButton = new Button("JOUER");
        playButton.setPrefSize(200, 60);
        playButton.getStyleClass().add("menu-button");
        playButton.setOnAction(e -> app.showGameScreen());

        VBox centerBox = new VBox(40);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(title, playButton);

        Button optionsButton = new Button("Options");
        optionsButton.setPrefSize(120, 40);
        optionsButton.setOnAction(e -> app.showOptionsScreen());

        Button quitButton = new Button("Quitter");
        quitButton.setPrefSize(120, 40);
        quitButton.setOnAction(e -> app.exit());

        HBox bottomRightBox = new HBox(10);
        bottomRightBox.setAlignment(Pos.BOTTOM_RIGHT);
        bottomRightBox.setPadding(new Insets(20));
        bottomRightBox.getChildren().addAll(optionsButton, quitButton);

        setCenter(centerBox);
        setBottom(bottomRightBox);
    }
}