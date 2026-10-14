#!/bin/sh
# Starts Dryrun. Needs a JDK (Java 17 or newer) on PATH. Press Ctrl+C to stop.
cd "$(dirname "$0")" || exit 1
exec java server/Server.java "$@"
