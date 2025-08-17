package com.runemate.party.pvpgodmode;

import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class GearSwapper {

    private static final Logger logger = LogManager.getLogger(GearSwapper.class);

    // Max/Med Loadout - Melee Gear Set
    private static final int[] MELEE_GEAR_IDS = {
            10828, // Helm of neitiznot
            2412, // Imbued guthix cape
            1704, // Amulet of glory
            4151, // Abyssal whip
            4587, // Dragon dagger
            2503, // Black d'hide body
            1127, // Rune platelegs
            7458, // Barrows gloves
            3105, // Climbing boots
            6737 // Berserker ring
    };

    // Max/Med Loadout - Magic Gear Set
    private static final int[] MAGIC_GEAR_IDS = {
            10828, // Helm of neitiznot
            2412, // Imbued guthix cape
            1704, // Amulet of glory
            4089, // Mystic robe top
            4093, // Mystic robe bottom
            7458, // Barrows gloves
            3105, // Climbing boots
            6737 // Berserker ring
    };

    // Magic weapon (two-handed, replaces weapon + shield)
    private static final int MAGIC_WEAPON_ID = 4710; // Ahrim's staff

    // Ranged weapon and shield
    private static final int RANGED_WEAPON_ID = 9185; // Rune crossbow
    private static final int SHIELD_ID = 1127; // Granite shield

    // Melee weapon
    private static final int MELEE_WEAPON_ID = 4151; // Abyssal whip

    /**
     * Swaps to melee combat setup
     */
    public static boolean executeSwapToMelee() {
        logger.info("Swapping to melee gear setup");

        if (!areAllItemsPresent(MELEE_GEAR_IDS)) {
            logger.warn("Not all melee gear is available. Aborting swap.");
            return false;
        }

        // Equip melee weapon first
        if (!isEquipped(MELEE_WEAPON_ID)) {
            if (!equipItem(MELEE_WEAPON_ID)) {
                logger.error("Failed to equip abyssal whip");
                return false;
            }
            Execution.delay(getRandomDelay());
        }

        // Equip other melee gear
        for (int itemId : MELEE_GEAR_IDS) {
            if (itemId != MELEE_WEAPON_ID && !isEquipped(itemId)) {
                if (equipItem(itemId)) {
                    Execution.delay(getRandomDelay());
                } else {
                    logger.error("Failed to equip item with ID: " + itemId);
                    return false;
                }
            }
        }

        Execution.delay(300);
        logger.info("Melee gear setup completed");
        return true;
    }

    /**
     * Swaps to magic combat setup
     */
    public static boolean executeSwapToMage() {
        logger.info("Swapping to magic gear setup");

        if (!areAllItemsPresent(MAGIC_GEAR_IDS)) {
            logger.warn("Not all magic gear is available. Aborting swap.");
            return false;
        }

        // Equip magic weapon (two-handed)
        if (!isEquipped(MAGIC_WEAPON_ID)) {
            if (!equipItem(MAGIC_WEAPON_ID)) {
                logger.error("Failed to equip Ahrim's staff");
                return false;
            }
            Execution.delay(getRandomDelay());
        }

        // Equip other magic gear
        for (int itemId : MAGIC_GEAR_IDS) {
            if (itemId != MAGIC_WEAPON_ID && !isEquipped(itemId)) {
                if (equipItem(itemId)) {
                    Execution.delay(getRandomDelay());
                } else {
                    logger.error("Failed to equip item with ID: " + itemId);
                    return false;
                }
            }
        }

        Execution.delay(300);
        logger.info("Magic gear setup completed");
        return true;
    }

    /**
     * Swaps to ranged combat setup
     */
    public static boolean executeSwapToRanged() {
        logger.info("Swapping to ranged gear setup");

        if (!areAllItemsPresent(MAGIC_GEAR_IDS)) {
            logger.warn("Not all ranged gear is available. Aborting swap.");
            return false;
        }

        // Equip ranged weapon
        if (!isEquipped(RANGED_WEAPON_ID)) {
            if (!equipItem(RANGED_WEAPON_ID)) {
                logger.error("Failed to equip rune crossbow");
                return false;
            }
            Execution.delay(getRandomDelay());
        }

        // Equip shield
        if (!isEquipped(SHIELD_ID)) {
            if (!equipItem(SHIELD_ID)) {
                logger.error("Failed to equip granite shield");
                return false;
            }
            Execution.delay(getRandomDelay());
        }

        // Equip other ranged gear
        for (int itemId : MAGIC_GEAR_IDS) {
            if (itemId != RANGED_WEAPON_ID && itemId != SHIELD_ID && !isEquipped(itemId)) {
                if (equipItem(itemId)) {
                    Execution.delay(getRandomDelay());
                } else {
                    logger.error("Failed to equip item with ID: " + itemId);
                    return false;
                }
            }
        }

        Execution.delay(300);
        logger.info("Ranged gear setup completed");
        return true;
    }

    /**
     * Quick weapon swap for hybrid combat
     */
    public static boolean quickWeaponSwap(int targetWeaponId) {
        logger.info("Quick weapon swap to ID: " + targetWeaponId);

        if (!equipItem(targetWeaponId)) {
            logger.error("Quick weapon swap failed");
            return false;
        }

        Execution.delay(150);
        return true;
    }

    private static boolean areAllItemsPresent(int[] itemIds) {
        for (int itemId : itemIds) {
            if (!Inventory.contains(itemId) && !Equipment.contains(itemId)) {
                logger.warn("Item with ID {} is not available in inventory or equipment", itemId);
                return false;
            }
        }
        return true;
    }

    private static boolean equipItem(int itemId) {
        logger.info("Equipping item with ID: " + itemId);

        // First check if item is in inventory
        SpriteItem inventoryItem = Inventory.getItems(itemId).first();
        if (inventoryItem != null) {
            if (inventoryItem.interact("Wield")) {
                Execution.delayUntil(() -> isEquipped(itemId), 1200);
                return isEquipped(itemId);
            }
        }

        // If not in inventory, check if it's already equipped
        if (isEquipped(itemId)) {
            return true;
        }

        logger.warn("Failed to equip item with ID: " + itemId);
        return false;
    }

    private static boolean isEquipped(int itemId) {
        return Equipment.contains(itemId);
    }

    private static int getRandomDelay() {
        return (int) (Math.random() * 200) + 100;
    }
}
