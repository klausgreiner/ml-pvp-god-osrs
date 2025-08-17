package com.runemate.party.pvpgodmode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class PvpRlModel {

    private static final Logger logger = LogManager.getLogger(PvpRlModel.class);

    private final String apiHost;
    private final int apiPort;
    private final String modelName;
    private static final int FRAME_STACK_SIZE = 1;

    // Persistent connection components
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isConnected = false;

    public PvpRlModel(String modelName) {
        this.apiHost = "127.0.0.1";
        this.apiPort = 9999;
        this.modelName = modelName;
        logger.info("PvP RL Model client initialized for model: {}", modelName);
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            logger.info("Establishing persistent connection to ML API at {}:{}", apiHost, apiPort);
            socket = new Socket(apiHost, apiPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            isConnected = true;
            logger.info("Successfully established persistent connection to ML API");
        } catch (IOException e) {
            logger.error("Failed to establish connection to ML API at {}:{} - {}", apiHost, apiPort, e.getMessage());
            isConnected = false;
            throw new RuntimeException("Failed to connect to ML API", e);
        }
    }

    private void ensureConnection() {
        if (!isConnected || socket == null || socket.isClosed()) {
            logger.info("Connection lost, attempting to reconnect...");
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.warn("Error closing old connection: {}", e.getMessage());
            }
            initializeConnection();
        }
    }

    public void closeConnection() {
        try {
            isConnected = false;
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            logger.info("Closed persistent connection to ML API");
        } catch (IOException e) {
            logger.warn("Error closing connection: {}", e.getMessage());
        }
    }

    public static class ModelOutput {
        private final int[] actionIndices;
        private final double confidence;
        private final double[] actionProbabilities;
        private final double[] outcomeProbabilities;

        public ModelOutput(int[] actionIndices, double confidence, double[] actionProbabilities,
                double[] outcomeProbabilities) {
            this.actionIndices = actionIndices;
            this.confidence = confidence;
            this.actionProbabilities = actionProbabilities;
            this.outcomeProbabilities = outcomeProbabilities;
        }

        public int[] getActionIndices() {
            return actionIndices;
        }

        public int getActionIndex() {
            return actionIndices[0];
        }

        public double getConfidence() {
            return confidence;
        }

        public double[] getActionProbabilities() {
            return actionProbabilities;
        }

        public double[] getOutcomeProbabilities() {
            return outcomeProbabilities;
        }
    }

    public ModelOutput getAction(List<PvpObservations.Observation> observations) {
        try {
            double[][] observationArray = convertObservationsToFrameStackedArray(observations);
            boolean[][] actionMasks = createActionMasks();
            return queryApi(observationArray, actionMasks);
        } catch (Exception e) {
            logger.error("Error communicating with PvP ML API: {}", e.getMessage());
            // Try to reconnect on error
            try {
                ensureConnection();
            } catch (Exception reconnectError) {
                logger.error("Failed to reconnect: {}", reconnectError.getMessage());
            }
            throw new RuntimeException("Failed to get action from API", e);
        }
    }

    public double[][] convertObservationsToFrameStackedArray(List<PvpObservations.Observation> observations) {
        double[][] frameStackedArray = new double[FRAME_STACK_SIZE][observations.size()];

        for (int frame = 0; frame < FRAME_STACK_SIZE; frame++) {
            for (int i = 0; i < observations.size(); i++) {
                frameStackedArray[frame][i] = observations.get(i).getValue();
            }
        }

        return frameStackedArray;
    }

    public boolean[][] createActionMasks() {
        return new boolean[][] {
                { true, true, true, true }, // attack
                { true, true, true }, // melee_attack_type
                { true, true, true }, // ranged_attack_type
                { true, true, true, true }, // mage_attack_type
                { true, true, true, true, true }, // potion
                { true, true }, // food
                { true, true }, // karambwan
                { true, true }, // veng
                { true, true }, // gear
                { true, true, true, true, true }, // movement
                { true, true, true, true, true, true, true }, // farcast_distance
                { true, true, true, true, true, true } // prayer
        };
    }

    private ModelOutput queryApi(double[][] observations, boolean[][] actionMasks) throws IOException {
        ensureConnection();

        String request = createApiRequest(observations, actionMasks);
        logger.debug("Sending API request: {}", request);

        out.println(request);
        out.flush();

        String response = in.readLine();
        if (response == null) {
            throw new IOException("No response from API");
        }

        logger.debug("Received API response: {}", response);
        return parseApiResponse(response);
    }

    public String createApiRequest(double[][] observations, boolean[][] actionMasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"model\":\"").append(modelName).append("\",");
        sb.append("\"actionMasks\":[");

        for (int i = 0; i < actionMasks.length; i++) {
            if (i > 0)
                sb.append(",");
            sb.append("[");
            for (int j = 0; j < actionMasks[i].length; j++) {
                if (j > 0)
                    sb.append(",");
                sb.append(actionMasks[i][j]);
            }
            sb.append("]");
        }
        sb.append("],");

        sb.append("\"obs\":[");
        for (int frame = 0; frame < observations.length; frame++) {
            if (frame > 0)
                sb.append(",");
            sb.append("[");
            for (int i = 0; i < observations[frame].length; i++) {
                if (i > 0)
                    sb.append(",");
                sb.append(observations[frame][i]);
            }
            sb.append("]");
        }
        sb.append("],");

        sb.append("\"deterministic\":false,");
        sb.append("\"returnLogProb\":false,");
        sb.append("\"returnEntropy\":false,");
        sb.append("\"returnValue\":false,");
        sb.append("\"returnProbs\":false,");
        sb.append("\"extensions\":[]");
        sb.append("}");

        return sb.toString();
    }

    private ModelOutput parseApiResponse(String response) {
        try {
            if (response == null || response.trim().isEmpty()) {
                throw new RuntimeException("Empty response from API");
            }

            if (!response.contains("\"action\":")) {
                throw new RuntimeException("Response does not contain action field: " + response);
            }

            // Basic JSON validation - check if response starts and ends with braces
            String trimmedResponse = response.trim();
            if (!trimmedResponse.startsWith("{") || !trimmedResponse.endsWith("}")) {
                throw new RuntimeException("Response is not valid JSON: " + response);
            }

            int actionStart = response.indexOf("\"action\":") + 9;
            // Skip any whitespace after the colon
            while (actionStart < response.length() && Character.isWhitespace(response.charAt(actionStart))) {
                actionStart++;
            }
            // Skip the opening bracket
            if (actionStart < response.length() && response.charAt(actionStart) == '[') {
                actionStart++;
            } else {
                throw new RuntimeException("Could not find opening bracket after action field: " + response);
            }

            // Find the closing bracket for the action array by counting opening/closing
            // brackets
            int bracketCount = 1; // Start with 1 since we're already after the opening bracket
            int actionEnd = actionStart;
            for (int i = actionStart; i < response.length(); i++) {
                char c = response.charAt(i);
                if (c == '[') {
                    bracketCount++;
                } else if (c == ']') {
                    bracketCount--;
                    if (bracketCount == 0) {
                        actionEnd = i;
                        break;
                    }
                }
            }

            if (actionEnd <= actionStart) {
                throw new RuntimeException("Could not parse action array from response: " + response);
            }

            String actionStr = response.substring(actionStart, actionEnd);
            logger.debug("Parsed action string: {}", actionStr);
            logger.debug("Action array bounds: start={}, end={}", actionStart, actionEnd);

            String[] actionParts = actionStr.split(",");
            int[] actionIndices = new int[actionParts.length];

            for (int i = 0; i < actionParts.length; i++) {
                try {
                    actionIndices[i] = Integer.parseInt(actionParts[i].trim());
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Failed to parse action index '" + actionParts[i] + "' at position " + i,
                            e);
                }
            }

            logger.info("Successfully parsed {} action indices: {}", actionIndices.length,
                    java.util.Arrays.toString(actionIndices));

            double[] actionProbabilities = new double[45];
            double confidence = 1.0 / actionIndices.length;

            double[] outcomeProbabilities = { 0.7, 0.3 };

            return new ModelOutput(actionIndices, confidence, actionProbabilities, outcomeProbabilities);

        } catch (Exception e) {
            logger.error("Failed to parse API response: {}", e.getMessage());
            logger.error("Raw response was: {}", response);
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

    public boolean isModelLoaded() {
        return isConnected && socket != null && !socket.isClosed();
    }
}
