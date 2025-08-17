package com.runemate.party.pvpgodmode;

import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.runemate.game.api.hybrid.entities.Actor;
import com.runemate.game.api.hybrid.entities.Player;

import java.util.List;

public class PvpCombatNode extends LeafTask {

    private static final Logger logger = LogManager.getLogger(PvpCombatNode.class);
    private final PvpGodModeSettings settings;

    private PvpRlModel rlModel;
    private long lastActionTime = 0;
    private static final long ACTION_COOLDOWN = 600; // Increased from 300ms to 600ms for better timing
    private Actor currentTarget;

    public PvpCombatNode(PvpGodModeSettings settings) {
        this.settings = settings;

    }

    @Override
    public void execute() {
        var player = Players.getLocal();
        if (player == null) {
            logger.warn("Player not found");
            return;
        }

        validateAndAcquireTarget(player);

        if (currentTarget == null) {
            logger.info("No target found, skipping combat logic");
            Execution.delay(300);
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < ACTION_COOLDOWN) {
            Execution.delay(100);
            return;
        }

        try {
            List<PvpObservations.Observation> observations = PvpObservations.getCurrentObservations(currentTarget);

            if (observations.isEmpty()) {
                logger.warn("No observations available");
                Execution.delay(100);
                return;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Current observations:");
                for (PvpObservations.Observation obs : observations) {
                    logger.debug("  {}", obs);
                }
            }

            PvpRlModel.ModelOutput modelOutput;
            if (settings.getRlModelEnabled()) {
                if (rlModel == null) {
                    logger.info("Initializing RL model for first use with model: {}", settings.getModelPath());
                    rlModel = new PvpRlModel(settings.getModelPath());
                }

                modelOutput = rlModel.getAction(observations);

                logger.info("RL Model decision: Actions {}, Confidence: {}%, Win Probability: {}%",
                        java.util.Arrays.toString(modelOutput.getActionIndices()),
                        String.format("%.1f", modelOutput.getConfidence() * 100),
                        String.format("%.1f", modelOutput.getOutcomeProbabilities()[0] * 100));
            } else {
                logger.warn("RL Model is disabled in settings");
                Execution.delay(100);
                return;
            }

            boolean actionSuccess = executeModelActions(modelOutput.getActionIndices());

            if (actionSuccess) {
                lastActionTime = currentTime;
                logger.info("Successfully executed actions: {}",
                        java.util.Arrays.toString(modelOutput.getActionIndices()));
            } else {
                logger.warn("Failed to execute actions: {}", java.util.Arrays.toString(modelOutput.getActionIndices()));
            }

            Execution.delay(100);

        } catch (Exception e) {
            logger.error("Error in PVP combat node: {}", e.getMessage(), e);
            Execution.delay(200);
        }
    }

    private boolean executeModelActions(int[] actionIndices) {
        if (actionIndices == null || actionIndices.length == 0) {
            logger.warn("No actions to execute");
            return false;
        }

        boolean allSuccess = true;
        try {
            for (int i = 0; i < actionIndices.length; i++) {
                int actionIndex = actionIndices[i];
                boolean success = PvpActions.executeActionFromActionHead(i, actionIndex);

                if (!success) {
                    logger.warn("Failed to execute action {} at position {} (action head {})", actionIndex, i, i);
                    allSuccess = false;
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to send prediction to UI: {}", e.getMessage());
        }
        return allSuccess;
    }

    public PvpRlModel getRlModel() {
        return rlModel;
    }

    private void validateAndAcquireTarget(Player player) {
        // Only change target if current target is completely invalid or dead
        if (currentTarget != null) {
            if (!currentTarget.isValid()) {
                logger.info("Target {} is no longer valid.", currentTarget.getName());
                currentTarget = null;
            } else if (currentTarget.getHealthGauge() != null && currentTarget.getHealthGauge().getPercent() == 0) {
                logger.info("Target {} has died.", currentTarget.getName());
                currentTarget = null;
            } else if (currentTarget.distanceTo(player) > 20) {
                logger.info("Target {} is too far away ({} tiles).", currentTarget.getName(),
                        currentTarget.distanceTo(player));
                currentTarget = null;
            } else {
                // Ensure we're still attacking the target
                ensureAttackingTarget();
            }
        }

        // Only acquire new target if we don't have one
        if (currentTarget == null) {
            // Look for players who are attacking us or nearby
            Player newTarget = Players.newQuery()
                    .filter(p -> !p.equals(player) && p.isValid())
                    .filter(p -> p.getTarget() != null && p.getTarget().equals(player)) // Players attacking us
                    .results()
                    .nearest();

            if (newTarget == null) {
                // If no one is attacking us, look for nearby players
                newTarget = Players.newQuery()
                        .filter(p -> !p.equals(player) && p.isValid())
                        .filter(p -> p.distanceTo(player) <= 10) // Within 10 tiles
                        .results()
                        .nearest();
            }

            if (newTarget != null) {
                currentTarget = newTarget;
                logger.info("New target acquired: {}", currentTarget.getName());

                // Attack the target immediately
                if (currentTarget.interact("Attack")) {
                    logger.info("Attacking target: {}", currentTarget.getName());
                }
            }
        }
    }

    private void ensureAttackingTarget() {
        if (currentTarget == null || Players.getLocal() == null) {
            return;
        }

        // Check if we're currently attacking the target
        if (Players.getLocal().getTarget() == null || !Players.getLocal().getTarget().equals(currentTarget)) {
            logger.debug("Re-engaging target: {}", currentTarget.getName());
            if (currentTarget.interact("Attack")) {
                logger.debug("Re-engaged target: {}", currentTarget.getName());
            }
        }
    }
}
