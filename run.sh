#!/bin/bash

# ==============================================================================
# 🚀 AZMEW JAVA MONOREPO AUTO-START SCRIPT
# This script handles everything: Database, Tunnel, Backend (Java), and Frontend (React).
# It cleans ports, builds Maven dependencies, and updates environment variables.
# ==============================================================================

ROOT_DIR=$(pwd)
BE_DIR="$ROOT_DIR/azmew-be"
FE_DIR="$ROOT_DIR/azmew-fe"
ENV_FILE="$BE_DIR/.env"
TUNNEL_LOG="$ROOT_DIR/tunnel.log"

echo "--------------------------------------------------------"
echo "🌟 INITIALIZING AZMEW JAVA CONNECTOR ECOSYSTEM"
echo "--------------------------------------------------------"

# 🛠️ 1. CLEANUP PREVIOUS PROCESSES
echo "🧹 Cleaning up existing processes on ports 8080 (BE) and 5173 (FE)..."
# Kill Java (8080)
sync
taskkill //F //IM java.exe //T 2>/dev/null || true
# Kill Node/Vite (5173)
npx kill-port 5173 2>/dev/null || true
npx kill-port 8080 2>/dev/null || true

# 🐳 2. START DATABASE
echo "🐘 Starting Docker Database..."
cd "$BE_DIR" && docker-compose up -d
cd "$ROOT_DIR"

# 🌐 3. START TUNNEL (Serveo)
echo "🌐 Starting Serveo Tunnel (Port 8080)..."
> "$TUNNEL_LOG"
ssh -o StrictHostKeyChecking=no -R 80:localhost:8080 serveo.net > "$TUNNEL_LOG" 2>&1 &
TUNNEL_PID=$!

echo "⏳ Waiting for Tunnel URL..."
TUNNEL_URL=""
MAX_RETRIES=30
COUNT=0
while [ -z "$TUNNEL_URL" ] && [ $COUNT -lt $MAX_RETRIES ]; do
  sleep 1
  if grep -q "Forwarding HTTP traffic from" "$TUNNEL_LOG"; then
     TUNNEL_URL=$(grep -o 'https://[^ ]*' "$TUNNEL_LOG" | head -n 1)
  fi
  COUNT=$((COUNT+1))
  echo -n "."
done
echo ""

if [ -z "$TUNNEL_URL" ]; then
  echo "⚠️  Tunnel failed. Using localhost:8080 for Redirect URIs."
  TUNNEL_URL="http://localhost:8080"
else
  echo "✅ TUNNEL ESTABLISHED: $TUNNEL_URL"
fi

# 📝 4. UPDATE ENV FILE
if [ ! -f "$ENV_FILE" ]; then
    echo "📄 Creating .env from .env.example..."
    cp "$BE_DIR/.env.example" "$ENV_FILE"
fi

echo "📝 Updating Redirect URIs..."
sed -i "s|FACEBOOK_REDIRECT_URI=.*|FACEBOOK_REDIRECT_URI=${TUNNEL_URL}/api/auth/facebook/callback|" "$ENV_FILE"
sed -i "s|TIKTOK_REDIRECT_URI=.*|TIKTOK_REDIRECT_URI=${TUNNEL_URL}/api/auth/tiktok/callback|" "$ENV_FILE"

# ☕ 5. BUILD & START BACKEND
echo "☕ Building and Starting Java Backend (Spring Boot)..."
cd "$BE_DIR"
# Ensure mvnw is executable if on Linux/Mac, but this is Windows Git Bash
chmod +x mvnw 2>/dev/null || true
./mvnw clean install -DskipTests
./mvnw spring-boot:run > "$ROOT_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
cd "$ROOT_DIR"

# ⚛️ 6. START FRONTEND
echo "⚛️ Starting React Frontend (Vite)..."
cd "$FE_DIR"
npm run dev > "$ROOT_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
cd "$ROOT_DIR"

echo "--------------------------------------------------------"
echo "🎉 SYSTEM FULL-STACK READY!"
echo "--------------------------------------------------------"
echo "👉 BACKEND:   http://localhost:8080"
echo "👉 FRONTEND:  http://localhost:5173"
echo "👉 TUNNEL:    $TUNNEL_URL (For Meta/TikTok)"
echo "--------------------------------------------------------"
echo "Check backend.log and frontend.log for details."
echo "Press Ctrl+C to stop all services."

# 🛑 CLEANUP ON EXIT
cleanup() {
    echo ""
    echo "🛑 Shutting down all services..."
    kill $BACKEND_PID $FRONTEND_PID $TUNNEL_PID 2>/dev/null
    echo "👋 Goodbye!"
}
trap cleanup EXIT
wait
