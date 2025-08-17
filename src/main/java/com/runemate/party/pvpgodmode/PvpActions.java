package com.runemate.party.pvpgodmode;

import com.runemate.game.api.hybrid.entities.Actor;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Varps;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.osrs.local.hud.interfaces.Prayer;

import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.script.Execution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PvpActions {

    private static final Logger logger = LogManager.getLogger(PvpActions.class);

    public static class Action {
        private final int index;
        private final String name;
        private final String description;
        private final boolean available;

        public Action(int index, String name, String description, boolean available) {
            this.index = index;
            this.name = name;
            this.description = description;
            this.available = available;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean isAvailable() {
            return available;
        }

        @Override
        public String toString() {
            return String.format("Index %d: %s - %s (%s)",
                    index, name, description, available ? "Available" : "Unavailable");
        }
    }

    public static List<Action> getAvailableActions() {
        List<Action> actions = new ArrayList<>();
        int index = 0;

        // Attack style actions (4 actions)
        actions.add(new Action(index++, "No-op attack", "Do nothing this tick", true));
        actions.add(new Action(index++, "Mage attack", "Cast magic spell", canUseMagic()));
        actions.add(new Action(index++, "Ranged attack", "Use ranged weapon", canUseRanged()));
        actions.add(new Action(index++, "Melee attack", "Use melee weapon", canUseMelee()));

        // Melee attack type actions (3 actions)
        actions.add(new Action(index++, "No melee attack", "Don't use melee", true));
        actions.add(new Action(index++, "Basic melee attack", "Basic melee attack", canUseMelee()));
        actions.add(new Action(index++, "Melee special attack", "Use melee special", canUseMeleeSpecial()));

        // Ranged attack type actions (3 actions)
        actions.add(new Action(index++, "No ranged attack", "Don't use ranged", true));
        actions.add(new Action(index++, "Basic ranged attack", "Basic ranged attack", canUseRanged()));
        actions.add(new Action(index++, "Ranged special attack", "Use ranged special", canUseRangedSpecial()));

        // Mage attack type actions (4 actions)
        actions.add(new Action(index++, "No mage attack", "Don't use magic", true));
        actions.add(new Action(index++, "Use ice spell", "Cast ice spell", canUseIceBarrage()));
        actions.add(new Action(index++, "Use blood spell", "Cast blood spell", canUseBloodBarrage()));
        actions.add(new Action(index++, "Use magic spec", "Use magic special", canUseMagicSpec()));

        // Potion actions (5 actions)
        actions.add(new Action(index++, "No potion", "Don't use potion", true));
        actions.add(new Action(index++, "Use brew", "Drink saradomin brew", hasBrew()));
        actions.add(new Action(index++, "Use restore potion", "Drink prayer restore", hasRestore()));
        actions.add(new Action(index++, "Use combat potion", "Drink combat potion", hasCombat()));
        actions.add(new Action(index++, "Use ranged potion", "Drink ranged potion", hasRanged()));

        // Food actions (2 actions)
        actions.add(new Action(index++, "Don't eat food", "Don't eat food", true));
        actions.add(new Action(index++, "Eat primary food", "Eat primary food", hasFood()));

        // Karambwan actions (2 actions)
        actions.add(new Action(index++, "Don't karambwan", "Don't eat karambwan", true));
        actions.add(new Action(index++, "Eat karambwan", "Eat karambwan", hasKarambwan()));

        // Vengeance actions (2 actions)
        actions.add(new Action(index++, "Don't use veng", "Don't use vengeance", true));
        actions.add(new Action(index++, "Use veng", "Cast Vengeance spell", canCastVengeance()));

        // Gear actions (2 actions)
        actions.add(new Action(index++, "No gear swap", "Don't swap gear", true));
        actions.add(new Action(index++, "Use tank gear", "Switch to tank gear", true));

        // Movement actions (5 actions)
        actions.add(new Action(index++, "Don't move", "Don't move", true));
        actions.add(new Action(index++, "Move next to target", "Move adjacent to target", canMove()));
        actions.add(new Action(index++, "Move under target", "Move under target", canMove()));
        actions.add(new Action(index++, "Move to farcast tile", "Move to farcast position", canMove()));
        actions.add(new Action(index++, "Move diagonal to target", "Move diagonal to target", canMove()));

        // Farcast distance actions (7 actions)
        actions.add(new Action(index++, "Don't move (farcast)", "Don't farcast", true));
        actions.add(new Action(index++, "Farcast 2 tiles", "Farcast 2 tiles away", canMove()));
        actions.add(new Action(index++, "Farcast 3 tiles", "Farcast 3 tiles away", canMove()));
        actions.add(new Action(index++, "Farcast 4 tiles", "Farcast 4 tiles away", canMove()));
        actions.add(new Action(index++, "Farcast 5 tiles", "Farcast 5 tiles away", canMove()));
        actions.add(new Action(index++, "Farcast 6 tiles", "Farcast 6 tiles away", canMove()));
        actions.add(new Action(index++, "Farcast 7 tiles", "Farcast 7 tiles away", canMove()));

        // Prayer actions (6 actions)
        actions.add(new Action(index++, "No-op prayer", "Don't change prayer", true));
        actions.add(new Action(index++, "Mage prayer", "Activate magic prayer", true));
        actions.add(new Action(index++, "Ranged prayer", "Activate ranged prayer", true));
        actions.add(new Action(index++, "Melee prayer", "Activate melee prayer", true));
        actions.add(new Action(index++, "Smite prayer", "Activate smite prayer", true));
        actions.add(new Action(index++, "Redemption prayer", "Activate redemption prayer", true));

        return actions;
    }

    public static boolean executeActionFromActionHead(int actionHeadIndex, int actionIndex) {
        try {
            switch (actionHeadIndex) {
                case 0: // attack
                    return executeAttackAction(actionIndex);
                case 1: // melee_attack_type
                    return executeMeleeAttackTypeAction(actionIndex);
                case 2: // ranged_attack_type
                    return executeRangedAttackTypeAction(actionIndex);
                case 3: // mage_attack_type
                    return executeMageAttackTypeAction(actionIndex);
                case 4: // potion
                    return executePotionAction(actionIndex);
                case 5: // food
                    return executeFoodAction(actionIndex);
                case 6: // karambwan
                    return executeKarambwanAction(actionIndex);
                case 7: // veng
                    return executeVengAction(actionIndex);
                case 8: // gear
                    return executeGearAction(actionIndex);
                case 9: // movement
                    return executeMovementAction(actionIndex);
                case 10: // farcast_distance
                    return executeFarcastDistanceAction(actionIndex);
                case 11: // prayer
                    return executePrayerAction(actionIndex);
                default:
                    logger.warn("Unknown action head index: {}", actionHeadIndex);
                    return false;
            }
        } catch (Exception e) {
            logger.error("Error executing action head {} with action {}: {}", actionHeadIndex, actionIndex,
                    e.getMessage());
            return false;
        }
    }

    private static boolean executeAttackAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // no_op_attack
            case 1:
                return executeMageAttack(); // mage_attack
            case 2:
                return executeRangedAttack(); // ranged_attack
            case 3:
                return executeMeleeAttack(); // melee_attack
            default:
                logger.warn("Unknown attack action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executeMeleeAttackTypeAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // no_melee_attack
            case 1:
                return executeMeleeAttack(); // basic_melee_attack
            case 2:
                return executeMeleeSpecial(); // melee_special_attack
            default:
                logger.warn("Unknown melee attack type action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executeRangedAttackTypeAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // no_ranged_attack
            case 1:
                return executeRangedAttack(); // basic_ranged_attack
            case 2:
                return executeRangedSpecial(); // ranged_special_attack
            default:
                logger.warn("Unknown ranged attack type action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executeMageAttackTypeAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // no_mage_attack
            case 1:
                return executeIceBarrage(); // use_ice_spell
            case 2:
                return executeBloodBarrage(); // use_blood_spell
            case 3:
                return executeMagicSpec(); // use_magic_spec
            default:
                logger.warn("Unknown mage attack type action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executePotionAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // no_potion
            case 1:
                return executeUseBrew(); // use_brew
            case 2:
                return executeUseRestore(); // use_restore_potion
            case 3:
                return executeUseCombat(); // use_combat_potion
            case 4:
                return executeUseRanged(); // use_ranged_potion
            default:
                logger.warn("Unknown potion action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executeFoodAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // dont_eat_food
            case 1:
                return executeEatFood(); // eat_primary_food
            default:
                logger.warn("Unknown food action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executeKarambwanAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // dont_karambwan
            case 1:
                return executeEatKarambwan(); // eat_karambwan
            default:
                logger.warn("Unknown karambwan action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executeVengAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // dont_use_veng
            case 1:
                return executeCastVengeance(); // use_veng
            default:
                logger.warn("Unknown veng action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executeGearAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // no_gear
            case 1:
                return executeSwapToTank(); // use_tank_gear
            case 2:
                return executeSwapToMage(); // use_tank_gear
            default:
                logger.warn("Unknown gear action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executeMovementAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // dont_move
            case 1:
                return executeMoveNextTo(); // move_next_to_target
            case 2:
                return executeMoveUnder(); // move_under_target
            case 3:
                return executeMoveToFarcast(); // move_to_farcast_tile
            case 4:
                return executeMoveDiagonal(); // move_diagonal_to_target
            default:
                logger.warn("Unknown movement action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executeFarcastDistanceAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // no_op_farcast
            case 1:
                return executeFarcast(2); // farcast_2_tiles
            case 2:
                return executeFarcast(3); // farcast_3_tiles
            case 3:
                return executeFarcast(4); // farcast_4_tiles
            case 4:
                return executeFarcast(5); // farcast_5_tiles
            case 5:
                return executeFarcast(6); // farcast_6_tiles
            case 6:
                return executeFarcast(7); // farcast_7_tiles
            default:
                logger.warn("Unknown farcast distance action index: {}", actionIndex);
                return false;
        }
    }

    private static boolean executePrayerAction(int actionIndex) {
        switch (actionIndex) {
            case 0:
                return true; // no_op_prayer
            case 1:
                return executePrayMage(); // mage_prayer
            case 2:
                return executePrayRanged(); // ranged_prayer
            case 3:
                return executePrayMelee(); // melee_prayer
            case 4:
                return executePraySmite(); // smite_prayer
            case 5:
                return executePrayRedemption(); // redemption_prayer
            default:
                logger.warn("Unknown prayer action index: {}", actionIndex);
                return false;
        }
    }

    // Action availability checks
    private static boolean canUseMagic() {
        Player local = Players.getLocal();
        return local != null && local.getTarget() != null && local.getAnimationId() == -1 && !local.isMoving();
    }

    private static boolean canUseIceBarrage() {
        return canUseMagic() && Inventory.contains("Water rune") && Inventory.contains("Death rune")
                && Inventory.contains("Blood rune");
    }

    private static boolean canUseBloodBarrage() {
        return canUseMagic();
    }

    private static boolean canUseMagicSpec() {
        return canUseMagic() && com.runemate.game.api.osrs.local.SpecialAttack.getEnergy() >= 50;
    }

    private static boolean canUseRanged() {
        Player local = Players.getLocal();
        return local != null && local.getTarget() != null && local.getAnimationId() == -1;
    }

    private static boolean canUseRangedSpecial() {
        return canUseRanged() && com.runemate.game.api.osrs.local.SpecialAttack.getEnergy() >= 50;
    }

    private static boolean canUseMelee() {
        Player local = Players.getLocal();
        if (local == null)
            return false;
        Actor target = local.getTarget();
        return target != null && local.getAnimationId() == -1 && local.distanceTo(target) <= 1;
    }

    private static boolean canUseMeleeSpecial() {
        return canUseMelee() && com.runemate.game.api.osrs.local.SpecialAttack.getEnergy() >= 50;
    }

    private static boolean hasBrew() {
        return Inventory.contains(Pattern.compile("Saradomin brew\\(\\d+\\)")) ||
                Inventory.contains("Saradomin brew");
    }

    private static boolean hasRestore() {
        return Inventory.contains(Pattern.compile("Super restore\\(\\d+\\)")) ||
                Inventory.contains("Super restore");
    }

    private static boolean hasCombat() {
        return Inventory.contains(Pattern.compile("Super combat potion\\(\\d+\\)")) ||
                Inventory.contains("Super combat potion");
    }

    private static boolean hasRanged() {
        return Inventory.contains(Pattern.compile("Ranging potion\\(\\d+\\)")) ||
                Inventory.contains("Ranging potion");
    }

    private static boolean hasFood() {
        return !Inventory.newQuery().actions("Eat").results().isEmpty();
    }

    private static boolean hasKarambwan() {
        return Inventory.contains("Cooked karambwan");
    }

    private static boolean canCastVengeance() {
        return Varps.getAt(2450).getValue() != 1;
    }

    private static boolean canMove() {
        Player local = Players.getLocal();
        return local != null && local.getTarget() != null && !local.isMoving();
    }

    // Action execution methods
    private static boolean executeMageAttack() {
        if (!canUseMagic())
            return false;
        logger.info("Executing magic attack");

        Actor target = Players.getLocal().getTarget();
        if (target == null) {
            logger.warn("No target for magic attack");
            return false;
        }

        SpriteItem weapon = Equipment.getItemIn(Equipment.Slot.WEAPON);
        if (weapon != null) {
            if (weapon.interact("Cast")) {
                // Wait for cast animation
                Execution.delayUntil(() -> Players.getLocal().getAnimationId() != -1, 1200);
                return true;
            }
        }
        return false;
    }

    private static boolean executeIceBarrage() {
        if (!canUseIceBarrage())
            return false;
        logger.info("Casting Ice Barrage");

        Actor target = Players.getLocal().getTarget();
        if (target == null) {
            logger.warn("No target for Ice Barrage");
            return false;
        }

        return castSpell("Ice Barrage");
    }

    private static boolean executeBloodBarrage() {
        if (!canUseBloodBarrage())
            return false;
        logger.info("Casting Blood Barrage");

        Actor target = Players.getLocal().getTarget();
        if (target == null) {
            logger.warn("No target for Blood Barrage");
            return false;
        }

        return castSpell("Blood Barrage");
    }

    private static boolean executeMagicSpec() {
        if (!canUseMagicSpec())
            return false;
        logger.info("Using magic special attack");
        return activateSpecialAttack();
    }

    private static boolean executeRangedAttack() {
        if (!canUseRanged())
            return false;
        logger.info("Executing ranged attack");

        Actor target = Players.getLocal().getTarget();
        if (target == null) {
            logger.warn("No target for ranged attack");
            return false;
        }

        if (target.interact("Attack")) {
            Execution.delay(100);
            return true;
        }
        return false;
    }

    private static boolean executeRangedSpecial() {
        if (!canUseRangedSpecial())
            return false;
        logger.info("Using ranged special attack");
        if (activateSpecialAttack()) {
            return executeRangedAttack();
        }
        return false;
    }

    private static boolean executeMeleeAttack() {
        if (!canUseMelee())
            return false;
        logger.info("Executing melee attack");

        Actor target = Players.getLocal().getTarget();
        if (target == null) {
            logger.warn("No target for melee attack");
            return false;
        }

        if (target.interact("Attack")) {
            Execution.delay(100);
            return true;
        }
        return false;
    }

    private static boolean executeMeleeSpecial() {
        if (!canUseMeleeSpecial())
            return false;
        logger.info("Using melee special attack");
        if (activateSpecialAttack()) {
            return executeMeleeAttack();
        }
        return false;
    }

    private static boolean executeUseBrew() {
        logger.info("Using brew potion");
        if (Inventory.contains("Saradomin brew")) {
            SpriteItem brew = Inventory.getItems("Saradomin brew").first();
            if (brew != null && brew.interact("Drink")) {
                Execution.delay(300);
                return true;
            }
        }
        return false;
    }

    private static boolean executeUseRestore() {
        if (Inventory.contains("Super restore")) {
            SpriteItem restore = Inventory.getItems("Super restore").first();
            if (restore != null && restore.interact("Drink")) {
                Execution.delay(300);
                return true;
            }
        }
        return false;
    }

    private static boolean executeUseCombat() {
        logger.info("Using combat potion");
        if (Inventory.contains("Super combat potion")) {
            SpriteItem combat = Inventory.getItems("Super combat potion").first();
            if (combat != null && combat.interact("Drink")) {
                Execution.delay(300);
                return true;
            }
        }
        return false;
    }

    private static boolean executeUseRanged() {
        logger.info("Using ranged potion");
        if (Inventory.contains("Ranging potion")) {
            SpriteItem ranged = Inventory.getItems("Ranging potion").first();
            if (ranged != null && ranged.interact("Drink")) {
                Execution.delay(300);
                return true;
            }
        }
        return false;
    }

    private static boolean executeEatFood() {
        logger.info("Eating food");

        SpriteItem food = Inventory.newQuery().actions("Eat").results().first();
        if (food != null) {
            if (food.interact("Eat")) {
                Execution.delay(300);
                return true;
            }
        }
        return false;
    }

    private static boolean executeEatKarambwan() {
        logger.info("Eating karambwan");
        if (Inventory.contains("Cooked karambwan")) {
            SpriteItem karambwan = Inventory.getItems("Cooked karambwan").first();
            if (karambwan != null && karambwan.interact("Eat")) {
                Execution.delay(300);
                return true;
            }
        }
        return false;
    }

    private static boolean executeCastVengeance() {
        if (!canCastVengeance())
            return false;
        logger.info("Using Vengeance");
        return castSpell("Vengeance");
    }

    private static boolean executeSwapToTank() {
        logger.info("Swapping to tank gear");
        return GearSwapper.executeSwapToMelee();
    }

    private static boolean executeSwapToMage() {
        logger.info("Swapping to mage gear");
        return GearSwapper.executeSwapToMage();
    }

    private static boolean executeMoveNextTo() {
        if (!canMove())
            return false;
        logger.info("Moving next to target");
        return moveToTarget(1);
    }

    private static boolean executeMoveUnder() {
        if (!canMove())
            return false;
        logger.info("Moving under target");
        return moveToTarget(0);
    }

    private static boolean executeMoveToFarcast() {
        var me = Players.getLocal();
        var target = me.getTarget();
        if (me == null || target == null)
            return false;

        Coordinate targetPos = target.getPosition();
        Coordinate farcastTile = targetPos.derive(8, 0);
        logger.info("Moving to farcast position: " + farcastTile);

        com.runemate.game.api.hybrid.input.direct.DirectInput.sendMovement(farcastTile);
        return true;
    }

    private static boolean executeMoveDiagonal() {
        var me = Players.getLocal();
        var target = me.getTarget();
        if (me == null || target == null)
            return false;
        Coordinate targetPos = target.getPosition();
        Coordinate diagonal = targetPos.derive(1, 1);
        logger.info("Moving diagonal to: " + diagonal);

        com.runemate.game.api.hybrid.input.direct.DirectInput.sendMovement(diagonal);
        return true;
    }

    private static boolean executeFarcast(int tiles) {
        logger.info("Farcasting {} tiles away", tiles);

        Player me = Players.getLocal();
        Player opponent = Players.newQuery().targeting(me).results().nearest();
        if (me == null || opponent == null)
            return false;

        Coordinate myPos = me.getPosition();
        Coordinate oppPos = opponent.getPosition();

        int dx = myPos.getX() - oppPos.getX();
        int dy = myPos.getY() - oppPos.getY();

        int stepX = dx > 0 ? tiles : -tiles;
        int stepY = dy > 0 ? tiles : -tiles;

        Coordinate farcastTile = new Coordinate(myPos.getX() + stepX, myPos.getY() + stepY, myPos.getPlane());
        com.runemate.game.api.hybrid.input.direct.DirectInput.sendMovement(farcastTile);
        return true;
    }

    private static boolean executePrayMage() {
        logger.info("Activating Protect from Magic");
        if (Prayer.PROTECT_FROM_MAGIC.activate()) {
            Execution.delay(100);
            return true;
        }
        return false;
    }

    private static boolean executePrayRanged() {
        logger.info("Activating Protect from Missiles");
        if (Prayer.PROTECT_FROM_MISSILES.activate()) {
            Execution.delay(100);
            return true;
        }
        return false;
    }

    private static boolean executePrayMelee() {
        logger.info("Activating Protect from Melee");
        if (Prayer.PROTECT_FROM_MELEE.activate()) {
            Execution.delay(100);
            return true;
        }
        return false;
    }

    private static boolean executePraySmite() {
        logger.info("Activating Smite");
        if (Prayer.SMITE.activate()) {
            Execution.delay(100);
            return true;
        }
        return false;
    }

    private static boolean executePrayRedemption() {
        logger.info("Activating Redemption");
        if (Prayer.REDEMPTION.activate()) {
            Execution.delay(100);
            return true;
        }
        return false;
    }

    private static boolean castSpell(String spellName) {
        Actor target = Players.getLocal().getTarget();
        if (target == null) {
            logger.warn("No target for spell: {}", spellName);
            return false;
        }

        SpriteItem weapon = Equipment.getItemIn(Equipment.Slot.WEAPON);
        if (weapon == null) {
            logger.warn("No weapon equipped for spell: {}", spellName);
            return false;
        }

        // First cast the spell
        if (weapon.interact("Cast")) {
            // Wait for cast animation
            Execution.delayUntil(() -> Players.getLocal().getAnimationId() != -1, 1200);

            // Now click on the target
            if (target.interact("Cast")) {
                Execution.delay(300);
                return true;
            }
        }
        return false;
    }

    private static boolean activateSpecialAttack() {
        if (com.runemate.game.api.osrs.local.SpecialAttack.isActivated())
            return true;
        if (com.runemate.game.api.osrs.local.SpecialAttack.activate(true)) {
            Execution.delayUntil(com.runemate.game.api.osrs.local.SpecialAttack::isActivated, 600);
            return true;
        }
        return false;
    }

    private static boolean moveToTarget(int distance) {
        Actor target = Players.getLocal().getTarget();
        if (target == null)
            return false;
        if (distance == 0) {
            return target.getPosition().interact("Walk here");
        } else {
            return target.getPosition().derive(target.getPosition().getX() + 1, target.getPosition().getY())
                    .interact("Walk here");
        }
    }
}
