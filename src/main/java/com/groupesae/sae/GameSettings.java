package com.groupesae.sae;

public class GameSettings {
    private String windowSize = "800x600";
    private String controls = "Flèches directionnelles";
    private int gridSize = 10;
    private String difficulty = "Moyen";

    public String getWindowSize() { return windowSize; }
    public void setWindowSize(String windowSize) { this.windowSize = windowSize; }

    public String getControls() { return controls; }
    public void setControls(String controls) { this.controls = controls; }

    public int getGridSize() { return gridSize; }
    public void setGridSize(int gridSize) { this.gridSize = gridSize; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}