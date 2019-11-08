
package org.collection.network;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.collection.client.FabricClient;
import org.collection.config.OrganisationConfig;
import org.collection.user.UserContext;
import org.collection.util.Util;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

/**
 * 
 * @author Vivek Gani
 *
 */

/**
 * 
 * class for implementing all the services of blockchain such as create channel,
 * recreate channel, install chaincode, instantiate chaincode, invoke chaincode,
 * query chaincode
 *
 */

public class CreateChannel {

	/**
	 * Create the channel returns channel that has been constructed
	 */
	public String createChannel() {

		try {

			// Fetch Crypto Config
			CryptoSuite.Factory.getCryptoSuite();

			// Cleanup previous users
			Util.cleanUp();

			UserContext org1Admin = OrganisationConfig.org1Admin();

			UserContext org2Admin = OrganisationConfig.org2Admin();

			UserContext org3Admin = OrganisationConfig.org3Admin();

			FabricClient fabClient = new FabricClient(org1Admin);

			// Create a new channel
			Orderer orderer = OrganisationConfig.ordererConfiguration(fabClient);

			Channel mychannel = OrganisationConfig.channelConfiguration(org1Admin, fabClient, orderer);

			// Peers in all orgs Configuration
			Peer peer0_org1 = OrganisationConfig.peer0Org1Configuration(fabClient);
			Peer peer0_org2 = OrganisationConfig.peer0Org2Configuration(fabClient);
			Peer peer0_org3 = OrganisationConfig.peer0Org3Configuration(fabClient);

			// Adding and initialising Orderer and peer Org1
			mychannel.joinPeer(peer0_org1);
			mychannel.addOrderer(orderer);
			mychannel.initialize();

			// Adding Org2 to mychannel
			fabClient.getInstance().setUserContext(org2Admin);
			mychannel = fabClient.getInstance().getChannel("channel");
			mychannel.joinPeer(peer0_org2);

			// Adding Org3 to mychannel
			fabClient.getInstance().setUserContext(org3Admin);
			mychannel = fabClient.getInstance().getChannel("channel");
			mychannel.joinPeer(peer0_org3);

			Logger.getLogger(CreateChannel.class.getName()).log(Level.INFO, "Channel created " + mychannel.getName());
			Collection peers = mychannel.getPeers();
			Iterator peerIter = peers.iterator();
			while (peerIter.hasNext()) {
				Peer pr = (Peer) peerIter.next();
				Logger.getLogger(CreateChannel.class.getName()).log(Level.INFO, pr.getName() + " at " + pr.getUrl());
			}

			return "Channel created Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error occured, Please try again";
		}

	}

}
