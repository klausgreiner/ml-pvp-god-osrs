package com.runemate.party.chocolateknifer;

import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.party.chocolateknifer.ChocolateKniferSettings;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

/**
 * NOTES: Checks if player has chocolate bars and knife for slicing
 */
public class HasChocolateAndKnife extends BranchTask {
    private final ChocolateKniferSettings settings;

    private SliceChocolate sliceChocolate;
    private IsMissingItem isMissingItem = new IsMissingItem();

    public HasChocolateAndKnife(ChocolateKniferSettings settings) {
        this.settings = settings;
        this.sliceChocolate = new SliceChocolate(settings);
    }

    @Override
    public boolean validate() {
        return Inventory.contains("Knife") && Inventory.contains("Chocolate bar");
    }

    @Override
    public TreeTask failureTask() {
        return isMissingItem;
    }

    @Override
    public TreeTask successTask() {
        return sliceChocolate;
    }
}
