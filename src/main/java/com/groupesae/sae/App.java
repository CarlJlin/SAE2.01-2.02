package com.groupesae.sae;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fenêtre JavaFX");
        primaryStage.setScene(new Scene(new Label("Application lancée !"), 300, 200));
        primaryStage.show();
    }
}
