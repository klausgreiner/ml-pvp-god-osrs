#!/bin/bash

echo "🚀 PvP God Mode System Startup Script"
echo "======================================"
echo ""

# Function to cleanup processes on exit
cleanup() {
    echo ""
    echo "🛑 Shutting down PVP system..."
    pkill -f "serve-api" 2>/dev/null
    pkill -f "pvp_ml.api" 2>/dev/null
    pkill -f "python.*pvp_ml" 2>/dev/null
    echo "✅ Cleanup complete"
    exit 0
}

# Set trap to cleanup on script exit
trap cleanup SIGINT SIGTERM

# Check if we're in the right directory
if [ ! -d "pvp-ml" ]; then
    echo "❌ Error: pvp-ml directory not found!"
    echo "Please run this script from the project root directory."
    exit 1
fi

# Check if conda is available
if ! command -v conda &> /dev/null; then
    echo "❌ Error: conda not found!"
    echo "Please install conda or miniconda first."
    echo "Download from: https://docs.conda.io/en/latest/miniconda.html"
    exit 1
fi

echo "✅ Found conda installation"
echo ""

# KILL EXISTING SESSIONS
echo "🔪 Checking for existing PvP ML sessions..."
echo ""

# Kill any existing serve-api processes
if pgrep -f "serve-api" > /dev/null; then
    echo "🔄 Found existing serve-api process, killing it..."
    pkill -f "serve-api"
    sleep 2
fi

# Kill any existing pvp_ml.api processes
if pgrep -f "pvp_ml.api" > /dev/null; then
    echo "🔄 Found existing pvp_ml.api process, killing it..."
    pkill -f "pvp_ml.api"
    sleep 2
fi

# Kill any Python processes related to pvp_ml
if pgrep -f "python.*pvp_ml" > /dev/null; then
    echo "🔄 Found existing pvp_ml Python process, killing it..."
    pkill -f "python.*pvp_ml"
    sleep 2
fi

# Check if port 9999 is still in use
if lsof -Pi :9999 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "⚠️  Port 9999 is still in use, force killing..."
    lsof -ti:9999 | xargs kill -9 2>/dev/null
    sleep 1
fi

echo "✅ All existing sessions killed"
echo ""

# Navigate to pvp-ml directory
cd pvp-ml

# Check if environment exists
if [ ! -d "env" ]; then
    echo "📦 Creating conda environment..."
    conda env create -p ./env -f environment.yml
    if [ $? -ne 0 ]; then
        echo "❌ Failed to create environment!"
        exit 1
    fi
    echo "✅ Environment created successfully"
else
    echo "✅ Found existing environment"
fi

echo ""
echo "🔧 Activating conda environment..."

# Activate the environment
eval "$(conda shell.bash hook)"
conda activate ./env

if [ $? -ne 0 ]; then
    echo "❌ Failed to activate environment!"
    exit 1
fi

echo "✅ Environment activated"
echo ""

# Check if models directory exists and has models
if [ ! -d "models" ] || [ -z "$(ls -A models/*.zip 2>/dev/null)" ]; then
    echo "⚠️  Warning: No models found in models/ directory"
    echo "   The API will start but won't have any models to serve"
    echo "   You can download pre-trained models or train your own"
else
    echo "✅ Found models in models/ directory:"
    ls -1 models/*.zip | sed 's/^/   /'
fi

echo ""
echo "🚀 Starting PvP ML API Server..."
echo "   The server will be available on 127.0.0.1:9999"
echo "   Press Ctrl+C to stop the server"
echo ""

# Start the API server using the serve-api command
python -m pvp_ml.api

echo ""
echo "🛑 API server stopped"
echo ""
echo "📋 Next Steps:"
echo "1. Start the Java bot in RuneMate"
echo "2. Configure the bot to use model: FineTunedNh"
echo "3. Enable the RL model in bot settings"
echo "4. Start the bot in a PvP area"
echo ""
echo "💡 Tips:"
echo "- Keep this terminal open while using the bot"
echo "- Check RuneMate logs for bot operation details"
echo "- The API server must be running for the bot to work"
echo ""
echo "🎮 Happy PvPing!"