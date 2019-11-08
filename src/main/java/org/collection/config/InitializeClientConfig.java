package org.collection.config;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import org.collection.client.CAClient;
import org.collection.client.ChannelClient;
import org.collection.client.FabricClient;
import org.collection.user.UserContext;
import org.collection.util.Util;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

/**
 * 
 *     
 *
 */

public class InitializeClientConfig {
	public static void channelConfiguration(FabricClient fabClient, ChannelClient channelClient)
			throws InvalidArgumentException, TransactionException {
		Channel channel = channelClient.getChannel();
		Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
		Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
		channel.addPeer(peer);
		channel.addOrderer(orderer);
		channel.initialize();
	}

	public static ChannelClient channelClient(FabricClient fabClient) throws InvalidArgumentException {
		return fabClient.createChannelClient(Config.CHANNEL_NAME);
	}

	public static FabricClient fabricClient()
			throws MalformedURLException, IllegalAccessException, InstantiationException, ClassNotFoundException,
			CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException, Exception {
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
		return fabClient;
	}
}
