package com.runemate.party.chocolateknifer;

import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * NOTES: Withdraws chocolate bars from bank and knife if needed
 */
public class GetChocolateFromBank extends LeafTask {

    private static final Logger logger = LogManager.getLogger(GetChocolateFromBank.class);

    @Override
    public void execute() {
        if (!Bank.isOpen()) {
            Bank.open();
            return;
        }

        if (!Bank.contains("Chocolate bar")) {
            logger.warn("No chocolate bars in bank! Stopping the bot.");
            // Stop the bot by throwing an exception or using your framework's stop method
            throw new IllegalStateException("No chocolate bars available, stopping bot.");
        }

        // Withdraw chocolate bars
        Bank.withdraw("Chocolate bar", 27);
        logger.info("Withdrew 27 chocolate bars");

        Execution.delay(500);
        Bank.close();
    }
}
