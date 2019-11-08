docker-compose -f docker-compose.yml -f docker-compose-couch.yml up -d
docker start $(docker ps -a | grep dev | awk '{print$1}')

