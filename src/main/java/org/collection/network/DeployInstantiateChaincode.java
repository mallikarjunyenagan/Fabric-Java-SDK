
package org.collection.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.collection.client.ChannelClient;
import org.collection.client.FabricClient;
import org.collection.config.Config;
import org.collection.config.OrganisationConfig;
import org.collection.user.UserContext;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

/**
 * 
 * @author Vivek Gani
 *
 */

public class DeployInstantiateChaincode {

	private Status status;

	public Status deployChaincode() {
		try {
			// Fetch Crypto Config
			CryptoSuite.Factory.getCryptoSuite();

			UserContext org1Admin = OrganisationConfig.org1Admin();

			UserContext org2Admin = OrganisationConfig.org2Admin();

			UserContext org3Admin = OrganisationConfig.org3Admin();

			FabricClient fabClient = new FabricClient(org1Admin);

			// Channel Configuration
			Channel mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME);

			// Orderer Configuration
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);

			// Peers in all orgs Configuration
			Peer peer0_org1 = OrganisationConfig.peer0Org1Configuration(fabClient);
			Peer peer0_org2 = OrganisationConfig.peer0Org2Configuration(fabClient);
			Peer peer0_org3 = OrganisationConfig.peer0Org3Configuration(fabClient);

			// Adding and initialising Orderer and peer Org1
			mychannel.addOrderer(orderer);
			mychannel.addPeer(peer0_org1);
			mychannel.initialize();

			// Org1 install and instantiate chaincode

			List<Peer> org1Peers = new ArrayList<Peer>();
			org1Peers.add(peer0_org1);

			Collection<ProposalResponse> org1DeployResponse = deployChaincode(fabClient, org1Peers);

			for (ProposalResponse res : org1DeployResponse) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
				System.out.println("****************************************************");
				System.out.println("Org1 Chaincode installation status: " + res.getStatus());
				System.out.println("****************************************************");
			}

			// Org2 install and instantiate chaincode

			mychannel.addPeer(peer0_org2);
			mychannel.initialize();
			fabClient.getInstance().setUserContext(org2Admin);

			List<Peer> org2Peers = new ArrayList<Peer>();
			org2Peers.add(peer0_org2);

			// Installing Chaincode to Org2
			Collection<ProposalResponse> org2DeployResponse = deployChaincode(fabClient, org2Peers);
			for (ProposalResponse res : org2DeployResponse) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
				System.out.println("****************************************************");
				System.out.println("Org2 Chaincode installation status: " + res.getStatus());
				System.out.println("****************************************************");
			}

			ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

			String[] arguments = { "" };

			// Instantiating Chaincode to Org2
			Collection<ProposalResponse> org2InstantiationResponse = instantiateChainCode(channelClient, arguments);

			for (ProposalResponse res : org2InstantiationResponse) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code instantiation " + res.getStatus());
				System.out.println("****************************************************");
				System.out.println("Org2 Chaincode instantiation status: " + res.getStatus());
				System.out.println("****************************************************");

			}

			// Org3 install and instantiate chaincode

			mychannel.addPeer(peer0_org3);
			mychannel.initialize();

			fabClient.getInstance().setUserContext(org3Admin);

			List<Peer> org3Peers = new ArrayList<Peer>();
			org3Peers.add(peer0_org3);

			// Installing Chaincode to Org3
			Collection<ProposalResponse> org3DeployResponse = deployChaincode(fabClient, org3Peers);
			for (ProposalResponse res : org3DeployResponse) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
				System.out.println("****************************************************");
				System.out.println("Org3 Chaincode installation status: " + res.getStatus());
				System.out.println("****************************************************");
			}

			ChannelClient channelClient1 = new ChannelClient(mychannel.getName(), mychannel, fabClient);

			String[] arguments1 = { "" };

			// Instantiating Chaincode to Org3
			Collection<ProposalResponse> org3InstantiationResponse = instantiateChainCode(channelClient1, arguments1);

			for (ProposalResponse res : org3InstantiationResponse) {
				Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
						Config.CHAINCODE_1_NAME + "- Chain code instantiation " + res.getStatus());
				System.out.println("****************************************************");
				System.out.println("Org3 Chaincode instantiation status: " + res.getStatus());
				System.out.println("****************************************************");
				this.status = res.getStatus();
			}
			return status;
		} catch (Exception e) {
			e.printStackTrace();
			return status;

		}
	}

	private Collection<ProposalResponse> instantiateChainCode(ChannelClient channelClient, String[] arguments)
			throws InvalidArgumentException, ProposalException, ChaincodeEndorsementPolicyParseException, IOException {
		Collection<ProposalResponse> org2InstantiationResponse = channelClient.instantiateChainCode(
				Config.CHAINCODE_1_NAME, Config.CHAINCODE_1_VERSION, Config.CHAINCODE_1_PATH, Type.GO_LANG.toString(),
				"init", arguments, null);
		return org2InstantiationResponse;
	}

	private Collection<ProposalResponse> deployChaincode(FabricClient fabClient, List<Peer> orgPeers)
			throws InvalidArgumentException, IOException, ProposalException {
		Collection<ProposalResponse> response = fabClient.deployChainCode(Config.CHAINCODE_1_NAME,
				Config.CHAINCODE_1_PATH, Config.CHAINCODE_ROOT_DIR, Type.GO_LANG.toString(), Config.CHAINCODE_1_VERSION,
				orgPeers);
		return response;
	}

}