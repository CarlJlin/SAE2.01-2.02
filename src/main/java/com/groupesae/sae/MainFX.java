package com.groupesae.sae;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainFX extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mange moi si tu peux !");
        MainMenuScreen mainMenuScreen = new MainMenuScreen(primaryStage);
        primaryStage.setScene(mainMenuScreen.getScene());
        primaryStage.setWidth(1100);
        primaryStage.setHeight(600);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            System.exit(0);
        });

        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(1300);
        primaryStage.setMinHeight(600);

        primaryStage.show();
    }
}