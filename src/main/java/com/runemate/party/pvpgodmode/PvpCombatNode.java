package com.runemate.party.pvpgodmode;

import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PvpCombatNode extends LeafTask {

    private static final Logger logger = LogManager.getLogger(PvpCombatNode.class);
    private final PvpGodModeSettings settings;
    private PvpRlModel rlModel;
    private long lastActionTime = 0;
    private static final long ACTION_COOLDOWN = 600;

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

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < ACTION_COOLDOWN) {
            Execution.delay(50);
            return;
        }

        try {
            List<PvpObservations.Observation> observations = PvpObservations.getCurrentObservations();

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
                logger.info("RL Model decision: Actions {}, Confidence: {:.1f}%, Win Probability: {:.1f}%",
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

            Execution.delay(50);

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
        for (int i = 0; i < actionIndices.length; i++) {
            int actionIndex = actionIndices[i];
            boolean success = PvpActions.executeActionFromActionHead(i, actionIndex);
            if (!success) {
                logger.warn("Failed to execute action {} at position {} (action head {})", actionIndex, i, i);
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    public PvpRlModel getRlModel() {
        return rlModel;
    }
}
