// import com.runemate.game.api.hybrid.entities.Player;
// import com.runemate.game.api.hybrid.local.Skill;
// import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
// import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
// import com.runemate.game.api.hybrid.region.Players;
// import com.runemate.game.api.script.Execution;
// import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
// import com.runemate.game.api.hybrid.local.Varps;
// import com.runemate.game.api.hybrid.local.hud.interfaces.Prayer;
// import java.util.*;

// public class CombatStatsCalculator {
// // Bonus indices from RuneMate's Bonus class
// private static final int ATTACK_STAB = 0;
// private static final int ATTACK_SLASH = 1;
// private static final int ATTACK_CRUSH = 2;
// private static final int ATTACK_MAGIC = 3;
// private static final int ATTACK_RANGED = 4;
// private static final int DEFENCE_STAB = 5;
// private static final int DEFENCE_SLASH = 6;
// private static final int DEFENCE_CRUSH = 7;
// private static final int DEFENCE_MAGIC = 8;
// private static final int DEFENCE_RANGED = 9;
// private static final int MELEE_STRENGTH = 10;
// private static final int RANGED_STRENGTH = 11;
// private static final int MAGIC_STRENGTH = 12;
// private static final int PRAYER_BONUS = 13;

// // Attack speeds (in ticks) for common weapon types
// private static final Map<String, Integer> WEAPON_SPEEDS = new HashMap<String,
// Integer>() {
// {
// put("staff", 6);
// put("wand", 5);
// put("sceptre", 5); // Magic
// put("bow", 5);
// put("crossbow", 6);
// put("blowpipe", 3); // Ranged
// put("whip", 4);
// put("scimitar", 4);
// put("godsword", 6); // Melee
// }
// };

// // Magic damage multipliers from gear
// private static final Map<String, Double> MAGIC_DAMAGE_BOOSTS = new
// HashMap<String, Double>() {
// {
// put("occult necklace", 0.10);
// put("tormented bracelet", 0.05);
// put("elder maul", 0.05);
// put("smoke battlestaff", 0.10);
// put("harmonised nightmare staff", 0.15);
// }
// };

// // Player stat calculations
// private static double getEffectiveLevel(Skill skill) {
// double base = skill.getCurrentLevel();
// // Apply potion boost (simplified)
// double boost = Math.min(base * 0.15 + 5, 15); // ~15% boost
// return base + boost;
// }

// private static int getEquipmentBonus(int bonusType) {
// int total = 0;
// for (SpriteItem item : Equipment.getItems()) {
// ItemDefinition def = item.getDefinition();
// if (def != null) {
// int[] bonuses = def.getBonuses();
// if (bonuses != null && bonuses.length > bonusType) {
// total += bonuses[bonusType];
// }
// }
// }
// return total;
// }

// private static double getStyleMultiplier() {
// // Check attack style (stab/slash/crush)
// // Simplified: assume controlled style (+1 to all)
// return 1.0;
// }

// private static double getPrayerMultiplier(int skillType) {
// // 0=melee, 1=ranged, 2=magic
// if (Prayer.Modern.PIETY.isActive() && skillType == 0)
// return 1.23;
// if (Prayer.Modern.RIGOUR.isActive() && skillType == 1)
// return 1.23;
// if (Prayer.Modern.AUGURY.isActive() && skillType == 2)
// return 1.25;
// return 1.0;
// }

// // Accuracy calculations
// private static double getMeleeAccuracy() {
// double effectiveAttack = getEffectiveLevel(Skill.ATTACK) *
// getPrayerMultiplier(0);
// double equipmentBonus = getEquipmentBonus(ATTACK_SLASH); // Use best attack
// type
// return effectiveAttack * (equipmentBonus + 64) * getStyleMultiplier();
// }

// private static double getRangedAccuracy() {
// double effectiveRanged = getEffectiveLevel(Skill.RANGED) *
// getPrayerMultiplier(1);
// double equipmentBonus = getEquipmentBonus(ATTACK_RANGED);
// return effectiveRanged * (equipmentBonus + 64);
// }

// private static double getMagicAccuracy() {
// double effectiveMagic = getEffectiveLevel(Skill.MAGIC) *
// getPrayerMultiplier(2);
// double equipmentBonus = getEquipmentBonus(ATTACK_MAGIC);
// return effectiveMagic * (equipmentBonus + 64);
// }

// // Strength/damage calculations
// private static double getMeleeStrength() {
// double effectiveStrength = getEffectiveLevel(Skill.STRENGTH) *
// getPrayerMultiplier(0);
// double equipmentBonus = getEquipmentBonus(MELEE_STRENGTH);
// return (effectiveStrength + 8) * (equipmentBonus + 64);
// }

// private static double getRangedStrength() {
// double equipmentBonus = getEquipmentBonus(RANGED_STRENGTH);
// return equipmentBonus; // Ranged strength bonus is direct damage adder
// }

// private static double getMagicStrength() {
// double baseDamage = 20; // Base max hit for most spells
// double multiplier = 1.0;

// // Sum magic damage bonuses
// for (SpriteItem item : Equipment.getItems()) {
// String name = item.getDefinition().getName().toLowerCase();
// if (MAGIC_DAMAGE_BOOSTS.containsKey(name)) {
// multiplier += MAGIC_DAMAGE_BOOSTS.get(name);
// }
// }

// return baseDamage * multiplier;
// }

// // Attack speed calculations
// private static double getMeleeAttackSpeed() {
// SpriteItem weapon = Equipment.getItemIn(Equipment.Slot.WEAPON);
// if (weapon == null)
// return 4.0; // Fists

// String name = weapon.getDefinition().getName().toLowerCase();
// for (String type : WEAPON_SPEEDS.keySet()) {
// if (name.contains(type)) {
// return WEAPON_SPEEDS.get(type);
// }
// }
// return 4.0; // Default speed
// }

// private static double getRangedAttackSpeed() {
// SpriteItem weapon = Equipment.getItemIn(Equipment.Slot.WEAPON);
// if (weapon == null)
// return 5.0;

// String name = weapon.getDefinition().getName().toLowerCase();
// if (name.contains("blowpipe"))
// return 2.0;
// if (name.contains("knife") || name.contains("dart"))
// return 3.0;
// if (name.contains("crossbow"))
// return 6.0;
// return 5.0; // Bows
// }

// private static double getRangedAttackRange() {
// SpriteItem weapon = Equipment.getItemIn(Equipment.Slot.WEAPON);
// if (weapon == null)
// return 1.0;

// String name = weapon.getDefinition().getName().toLowerCase();
// if (name.contains("shortbow"))
// return 7.0;
// if (name.contains("longbow"))
// return 10.0;
// if (name.contains("crossbow"))
// return 7.0;
// return 1.0; // Default
// }

// // Defense calculations
// private static double getMeleeGearDefence(int defenceType) {
// return getEquipmentBonus(defenceType);
// }

// private static double getMagicGearDefence(int defenceType) {
// double base = getEquipmentBonus(defenceType);
// // Mage gear typically has negative melee def, so clamp
// return Math.max(base, 0);
// }

// private static double getRangedGearDefence(int defenceType) {
// return getEquipmentBonus(defenceType);
// }

// // Target estimation (using visible gear analysis)
// private static double estimateTargetStat(Player target, String statType) {
// if (target == null)
// return 100.0;

// // Simplified estimation based on visible weapon
// SpriteItem weapon = target.getEquipment().get(Equipment.Slot.WEAPON);
// if (weapon == null)
// return 100.0;

// String name = weapon.getDefinition().getName().toLowerCase();

// switch (statType) {
// case "magic":
// return name.contains("staff") || name.contains("wand") ? 120.0 : 80.0;
// case "ranged":
// return name.contains("bow") || name.contains("crossbow") ? 120.0 : 80.0;
// case "melee":
// return !name.contains("staff") && !name.contains("bow") ? 120.0 : 80.0;
// default:
// return 100.0;
// }
// }

// // Implementations for all required methods
// public static double getMagicAccuracy() {
// return getMagicAccuracy() / 1000.0; // Normalize
// }

// public static double getMagicStrength() {
// return getMagicStrength() / 30.0; // Max ~30 damage
// }

// public static double getRangedAccuracy() {
// return getRangedAccuracy() / 1000.0;
// }

// public static double getRangedStrength() {
// return getRangedStrength() / 50.0; // Max +50 ranged str
// }

// public static double getRangedAttackSpeed() {
// return getRangedAttackSpeed() / 10.0; // Normalize
// }

// public static double getRangedAttackRange() {
// return getRangedAttackRange() / 10.0;
// }

// public static double getMeleeAttackSpeed() {
// return getMeleeAttackSpeed() / 10.0;
// }

// public static double getMeleeAccuracy() {
// return getMeleeAccuracy() / 1000.0;
// }

// public static double getMeleeStrength() {
// return getMeleeStrength() / 1000.0;
// }

// public static double getMagicGearRangedDefence() {
// return getMagicGearDefence(DEFENCE_RANGED) / 100.0;
// }

// public static double getMagicGearMageDefence() {
// return getMagicGearDefence(DEFENCE_MAGIC) / 100.0;
// }

// public static double getMagicGearMeleeDefence() {
// double stab = getMagicGearDefence(DEFENCE_STAB);
// double slash = getMagicGearDefence(DEFENCE_SLASH);
// double crush = getMagicGearDefence(DEFENCE_CRUSH);
// return Math.max(Math.max(stab, slash), crush) / 100.0;
// }

// public static double getRangedGearRangedDefence() {
// return getRangedGearDefence(DEFENCE_RANGED) / 100.0;
// }

// public static double getRangedGearMageDefence() {
// return getRangedGearDefence(DEFENCE_MAGIC) / 100.0;
// }

// public static double getRangedGearMeleeDefence() {
// double stab = getRangedGearDefence(DEFENCE_STAB);
// double slash = getRangedGearDefence(DEFENCE_SLASH);
// double crush = getRangedGearDefence(DEFENCE_CRUSH);
// return Math.max(Math.max(stab, slash), crush) / 100.0;
// }

// public static double getMeleeGearRangedDefence() {
// return getMeleeGearDefence(DEFENCE_RANGED) / 100.0;
// }

// public static double getMeleeGearMageDefence() {
// return getMeleeGearDefence(DEFENCE_MAGIC) / 100.0;
// }

// public static double getMeleeGearMeleeDefence() {
// double stab = getMeleeGearDefence(DEFENCE_STAB);
// double slash = getMeleeGearDefence(DEFENCE_SLASH);
// double crush = getMeleeGearDefence(DEFENCE_CRUSH);
// return Math.max(Math.max(stab, slash), crush) / 100.0;
// }

// public static double getTargetCurrentGearRangedDefence(Player target) {
// return estimateTargetStat(target, "ranged") / 100.0;
// }

// public static double getTargetCurrentGearMageDefence(Player target) {
// return estimateTargetStat(target, "magic") / 100.0;
// }

// public static double getTargetCurrentGearMeleeDefence(Player target) {
// return estimateTargetStat(target, "melee") / 100.0;
// }

// public static double getTargetMagicAccuracy() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "magic") / 100.0;
// }

// public static double getTargetMagicStrength() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "magic") / 100.0;
// }

// public static double getTargetRangedAccuracy() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "ranged") / 100.0;
// }

// public static double getTargetRangedStrength() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "ranged") / 100.0;
// }

// public static double getTargetMeleeAccuracy() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "melee") / 100.0;
// }

// public static double getTargetMeleeStrength() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "melee") / 100.0;
// }

// public static double getTargetMagicGearRangedDefence() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "ranged") / 100.0;
// }

// public static double getTargetMagicGearMageDefence() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "magic") / 100.0;
// }

// public static double getTargetMagicGearMeleeDefence() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "melee") / 100.0;
// }

// public static double getTargetRangedGearRangedDefence() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "ranged") / 100.0;
// }

// public static double getTargetRangedGearMageDefence() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "magic") / 100.0;
// }

// public static double getTargetRangedGearMeleeDefence() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "melee") / 100.0;
// }

// public static double getTargetMeleeGearRangedDefence() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "ranged") / 100.0;
// }

// public static double getTargetMeleeGearMageDefence() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "magic") / 100.0;
// }

// public static double getTargetMeleeGearMeleeDefence() {
// Player target = Players.getLocal().getTarget();
// return estimateTargetStat(target, "melee") / 100.0;
// }
// }