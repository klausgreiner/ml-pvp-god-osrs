package com.runemate.party.chocolateknifer;

import com.runemate.game.api.script.framework.tree.TreeBot;
import com.runemate.game.api.script.framework.tree.TreeTask;
import com.runemate.ui.setting.annotation.open.SettingsProvider;

/**
 * Chocolate Knifer TreeBot
 * 
 * DIRECTIONS:
 * - Bot slices chocolate bars into chocolate dust at Grand Exchange
 * - Uses knife to slice chocolate bars at 2 times per second rate
 * - Automatically manages inventory and bank operations
 * 
 * CURRENT PROBLEMS:
 * - None
 * 
 * RECENT CHANGES:
 * - Converted to TreeBot architecture
 * 
 * UPCOMING IMPROVEMENTS:
 * - Add more chocolate types
 * - Optimize slicing speed
 */
public class ChocolateKniferTreeBot extends TreeBot {

    @SettingsProvider(updatable = true)
    private ChocolateKniferSettings settings;

    @Override
    public TreeTask createRootTask() {
        return new IsInventoryFullDust(settings);
    }
}
