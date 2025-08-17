# PVP God Mode - Bot Predictions UI

This document explains how to use the bot predictions UI system in PVP God Mode.

## Overview

The PVP God Mode bot now includes a custom UI system that displays real-time predictions from the RL model during combat. The UI shows:

- Bot status (Active/Inactive)
- RL Model status (Enabled/Disabled)
- Prediction count
- Recent predictions with timestamps
- Control buttons for clearing predictions and refreshing status

## How to Use

### 1. Starting the Bot

When you start the PVP God Mode bot, it will initialize the UI system automatically. You'll see a log message:

```
PVP God Mode started successfully - waiting for settings confirmation
Use showPredictionsUI() method to display bot predictions interface
```

### 2. Showing the UI

To display the bot predictions interface, call the `showPredictionsUI()` method on your bot instance:

```java
// If you have access to the bot instance
bot.showPredictionsUI();

// Or if you're running the bot, you can call this method
// through the bot's console or by adding it to your script
```

### 3. UI Features

The UI provides several features:

- **Real-time Updates**: The UI automatically updates as the bot makes predictions
- **Status Display**: Shows current bot and RL model status
- **Prediction History**: Displays recent predictions with timestamps
- **Clear Button**: Clears all stored predictions
- **Refresh Button**: Refreshes the current status display

### 4. Prediction Format

Each prediction includes:
- Timestamp
- Target name
- Recommended actions
- Confidence level
- Win probability

Example prediction:
```
[14:32:15] Target: Player123 | Actions: [1, 3, 0] | Confidence: 85.2% | Win Probability: 72.1%
```

### 5. Hiding the UI

To hide the UI, call:

```java
bot.hidePredictionsUI();
```

The UI will also automatically hide when the bot stops.

## Technical Details

### Files

- `PvpGodModeUI.fxml` - UI layout definition
- `PvpGodModeController.java` - UI controller with event handling
- `PvpGodModeUI.java` - UI launcher and management
- `PvpGodMode.java` - Main bot class with UI integration

### Integration Points

The UI system integrates with:

1. **Bot Status**: Automatically updates when settings are confirmed
2. **RL Model**: Shows when the model is enabled/disabled
3. **Combat Node**: Receives predictions during combat execution
4. **Settings**: Reflects current configuration

### Customization

You can customize the UI by:

1. Modifying the FXML file for layout changes
2. Updating the controller for new functionality
3. Adding new prediction types in the combat node
4. Extending the UI with additional controls

## Troubleshooting

### UI Not Showing

- Ensure the bot has started successfully
- Check that the FXML resource is properly included in the manifest
- Verify JavaFX is available in your RuneMate environment

### Predictions Not Updating

- Confirm the RL model is enabled in settings
- Check that combat is active and targets are being acquired
- Verify the UI controller is properly connected

### Performance Issues

- The UI updates are throttled to prevent excessive updates
- Prediction history is limited to 50 entries
- UI updates run on the JavaFX application thread

## Example Usage in Combat

The bot automatically sends predictions to the UI during combat:

```java
// In PvpCombatNode.execute()
if (settings.getRlModelEnabled()) {
    PvpRlModel.ModelOutput modelOutput = rlModel.getAction(observations);
    
    // This automatically sends the prediction to the UI
    sendPredictionToUI(modelOutput, observations);
}
```

## Future Enhancements

Potential improvements for the UI system:

1. **Charts and Graphs**: Visual representation of prediction trends
2. **Action History**: Detailed log of all bot actions
3. **Performance Metrics**: Win/loss ratios, accuracy statistics
4. **Configuration Panel**: In-UI settings management
5. **Export Functionality**: Save predictions to file
6. **Real-time Charts**: Live updating performance graphs
