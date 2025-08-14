package com.runemate.party.chocolateknifer;

import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * NOTES: Stores chocolate dust in bank when inventory is full
 */
public class StoreDust extends LeafTask {

    private static final Logger logger = LogManager.getLogger(StoreDust.class);

    @Override
    public void execute() {
        if (!Bank.isOpen()) {
            Bank.open();
            return;
        }

        if (Inventory.contains("Chocolate dust")) {
            if (Bank.deposit("Chocolate dust", 0)) {
                logger.info("Stored all chocolate dust");
                Execution.delay(500);
            }
        }
    }
}
