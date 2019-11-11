#!/bin/bash
#
# Exit on first error, print all commands.
set -e

#Start from here
startTime=$SECONDS
echo -e "\nStopping the previous network (if any)"
BASEDIR=$(dirname $0)
echo "Script location: ${BASEDIR}"

docker-compose -f $BASEDIR/docker-compose.yml -f $BASEDIR/docker-compose-couch.yml down
$BASEDIR/./teardown.sh
# If need to re-generate the artifacts, uncomment the following lines and run
cd $BASEDIR/
  if [ ! -d "crypto-config" ]; then
  cryptogen generate --config=./crypto-config.yaml
  mkdir config
  configtxgen -profile TwoOrgsOrdererGenesis -outputBlock ./config/genesis.block
  configtxgen -profile TwoOrgsChannel -outputCreateChannelTx ./config/channel.tx -channelID channel
  configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./config/Org1MSPanchors.tx -channelID channel -asOrg Org1MSP
  configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./config/Org2MSPanchors.tx -channelID channel -asOrg Org2MSP
  configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ./config/Org3MSPanchors.tx -channelID channel -asOrg Org3MSP
   rm -Rf ../network_resources/config
   rm -Rf ../network_resources/crypto-config
  cp -r ./config/ ../network_resources
  cp -r ./crypto-config/ ../network_resources
fi

  export BYFN_CA1_PRIVATE_KEY=$(cd ../network_resources/crypto-config/peerOrganizations/org1.example.com/ca && ls *_sk)
  export BYFN_CA2_PRIVATE_KEY=$(cd ../network_resources/crypto-config/peerOrganizations/org2.example.com/ca && ls *_sk)
  export BYFN_CA3_PRIVATE_KEY=$(cd ../network_resources/crypto-config/peerOrganizations/org3.example.com/ca && ls *_sk)
# Create and Start the Docker containers for the network
cd -
echo -e "\nSetting up the Hyperledger Fabric 1.4.1 network"
docker-compose -f $BASEDIR/docker-compose.yml -f $BASEDIR/docker-compose-couch.yml up -d
timeElasped=$((SECONDS-startTime))
echo -e "\n Total Time Elapsed : $timeElasped seconds"
echo -e "\n Network setup completed!!\n"
