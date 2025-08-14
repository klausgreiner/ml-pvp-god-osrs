package com.runemate.party.chocolateknifer;

import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * NOTES: Withdraws knife from bank
 */
public class GetKnifeFromBank extends LeafTask {

    private static final Logger logger = LogManager.getLogger(GetKnifeFromBank.class);

    @Override
    public void execute() {
        if (!Bank.isOpen()) {
            Bank.open();
            return;
        }

        if (!Bank.contains("Knife")) {
            logger.warn("No knives in bank!");
            return;
        }

        Bank.withdraw("Knife", 1);
        logger.info("Withdrew 1 knife");
        Execution.delay(500);
        Bank.close();
    }
}
