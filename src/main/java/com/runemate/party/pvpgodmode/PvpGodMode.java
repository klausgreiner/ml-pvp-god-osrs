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
        logger.info("PVP God Mode started successfully - waiting for settings confirmation");
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
        } else {
            logger.warn("Settings confirmed but settings object is still null!");
        }
    }

    @Override
    public void onStop() {
        logger.info("PVP God Mode stopped");

        // Close the RL model connection if it exists
        if (currentCombatNode != null) {
            PvpRlModel rlModel = currentCombatNode.getRlModel();
            if (rlModel != null) {
                logger.info("Closing RL model connection");
                rlModel.closeConnection();
            }
        }

        super.onStop("0");
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
