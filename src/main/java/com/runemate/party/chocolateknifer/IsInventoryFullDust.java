package com.runemate.party.chocolateknifer;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;
import com.runemate.party.chocolateknifer.SliceChocolate;
import com.runemate.party.chocolateknifer.StoreDust;

/**
 * NOTES: Checks if inventory is full and routes to appropriate action
 */
public class IsInventoryFullDust extends BranchTask {

    private final ChocolateKniferSettings settings;
    private StoreDust storeDust = new StoreDust();
    private HasChocolateAndKnife hasChocolateAndKnife;

    public IsInventoryFullDust(ChocolateKniferSettings settings) {
        this.settings = settings;
        this.hasChocolateAndKnife = new HasChocolateAndKnife(settings);
    }

    @Override
    public boolean validate() {
        return !Inventory.contains("Chocolate bar") && Inventory.contains("Chocolate dust");
    }

    @Override
    public TreeTask failureTask() {
        return hasChocolateAndKnife;
    }

    @Override
    public TreeTask successTask() {
        return storeDust;
    }
}
