package org.collection.config;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.collection.client.FabricClient;
import org.collection.user.UserContext;
import org.collection.util.Util;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

public class OrganisationConfig {
	public static UserContext org3Admin()
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CryptoException {
		// Fetch Org3 Configuration
		UserContext org3Admin = new UserContext();
		File pkFolder3 = new File(Config.ORG3_USR_ADMIN_PK);
		File[] pkFiles3 = pkFolder3.listFiles();
		File certFolder3 = new File(Config.ORG3_USR_ADMIN_CERT);
		File[] certFiles3 = certFolder3.listFiles();
		Enrollment enrollOrg3Admin = Util.getEnrollment(Config.ORG3_USR_ADMIN_PK, pkFiles3[0].getName(),
				Config.ORG3_USR_ADMIN_CERT, certFiles3[0].getName());
		org3Admin.setEnrollment(enrollOrg3Admin);
		org3Admin.setMspId(Config.ORG3_MSP);
		org3Admin.setName(Config.ADMIN);
		return org3Admin;
	}

	public static UserContext org2Admin()
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CryptoException {
		// Fetch Org2 Configuration
		UserContext org2Admin = new UserContext();
		File pkFolder2 = new File(Config.ORG2_USR_ADMIN_PK);
		File[] pkFiles2 = pkFolder2.listFiles();
		File certFolder2 = new File(Config.ORG2_USR_ADMIN_CERT);
		File[] certFiles2 = certFolder2.listFiles();
		Enrollment enrollOrg2Admin = Util.getEnrollment(Config.ORG2_USR_ADMIN_PK, pkFiles2[0].getName(),
				Config.ORG2_USR_ADMIN_CERT, certFiles2[0].getName());
		org2Admin.setEnrollment(enrollOrg2Admin);
		org2Admin.setMspId(Config.ORG2_MSP);
		org2Admin.setName(Config.ADMIN);
		return org2Admin;
	}

	public static UserContext org1Admin()
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CryptoException {
		// Fetch Org1 Configuration
		UserContext org1Admin = new UserContext();
		File pkFolder1 = new File(Config.ORG1_USR_ADMIN_PK);
		File[] pkFiles1 = pkFolder1.listFiles();
		File certFolder1 = new File(Config.ORG1_USR_ADMIN_CERT);
		File[] certFiles1 = certFolder1.listFiles();
		Enrollment enrollOrg1Admin = Util.getEnrollment(Config.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
				Config.ORG1_USR_ADMIN_CERT, certFiles1[0].getName());
		org1Admin.setEnrollment(enrollOrg1Admin);
		org1Admin.setMspId(Config.ORG1_MSP);
		org1Admin.setName(Config.ADMIN);
		return org1Admin;
	}

	public static Peer peer0Org3Configuration(FabricClient fabClient) throws InvalidArgumentException {
		return fabClient.getInstance().newPeer(Config.ORG3_PEER_0, Config.ORG3_PEER_0_URL);
	}

	public static Peer peer0Org2Configuration(FabricClient fabClient) throws InvalidArgumentException {
		return fabClient.getInstance().newPeer(Config.ORG2_PEER_0, Config.ORG2_PEER_0_URL);
	}

	public static Peer peer0Org1Configuration(FabricClient fabClient) throws InvalidArgumentException {
		return fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
	}

	public static Channel channelConfiguration(UserContext org1Admin, FabricClient fabClient, Orderer orderer)
			throws IOException, InvalidArgumentException, TransactionException {
		// Channel Configuration
		ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(Config.CHANNEL_CONFIG_PATH));

		byte[] channelConfigurationSignatures = fabClient.getInstance()
				.getChannelConfigurationSignature(channelConfiguration, org1Admin);

		Channel mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME, orderer, channelConfiguration,
				channelConfigurationSignatures);
		return mychannel;
	}

	public static Orderer ordererConfiguration(FabricClient fabClient) throws InvalidArgumentException {
		// Orderer Configuration
		Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
		return orderer;
	}
}
