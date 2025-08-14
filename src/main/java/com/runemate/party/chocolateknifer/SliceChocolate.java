package com.runemate.party.chocolateknifer;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SliceChocolate extends LeafTask {

    private static final Logger logger = LogManager.getLogger(SliceChocolate.class);
    private final ChocolateKniferSettings settings;

    private long inventoryStartTime = 0;

    public SliceChocolate(ChocolateKniferSettings settings) {
        this.settings = settings;
    }

    @Override
    public void execute() {
        var chocolate = Inventory.newQuery().names("Chocolate bar").results().first();
        var knife = Inventory.newQuery().names("Knife").results().first();

        if (knife != null && chocolate != null) {
            if (inventoryStartTime == 0) {
                inventoryStartTime = System.currentTimeMillis();
                logger.info("Started slicing inventory at: " + inventoryStartTime);
            }

            if (knife.interact("Use")) {
                Execution.delayUntil(() -> Inventory.getSelectedItem() != null, 300);
                if (chocolate.interact("Use")) {
                    logger.info("Slicing chocolate...");
                    Execution.delay(settings.getSliceInterval()); // uses UI setting
                }
            }

            // If no more chocolate in inventory, log the finish time
            if (!Inventory.contains("Chocolate bar")) {
                long finishTime = System.currentTimeMillis();
                long durationMs = finishTime - inventoryStartTime;
                logger.info("Finished slicing inventory at: " + finishTime +
                        " | Duration: " + (durationMs / 1000.0) + " seconds");
                inventoryStartTime = 0; // reset for next round
            }
        }
    }
}
