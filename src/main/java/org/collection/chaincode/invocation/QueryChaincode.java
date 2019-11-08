
package org.collection.chaincode.invocation;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.collection.client.CAClient;
import org.collection.client.ChannelClient;
import org.collection.client.FabricClient;
import org.collection.config.Config;
import org.collection.user.UserContext;
import org.collection.util.Util;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;

import com.google.gson.JsonObject;

/**
 * 
 * @author Vivek Gani
 *
 */

public class QueryChaincode {

	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";
	private String stringResponse;
	private JsonObject jsonObject;

	public String getStringResponse() {
		return stringResponse;
	}

	public void setStringResponse(String stringResponse) {
		this.stringResponse = stringResponse;
	}

	public String queryChaincode(String name) {
		try {
			Util.cleanUp();
			String caUrl = Config.CA_ORG1_URL;
			CAClient caClient = new CAClient(caUrl, null);
			// Enroll Admin to Org1MSP
			UserContext adminUserContext = new UserContext();
			adminUserContext.setName(Config.ADMIN);
			adminUserContext.setAffiliation(Config.ORG1);
			adminUserContext.setMspId(Config.ORG1_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

			FabricClient fabClient = new FabricClient(adminUserContext);

			ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
			Channel channel = channelClient.getChannel();
			Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
			channel.addPeer(peer);
			channel.addOrderer(orderer);
			channel.initialize();

			String[] args1 = { name };
			Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "getBankInfo  - " + args1[0]);

			Collection<ProposalResponse> responses1Query = channelClient.queryByChainCode("fabcar", "getBankInfo",
					args1);
			for (ProposalResponse pres : responses1Query) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				this.stringResponse = new String(pres.getChaincodeActionResponsePayload());
				Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
			}
			return "Queried Chaincode Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error occured, Please try again";
		}
	}

	public String queryChaincodeAllData() {
		try {
			Util.cleanUp();
			String caUrl = Config.CA_ORG1_URL;
			CAClient caClient = new CAClient(caUrl, null);
			// Enroll Admin to Org1MSP
			UserContext adminUserContext = new UserContext();
			adminUserContext.setName(Config.ADMIN);
			adminUserContext.setAffiliation(Config.ORG1);
			adminUserContext.setMspId(Config.ORG1_MSP);
			caClient.setAdminUserContext(adminUserContext);
			adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

			FabricClient fabClient = new FabricClient(adminUserContext);

			ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
			Channel channel = channelClient.getChannel();
			Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
			Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
			channel.addPeer(peer);
			channel.addOrderer(orderer);
			channel.initialize();

			Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "queryAllData  - " + null);

			Collection<ProposalResponse> responses1Query = channelClient.queryByChainCode("records", "queryAllData",
					null);
			for (ProposalResponse pres : responses1Query) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				this.stringResponse = new String(pres.getChaincodeActionResponsePayload());

				Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
			}
			return "Queried Chaincode Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error occured, Please try again";
		}
	}

}