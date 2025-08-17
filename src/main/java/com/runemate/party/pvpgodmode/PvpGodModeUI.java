package com.runemate.party.pvpgodmode;

import com.runemate.game.api.hybrid.util.Resources;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class PvpGodModeUI {
    
    private Stage uiStage;
    private PvpGodModeController controller;
    private final PvpGodMode script;
    
    public PvpGodModeUI(PvpGodMode script) {
        this.script = script;
    }
    
    public void showUI() {
        if (uiStage != null && uiStage.isShowing()) {
            uiStage.requestFocus();
            return;
        }
        
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setController(new PvpGodModeController(script));
                
                Scene scene = new Scene(loader.load(
                    Resources.getAsStream("com/runemate/party/pvpgodmode/PvpGodModeUI.fxml")
                ));
                
                controller = loader.getController();
                script.setUiController(controller);
                
                uiStage = new Stage();
                uiStage.setTitle("PVP God Mode - Bot Predictions");
                uiStage.setScene(scene);
                uiStage.setResizable(false);
                uiStage.initStyle(StageStyle.DECORATED);
                
                uiStage.setOnCloseRequest(event -> {
                    event.consume();
                    hideUI();
                });
                
                uiStage.show();
                
            } catch (IOException e) {
                showError("Failed to load UI", "Error loading bot prediction interface: " + e.getMessage());
            }
        });
    }
    
    public void hideUI() {
        if (uiStage != null && uiStage.isShowing()) {
            Platform.runLater(() -> {
                uiStage.hide();
                script.setUiController(null);
            });
        }
    }
    
    public void updatePrediction(String prediction) {
        if (controller != null) {
            controller.addPrediction(prediction);
        }
    }
    
    public void updateStatus(boolean active) {
        if (controller != null) {
            controller.updateStatus(active);
        }
    }
    
    public void updateModelStatus(boolean enabled) {
        if (controller != null) {
            controller.updateModelStatus(enabled);
        }
    }
    
    private void showError(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    public boolean isVisible() {
        return uiStage != null && uiStage.isShowing();
    }
}
