
package com.runemate.party.chocolateknifer;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.framework.tree.TreeBot;
import com.runemate.game.api.script.framework.tree.TreeTask;
import com.runemate.game.api.script.framework.listeners.SettingsListener;
import com.runemate.game.api.script.framework.listeners.events.SettingChangedEvent;
import com.runemate.ui.setting.annotation.open.SettingsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Chocolate Knifer TreeBot
 * 
 * DIRECTIONS:
 * - Bot slices chocolate bars into chocolate dust at Grand Exchange
 * - Uses knife to slice chocolate bars at 3 times per second rate
 * - Automatically manages inventory and bank operations
 * 
 * CURRENT PROBLEMS:
 * - None
 * 
 * RECENT CHANGES:
 * - Converted to TreeBot architecture
 * 
 * UPCOMING IMPROVEMENTS:
 * - breaks
 * -
 */
public class ChocolateKnifer extends TreeBot implements SettingsListener {

    private static final Logger logger = LogManager.getLogger(ChocolateKnifer.class);

    @SettingsProvider(updatable = true)
    private ChocolateKniferSettings settings;

    private static final Coordinate GE_CENTER = new Coordinate(3165, 3487, 0);
    private static final int GE_DISTANCE_THRESHOLD = 20;

    private boolean started = false;

    @Override
    public TreeTask createRootTask() {
        if (!started) {
            return null;
        }
        return new IsInventoryFullDust(settings);
    }

    @Override
    public void onStart(String... arguments) {
        super.onStart(arguments);
        getEventDispatcher().addListener(this);

        if (!isNearGrandExchange()) {
            logger.warn("Not near Grand Exchange! Please move to GE area.");
            return;
        }

        logger.info("Chocolate Knifer TreeBot started successfully");
    }

    @Override
    public void onSettingChanged(SettingChangedEvent settingChangedEvent) {
        // Handle setting changes if needed
    }

    @Override
    public void onSettingsConfirmed() {
        started = true;
        logger.info("Settings confirmed, bot is now active");
    }

    private boolean isNearGrandExchange() {
        var player = Players.getLocal();
        if (player == null) {
            return false;
        }

        double distance = Distance.between(player, GE_CENTER);
        return distance <= GE_DISTANCE_THRESHOLD;
    }
}
