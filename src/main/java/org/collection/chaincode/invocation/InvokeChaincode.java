
package org.collection.chaincode.invocation;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.collection.client.CAClient;
import org.collection.client.ChannelClient;
import org.collection.client.FabricClient;
import org.collection.config.Config;
import org.collection.user.UserContext;
import org.collection.util.Util;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 
 *     
 *
 */
public class InvokeChaincode {

	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";
	private String transactionId;

	private String address;
	private String bankId;
	private String bankName;
	private String loanType;
	private String bucket;
	private UUID uuid;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String invokeChaincode() {

		try {

			// url for retriving data
//			final String uri = "http://192.168.1.25/Dynamics/Table/GetDetails";

			// converting to gson format
			Gson gson = new Gson();

			// to consume data from url
			RestTemplate restTemplate = new RestTemplate();
			String result = restTemplate.getForObject(Config.GETDETAILS_URL, String.class);

			// converting into object
			JsonObject convertedObject = new Gson().fromJson(result, JsonObject.class);

			// getting Banks Info data
			JsonArray a = convertedObject.get("BanksInfo").getAsJsonArray();
			// converting array string to json array
			String bankJson = gson.toJson(a);
			JSONArray jsonArray = new JSONArray(bankJson);

			for (int i = 0; i < jsonArray.length(); i++) {

				// converting from jsonarray to json object
				JSONObject jsonObject1 = jsonArray.getJSONObject(i);

				this.address = jsonObject1.optString("Addresses");
				this.bankId = jsonObject1.optString("BankId");
				this.bankName = jsonObject1.optString("BankName");
				this.loanType = jsonObject1.optString("LoanType");
				this.bucket = jsonObject1.optString("Bucket");

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
				TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
				ChaincodeID ccid = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).build();
				request.setChaincodeID(ccid);
				request.setFcn("bankInfoMethod");
				String[] arguments = { this.bankId, this.address, "hdfc", this.loanType, this.bucket, "1" };

				request.setArgs(arguments);
				request.setProposalWaitTime(1000);
				Map<String, byte[]> tm2 = new HashMap<>();
				tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
				tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
				tm2.put("result", ":)".getBytes(UTF_8));
				tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);
				request.setTransientMap(tm2);

				Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
				for (ProposalResponse res : responses) {
					Status status = res.getStatus();
					this.transactionId = res.getTransactionID();
					Logger.getLogger(InvokeChaincode.class.getName()).log(Level.INFO,
							"Invoked createCar on " + Config.CHAINCODE_1_NAME + ". Status - " + status);
				}
			}
			return "Invoked Chaincode Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error occured, Please try again";
		}
	}

	public String invokeChaincodeCollectionInfo() {

		try {
			this.uuid = UUID.randomUUID();
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

			TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
			ChaincodeID ccid = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).build();
			request.setChaincodeID(ccid);
			request.setFcn("bankInfoMethod1");
			String[] arguments = { "ed", "yashdad", "sdjsd", "dfdcfd", "sfsfss" };

			request.setArgs(arguments);
			request.setProposalWaitTime(1000);
			Map<String, byte[]> tm2 = new HashMap<>();
			tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
			tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
			tm2.put("result", ":)".getBytes(UTF_8));
			tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);
			request.setTransientMap(tm2);

			Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
			for (ProposalResponse res : responses) {
				Status status = res.getStatus();
				this.transactionId = res.getTransactionID();
				Logger.getLogger(InvokeChaincode.class.getName()).log(Level.INFO,
						"Invoked createCar on " + Config.CHAINCODE_1_NAME + ". Status - " + status);
			}

			return "Invoked Chaincode Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error occured, Please try again";
		}
	}

}