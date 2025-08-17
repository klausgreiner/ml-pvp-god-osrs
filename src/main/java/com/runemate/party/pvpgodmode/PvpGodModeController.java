package com.runemate.party.pvpgodmode;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PvpGodModeController implements Initializable {

    @FXML
    private Label statusLabel;

    @FXML
    private Label modelLabel;

    @FXML
    private Label predictionCountLabel;

    @FXML
    private TextArea predictionsArea;

    @FXML
    private Button clearButton;

    @FXML
    private Button refreshButton;

    private final PvpGodMode script;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public PvpGodModeController(PvpGodMode script) {
        this.script = script;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        predictionsArea.setText("Waiting for predictions...\n\n" +
                "The bot will display real-time predictions here\n" +
                "when the RL model is active and making decisions.");

        updateStatus(false);
        updateModelStatus(false);
        updatePredictionCount(0);
    }

    @FXML
    public void onClearPredictions() {
        predictionsArea.clear();
        predictionsArea.setText("Predictions cleared at " +
                LocalDateTime.now().format(TIME_FORMATTER) + "\n\n" +
                "Waiting for new predictions...");
        updatePredictionCount(0);
    }

    @FXML
    public void onRefreshStatus() {
        if (script != null) {
            script.updateStatus(script.isStarted());
            script.updateModelStatus(script.getPvpSettings() != null && script.getPvpSettings().getRlModelEnabled());
        }
    }

    public void addPrediction(String prediction) {
        if (prediction == null || prediction.trim().isEmpty()) {
            return;
        }

        Platform.runLater(() -> {
            String timestampedPrediction = String.format("[%s] %s\n",
                    LocalDateTime.now().format(TIME_FORMATTER), prediction);

            predictionsArea.appendText(timestampedPrediction);

            // Auto-scroll to bottom
            predictionsArea.setScrollTop(Double.MAX_VALUE);

            // Update prediction count
            String[] lines = predictionsArea.getText().split("\n");
            int predictionLines = 0;
            for (String line : lines) {
                if (line.matches("\\[\\d{2}:\\d{2}:\\d{2}\\].*")) {
                    predictionLines++;
                }
            }
            updatePredictionCount(predictionLines);
        });
    }

    public void updateStatus(boolean active) {
        Platform.runLater(() -> {
            statusLabel.setText(active ? "Active" : "Inactive");
            statusLabel.setStyle(active ? "-fx-text-fill: #00ff00;" : "-fx-text-fill: #ff4444;");
        });
    }

    public void updateModelStatus(boolean enabled) {
        Platform.runLater(() -> {
            modelLabel.setText(enabled ? "Enabled" : "Disabled");
            modelLabel.setStyle(enabled ? "-fx-text-fill: #00ff00;" : "-fx-text-fill: #ffaa00;");
        });
    }

    private void updatePredictionCount(int count) {
        Platform.runLater(() -> {
            predictionCountLabel.setText(String.valueOf(count));
        });
    }

    public void clearPredictions() {
        Platform.runLater(() -> {
            predictionsArea.clear();
            predictionsArea.setText("Predictions cleared at " +
                    LocalDateTime.now().format(TIME_FORMATTER) + "\n\n" +
                    "Waiting for new predictions...");
            updatePredictionCount(0);
        });
    }
}
