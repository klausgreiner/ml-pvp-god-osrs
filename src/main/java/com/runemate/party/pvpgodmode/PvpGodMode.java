package com.runemate.party.pvpgodmode;

import com.runemate.game.api.script.framework.tree.TreeBot;
import com.runemate.game.api.script.framework.tree.TreeTask;
import com.runemate.game.api.script.framework.listeners.SettingsListener;
import com.runemate.game.api.script.framework.listeners.events.SettingChangedEvent;
import com.runemate.ui.setting.annotation.open.SettingsProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PvpGodMode extends TreeBot implements SettingsListener {

    private static final Logger logger = LogManager.getLogger(PvpGodMode.class);

    @SettingsProvider(updatable = true)
    private PvpGodModeSettings settings;

    private boolean started = false;
    private PvpCombatNode currentCombatNode;
    private PvpGodModeController uiController;
    private PvpGodModeUI uiLauncher;

    @Override
    public TreeTask createRootTask() {
        if (settings == null) {
            logger.warn("Settings not loaded yet, creating default settings");
            currentCombatNode = new PvpCombatNode(new DefaultPvpGodModeSettings());
            return currentCombatNode;
        }

        if (!started) {
            logger.info("Bot not started yet, creating combat node with current settings");
        }

        currentCombatNode = new PvpCombatNode(settings);
        return currentCombatNode;
    }

    @Override
    public void onStart(String... arguments) {
        super.onStart(arguments);
        getEventDispatcher().addListener(this);

        // Initialize UI launcher
        uiLauncher = new PvpGodModeUI(this);

        logger.info("PVP God Mode started successfully - waiting for settings confirmation");
        logger.info("Use showPredictionsUI() method to display bot predictions interface");
    }

    @Override
    public void onSettingChanged(SettingChangedEvent settingChangedEvent) {
        logger.info("Setting changed: {}", settingChangedEvent.getKey());
    }

    @Override
    public void onSettingsConfirmed() {
        if (settings != null) {
            started = true;
            logger.info("Settings confirmed, bot is now active!");
            logger.info("RL Model enabled: {}", settings.getRlModelEnabled());
            updateStatus(true);
            updateModelStatus(settings.getRlModelEnabled());
        } else {
            logger.warn("Settings confirmed but settings object is still null!");
        }
    }

    @Override
    public void onStop() {
        logger.info("PVP God Mode stopped");

        if (currentCombatNode != null) {
            PvpRlModel rlModel = currentCombatNode.getRlModel();
            if (rlModel != null) {
                logger.info("Closing RL model connection");
                rlModel.closeConnection();
            }
        }

        // Hide UI if visible
        if (uiLauncher != null) {
            uiLauncher.hideUI();
        }

        super.onStop("0");
    }

    public void showPredictionsUI() {
        if (uiLauncher != null) {
            uiLauncher.showUI();
            logger.info("Bot predictions UI displayed");
        } else {
            logger.warn("UI launcher not initialized");
        }
    }

    public void hidePredictionsUI() {
        if (uiLauncher != null) {
            uiLauncher.hideUI();
            logger.info("Bot predictions UI hidden");
        }
    }

    public void updatePrediction(String prediction) {
        if (uiController != null) {
            uiController.addPrediction(prediction);
        }
        if (uiLauncher != null) {
            uiLauncher.updatePrediction(prediction);
        }
        logger.info("Bot prediction: {}", prediction);
    }

    public void updateStatus(boolean active) {
        if (uiController != null) {
            uiController.updateStatus(active);
        }
        if (uiLauncher != null) {
            uiLauncher.updateStatus(active);
        }
        logger.info("Bot status updated: {}", active ? "Active" : "Inactive");
    }

    public void updateModelStatus(boolean enabled) {
        if (uiController != null) {
            uiController.updateModelStatus(enabled);
        }
        if (uiLauncher != null) {
            uiLauncher.updateModelStatus(enabled);
        }
        logger.info("RL Model status updated: {}", enabled ? "Enabled" : "Disabled");
    }

    public boolean isStarted() {
        return started;
    }

    public PvpGodModeSettings getPvpSettings() {
        return settings;
    }

    public void setUiController(PvpGodModeController controller) {
        this.uiController = controller;
    }

    private static class DefaultPvpGodModeSettings implements PvpGodModeSettings {
        @Override
        public boolean getRlModelEnabled() {
            return false;
        }

        @Override
        public String getModelPath() {
            return "FineTunedNh";
        }
    }
}
