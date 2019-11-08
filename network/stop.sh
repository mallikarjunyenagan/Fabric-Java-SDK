#!/bin/bash
#
# Exit on first error, print all commands.
set -ev

# Shut down the Docker containers that might be currently running.
docker-compose -f docker-compose.yml -f docker-compose-couch.yml stop
