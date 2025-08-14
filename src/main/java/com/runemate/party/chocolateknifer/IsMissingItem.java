package com.runemate.party.chocolateknifer;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

/**
 * NOTES: Checks if player has chocolate bars but no knife
 */
public class IsMissingItem extends BranchTask {

    private GetKnifeFromBank getKnifeFromBank = new GetKnifeFromBank();
    private GetChocolateFromBank getChocolateFromBank = new GetChocolateFromBank();

    @Override
    public boolean validate() {
        return Inventory.contains("Chocolate bar") && !Inventory.contains("Knife");
    }

    @Override
    public TreeTask failureTask() {
        return getChocolateFromBank;
    }

    @Override
    public TreeTask successTask() {
        return getKnifeFromBank;
    }
}
