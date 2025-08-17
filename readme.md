# PvP God Mode

A RuneMate bot that uses reinforcement learning models to automatically play PvP combat in Old School RuneScape.

## Overview

This bot integrates with the pvp-ml reinforcement learning system to make intelligent combat decisions in real-time. It connects to a socket-based API that serves trained PvP models, allowing the bot to:

- Analyze the current combat situation through comprehensive observations
- Get AI-powered action recommendations from trained models
- Execute complex combat strategies including prayer switching, gear swapping, and movement
- Adapt to different opponents and combat scenarios

## Architecture

### Core Components

- **PvpGodMode.java** - Main bot class that orchestrates the system
- **PvpCombatNode.java** - Combat logic that processes observations and executes actions
- **PvpRlModel.java** - Client for communicating with the pvp-ml socket API
- **PvpObservations.java** - Generates comprehensive game state observations
- **PvpActions.java** - Defines and executes available combat actions
- **PvpGodModeSettings.java** - Bot configuration and settings

### Communication Protocol

The bot communicates with the pvp-ml API using a TCP socket connection on `127.0.0.1:9999`. The protocol uses JSON messages with the following structure:

#### Request Format
```json
{
  "model": "FineTunedNh",
  "actionMasks": [
    [true, true, true, true],           // attack (4 actions)
    [true, true, true],                 // melee_attack_type (3 actions)
    [true, true, true],                 // ranged_attack_type (3 actions)
    [true, true, true, true],           // mage_attack_type (4 actions)
    [true, true, true, true, true],     // potion (5 actions)
    [true, true],                       // food (2 actions)
    [true, true],                       // karambwan (2 actions)
    [true, true],                       // veng (2 actions)
    [true, true],                       // gear (2 actions)
    [true, true, true, true, true],     // movement (5 actions)
    [true, true, true, true, true, true, true], // farcast_distance (7 actions)
    [true, true, true, true, true, true] // prayer (6 actions)
  ],
  "obs": [/* 176 observation values */],
  "deterministic": false,
  "returnLogProb": false,
  "returnEntropy": false,
  "returnValue": false,
  "returnProbs": true,
  "extensions": []
}
```

#### Response Format
```json
{
  "action": [/* 12 action indices, one per action head */],
  "logProb": null,
  "entropy": null,
  "values": null,
  "probs": [/* action probabilities for each action head */],
  "extensionResults": []
}
```

## Setup

### Prerequisites

1. **RuneMate** - The bot framework
2. **pvp-ml API Server** - Must be running to serve models

### Starting the pvp-ml API

1. Navigate to the `pvp-ml` directory
2. Activate the conda environment: `conda activate ./env`
3. Start the API server: `serve-api`
4. The API will be available on `127.0.0.1:9999`

### Available Models

The API serves models from the `pvp-ml/models` directory:
- **FineTunedNh** - Fine-tuned model for human-like behavior (default)
- **GeneralizedNh** - General-purpose PvP model
- Various other trained models

### Bot Configuration

Configure the bot through the RuneMate settings panel:
- **Enable RL Model** - Toggle the AI system on/off
- **RL Model Name** - Select which model to use (e.g., "FineTunedNh")

## Action Space

The bot uses a hierarchical action space with 11 action heads:

1. **Attack Style** (4 actions) - Choose combat style (none, mage, ranged, melee)
2. **Melee Attack Type** (3 actions) - Melee attack specifics
3. **Ranged Attack Type** (3 actions) - Ranged attack specifics  
4. **Mage Attack Type** (4 actions) - Magic spell selection
5. **Potion Usage** (5 actions) - Health, prayer, and combat potions
6. **Food Consumption** (2 actions) - Primary food usage
7. **Karambwan** (2 actions) - Secondary food for combo eating
8. **Vengeance** (2 actions) - Lunar spell usage
9. **Gear Swapping** (2 actions) - Tank gear switching
10. **Movement** (5 actions) - Positioning and movement
11. **Farcast Distance** (7 actions) - Ranged positioning
12. **Prayer** (6 actions) - Combat and utility prayers

## Observation Space

The bot provides 176 observations covering:
- Player and target combat states
- Health, prayer, and special attack energy
- Equipment and gear statistics
- Combat timing and cycles
- Movement and positioning
- Prayer status and effectiveness
- Recent combat history and statistics
- Gear defense bonuses and attack stats
- Special weapon loadouts and restrictions
- Game mode rules and restrictions

## Usage

1. Start the pvp-ml API server
2. Load the bot in RuneMate
3. Configure settings and enable the RL model
4. Start the bot in a PvP area
5. The bot will automatically analyze the situation and make combat decisions

## Development

### Building

The project uses Gradle for building:
```bash
./gradlew build
```

### Testing

Run tests with:
```bash
./gradlew test
```

### Adding New Actions

To add new combat actions:
1. Update `PvpActions.java` with the new action
2. Update the action masks in `PvpRlModel.java` if needed
3. Ensure the action is properly integrated with the observation space

## Troubleshooting

### Common Issues

1. **API Connection Failed** - Ensure the pvp-ml API server is running
2. **Model Not Found** - Check that the specified model exists in the models directory
3. **Invalid Action Index** - Verify action masks match the expected structure

### Debug Mode

Enable debug logging to see detailed API communication:
- Check RuneMate logs for detailed bot operation
- Monitor API server logs for request/response details

## License

This project is licensed under the same terms as the pvp-ml system.

## Contributing

Contributions are welcome! Please ensure:
- Code follows the existing style
- New features include appropriate tests
- Documentation is updated for any API changes
