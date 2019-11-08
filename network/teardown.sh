#!/bin/bash
#
# Exit on first error, print all commands.
set -ev

# Shut down the Docker containers for the system tests.
docker-compose -f docker-compose.yml -f docker-compose-couch.yml kill && docker-compose -f docker-compose.yml -f docker-compose-couch.yml down
if [ "$(docker ps -aq)" ]; then
	docker rm -f $(docker ps -aq)
fi

# remove chaincode docker images
if [ "$(docker images dev-* -q)" ]; then
	docker rmi $(docker images dev-* -q)
fi
echo y | docker volume prune
# Your system is now clean
