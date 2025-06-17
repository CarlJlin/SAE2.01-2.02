package com.groupesae.sae;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.control.ComboBox;

public class GameModeScreen {
    private Stage primaryStage;
    private Grille grilleChargee = null;
    private Label fileStatusLabel;

    private ComboBox<String> mazeTypeCombo = new ComboBox<>();
    protected ComboBox<String> pathfindingCombo = new ComboBox<>();


    // Définir une correspondance entre caractères et éléments du jeu
    private static final Map<Character, Integer> CONVERSION_CARACTERES = new HashMap<Character, Integer>() {{
        put('-', Grille.ROCHER);
        put('H', Grille.HERBE);
        put('M', Grille.MARGUERITE);
        put('C', Grille.CACTUS);
        put('S', Grille.MOUTON);
        put('L', Grille.LOUP);
        // Conversion numérique également supportée
        put('0', Grille.HERBE);
        put('1', Grille.MARGUERITE);
        put('2', Grille.CACTUS);
        put('8', Grille.MOUTON);
        put('9', Grille.LOUP);
    }};

    public GameModeScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.fileStatusLabel = new Label("Aucun fichier chargé");
        mazeTypeCombo.getItems().addAll("Labyrinthe parfait", "Labyrinthe imparfait");
        mazeTypeCombo.setValue("Labyrinthe parfait");
        pathfindingCombo.getItems().addAll("Dijkstra", "A*");
        pathfindingCombo.setValue("Dijkstra");

    }

    private void setupButtonActions(Stage primaryStage, Button manualButton, Button automaticButton, Button loadButton, Button backButton) {
        loadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir un fichier de labyrinthe");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers texte", "*.txt")
            );

            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    grilleChargee = chargerGrilleDepuisFichier(selectedFile.getPath());
                    if (grilleChargee != null) {
                        fileStatusLabel.setText("Fichier chargé : " + selectedFile.getName());
                        fileStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
                    } else {
                        fileStatusLabel.setText("Erreur : impossible de charger le fichier");
                        fileStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
                    }
                } catch (Exception e) {
                    fileStatusLabel.setText("Erreur : format de fichier invalide");
                    fileStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
                    grilleChargee = null;
                    e.printStackTrace();
                }
            }
        });

        manualButton.setOnAction(event -> {
            if (grilleChargee != null) {
                // Utiliser la grille chargée en mode manuel
                GamePlayScreen gamePlayScreen = new GamePlayScreen(primaryStage, grilleChargee, false, pathfindingCombo.getValue());
                primaryStage.setScene(gamePlayScreen.getScene());
            } else {
                // Comportement normal sans fichier chargé
                GridCustomizationScreen gridCustomizationScreen = new GridCustomizationScreen(primaryStage);
                primaryStage.setScene(gridCustomizationScreen.getScene());
            }
        });

        automaticButton.setOnAction(event -> {
            automaticButton.setDisable(true);
            automaticButton.setText("Génération...");

            final Grille[] grid = new Grille[1];
            Thread task = new Thread(() -> {
                try {
                    if (grilleChargee != null) {
                        System.out.println("Utilisation de la grille chargée depuis fichier");
                        // Créer une copie de la grille chargée pour éviter les modifications
                        grid[0] = creerCopieGrille(grilleChargee);
                    } else {
                        System.out.println("Génération d'une nouvelle grille aléatoire");
                        grid[0] = new Grille(11, 11, true);
                        if (mazeTypeCombo.getValue().equals("Labyrinthe imparfait")) {
                            grid[0].genererLabyrintheImparfait(0.25);
                        } else {
                            grid[0].genererLabyrinthe();
                        }

                    }
                    javafx.application.Platform.runLater(() -> {
                        try {
                            GamePlayScreen gamePlayScreen = new GamePlayScreen(primaryStage, grid[0], true, pathfindingCombo.getValue());
                            primaryStage.setScene(gamePlayScreen.getScene());
                        } catch (Exception e) {
                            System.err.println("Erreur lors de la création de l'écran de jeu: " + e.getMessage());
                            e.printStackTrace();
                            automaticButton.setDisable(false);
                            automaticButton.setText("Mode Automatique");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Erreur lors de la génération de la grille: " + e.getMessage());
                    e.printStackTrace();
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

    private Grille creerCopieGrille(Grille original) {
        int largeur = original.getX();
        int hauteur = original.getY();

        Grille copie = new Grille(largeur, hauteur, true);
        int[][] grilleOriginale = original.getGrille();
        int[][] grilleCopie = copie.getGrille();

        // Copier toutes les cellules
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                grilleCopie[y][x] = grilleOriginale[y][x];
            }
        }

        return copie;
    }

    private Grille chargerGrilleDepuisFichier(String cheminFichier) {
        try {
            List<String> lignes = Files.readAllLines(Path.of(cheminFichier));
            System.out.println("Nombre de lignes lues: " + lignes.size());

            // Filtrer les lignes vides
            lignes = lignes.stream()
                    .map(String::trim)
                    .filter(ligne -> !ligne.isEmpty())
                    .collect(Collectors.toList());

            if (lignes.isEmpty()) {
                System.err.println("Aucune ligne valide trouvée dans le fichier");
                return null;
            }

            int largeur = lignes.getFirst().length();
            int hauteur = lignes.size();

            System.out.println("Dimensions de la grille: " + largeur + "x" + hauteur);

            Grille grille = new Grille(largeur, hauteur, true);
            int[][] grilleData = grille.getGrille();

            for (int y = 0; y < hauteur; y++) {
                for (int x = 0; x < largeur; x++) {
                    grilleData[y][x] = Grille.HERBE;
                }
            }

            // Position du mouton et du loup pour vérification
            boolean moutonTrouve = false, loupTrouve = false, sortieTrouvee = false;

            for (int y = 0; y < hauteur; y++) {
                String ligne = lignes.get(y);
                System.out.println("Ligne " + y + ": '" + ligne + "'");

                for (int x = 0; x < Math.min(largeur, ligne.length()); x++) {
                    char c = ligne.charAt(x);

                    switch (c) {
                        case 'x':
                        case 'X':
                            grilleData[y][x] = Grille.ROCHER;
                            break;
                        case 'h':
                        case 'H':
                            grilleData[y][x] = Grille.HERBE;
                            break;
                        case 'f':
                        case 'F':
                            grilleData[y][x] = Grille.MARGUERITE;
                            break;
                        case 'c':
                        case 'C':
                            grilleData[y][x] = Grille.CACTUS;
                            break;
                        case 'm':
                        case 'M':
                            grilleData[y][x] = Grille.MOUTON;
                            moutonTrouve = true;
                            System.out.println("Mouton trouvé en position: (" + x + ", " + y + ")");
                            break;
                        case 'l':
                        case 'L':
                            grilleData[y][x] = Grille.LOUP;
                            loupTrouve = true;
                            System.out.println("Loup trouvé en position: (" + x + ", " + y + ")");
                            break;
                        case 's':
                        case 'S':
                            // Marquer la sortie comme de l'herbe ET l'enregistrer comme sortie
                            grilleData[y][x] = Grille.HERBE;
                            // Ajouter la sortie à la liste des sorties de la grille
                            if (grille.getSorties() != null) {
                                grille.getSorties().add(new int[]{x, y});
                            }
                            sortieTrouvee = true;
                            System.out.println("Sortie trouvée en position: (" + x + ", " + y + ")");
                            break;
                        case ' ':
                        case '.':
                            grilleData[y][x] = Grille.HERBE;
                            break;
                        default:
                            // Vérifier si c'est un caractère de la map de conversion
                            if (CONVERSION_CARACTERES.containsKey(c)) {
                                grilleData[y][x] = CONVERSION_CARACTERES.get(c);
                                if (c == 'S' || c == '8') moutonTrouve = true;
                                if (c == 'L' || c == '9') loupTrouve = true;
                            } else {
                                System.out.println("Caractère non reconnu '" + c + "' en position (" + x + ", " + y + "), remplacé par herbe");
                                grilleData[y][x] = Grille.HERBE;
                            }
                            break;
                    }
                }
            }

            System.out.println("Mouton trouvé: " + moutonTrouve + ", Loup trouvé: " + loupTrouve + ", Sortie trouvée: " + sortieTrouvee);

            // Vérifier qu'il y a au moins une sortie possible sur les bords (pas les coins)
            boolean sortiesPossibles = false;

            // Vérifier les bords horizontaux (haut et bas), sans les coins
            for (int x = 1; x < largeur - 1; x++) {
                if (grilleData[0][x] != Grille.ROCHER || grilleData[hauteur-1][x] != Grille.ROCHER) {
                    sortiesPossibles = true;
                    break;
                }
            }

            // Vérifier les bords verticaux (gauche et droite), sans les coins
            if (!sortiesPossibles) {
                for (int y = 1; y < hauteur - 1; y++) {
                    if (grilleData[y][0] != Grille.ROCHER || grilleData[y][largeur-1] != Grille.ROCHER) {
                        sortiesPossibles = true;
                        break;
                    }
                }
            }

            if (!sortiesPossibles) {
                System.err.println("Attention: Aucune sortie possible détectée sur les bords (hors coins)");
            }

            if (!moutonTrouve || !loupTrouve) {
                System.err.println("Attention: Le mouton ou le loup n'ont pas été trouvés dans le fichier");
            }

            System.out.println("Grille chargée avec succès. Sorties possibles: " + sortiesPossibles);

            return grille;
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors du chargement: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Scene getScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Titre
        Text titleText = new Text("Sélection du mode de jeu");
        titleText.setFont(new Font(24));
        StackPane titlePane = new StackPane(titleText);
        titlePane.setPadding(new Insets(0, 0, 30, 0));

        // Bouton de chargement
        Button loadButton = new Button("Charger un labyrinthe");
        loadButton.setPrefSize(200, 40);
        loadButton.setStyle("-fx-font-size: 14px;");
        fileStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        // Zone de chargement
        VBox fileBox = new VBox(10, loadButton, fileStatusLabel);
        fileBox.setAlignment(Pos.CENTER);
        fileBox.setPadding(new Insets(0, 0, 20, 0));

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
        mainLayout.getChildren().addAll(titlePane, mazeTypeCombo, pathfindingCombo, fileBox, buttonsBox, bottomBox);
        root.setCenter(mainLayout);

        setupButtonActions(primaryStage, manualButton, automaticButton, loadButton, backButton);

        return new Scene(root, 1300, 600);
    }

    public String getAlgo() {
        return pathfindingCombo.getValue();
    }
}