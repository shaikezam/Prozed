#!/bin/bash
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
( cd "$ROOT/frontend-ui" && composer install )
for service in project-service issue-service activity-service; do
  ( cd "$ROOT/$service" && mvn -s "$ROOT/settings.xml" clean package -DskipTests )
done
# Foreground: build, start, and stream all container logs in this session (Ctrl-C stops the stack).
docker-compose up --build
