#!/usr/bin/env bash

# Configuration
MAIN_CLASS="omega.sleepy.Main"
SKIP_TESTS=false

# Build command based on whether tests are skipped
if [ "$SKIP_TESTS" = true ]; then
  BUILD_CMD="mvn clean install -DskipTests"
else
  BUILD_CMD="mvn clean install"
fi

echo "==> Building project..."
$BUILD_CMD || { echo "Build failed!"; exit 1; }

echo "==> Starting Spark server..."
mvn exec:java -Dexec.mainClass="$MAIN_CLASS"
