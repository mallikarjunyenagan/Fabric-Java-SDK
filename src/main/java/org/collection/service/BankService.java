
package org.collection.service;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.collection.chaincode.invocation.QueryChaincode;
import org.collection.client.ChannelClient;
import org.collection.client.FabricClient;
import org.collection.config.Config;
import org.collection.config.InitializeClientConfig;
import org.collection.request.BankRequest;
import org.collection.request.BankUpdateRequest;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 
 * @author Vivek Gani and Akhil
 *
 */

public class BankService {

	private String transactionId;
	private String stringResponse;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	// Posting Bank Details to CouchDb

	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";

	public String postObject(String jsonString) {

		try {
			// Auto generating bankId
			String bankId = UUID.randomUUID().toString();

			// Initializing Configuration
			FabricClient fabClient = InitializeClientConfig.fabricClient();
			ChannelClient channelClient = InitializeClientConfig.channelClient(fabClient);
			InitializeClientConfig.channelConfiguration(fabClient, channelClient);

			// Arguments
			String[] arguments = { bankId, jsonString };

			// Function Name to be executed in GoLang
			String functionName = "insertOrUpdateData";

			// Transaction Execution and Response
			Collection<ProposalResponse> responses = Transaction.sendTransaction(fabClient, channelClient, arguments,
					functionName);
			for (ProposalResponse res : responses) {
				Status status = res.getStatus();
				this.transactionId = res.getTransactionID();
				Logger.getLogger(BankService.class.getName()).log(Level.INFO,
						"posted Data " + Config.CHAINCODE_1_NAME + ". Status - " + status);

			}
			return "Inserted Details Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error occured, Please try again";
		}
	}



	private Collection<ProposalResponse> Transactionrequest(FabricClient fabClient, ChannelClient channelClient,
			String[] arguments, String functionName) throws InvalidArgumentException, ProposalException {
		TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
		ChaincodeID ccid = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).build();
		request.setChaincodeID(ccid);
		request.setFcn(functionName);
		request.setArgs(arguments);
		request.setProposalWaitTime(1000);
		Map<String, byte[]> tm2 = new HashMap<>();
		tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
		tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
		tm2.put("result", ":)".getBytes(UTF_8));
		tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);
		request.setTransientMap(tm2);

		Collection<ProposalResponse> proposalResponses = channelClient.sendTransactionProposal(request);

		for (ProposalResponse res : proposalResponses) {
			Status status = res.getStatus();
			this.transactionId = res.getTransactionID();
			Logger.getLogger(BankService.class.getName()).log(Level.INFO,
					"Inserted Bank Details on  " + Config.CHAINCODE_1_NAME + ". Status - " + status);

		}
		return proposalResponses;
	}

	// Getting all records from CouchDb

	public String getAllData() {
		try {
			ChannelClient channelClient = initialiseConfig();

			Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "getAllBankDetails  - " + null);

			Collection<ProposalResponse> responses1Query = channelClient.queryByChainCode("records",
					"getAllData", null);
			for (ProposalResponse pres : responses1Query) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				this.stringResponse = new String(pres.getChaincodeActionResponsePayload());
				Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
			}
			return this.stringResponse;
		} catch (Exception e) {
			e.printStackTrace();
			return "Error occured, Please try again";
		}

	}

	public String response;

	// Getting history for a particular record from ledger

	public String getHistory(String key) {
		try {
			ChannelClient channelClient = initialiseConfig();

			String[] args1 = { key };
			Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO,
					"Getting History for Record - " + args1[0]);

			Collection<ProposalResponse> responses1Query = channelClient.queryByChainCode("records",
					"getHistoryForRecord", args1);
			for (ProposalResponse pres : responses1Query) {
				String stringResponse = new String(pres.getChaincodeActionResponsePayload());
				this.response = new String(pres.getChaincodeActionResponsePayload());

				Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
			}
			return this.response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	// Updating Bank Details in CouchDb

	public String updateObjectByKey(String key,String jsonstring) {

		try {

			// Initializing Configuration
			FabricClient fabClient = InitializeClientConfig.fabricClient();
			ChannelClient channelClient = InitializeClientConfig.channelClient(fabClient);
			InitializeClientConfig.channelConfiguration(fabClient, channelClient);

			// Arguments
			String[] arguments = {key, jsonstring };

			// Function Name to be executed in GoLang
			String functionName = "insertOrUpdateData";

			// Transaction Execution and Response
			Collection<ProposalResponse> responses = Transaction.sendTransaction(fabClient, channelClient, arguments,
					functionName);
			for (ProposalResponse res : responses) {
				Status status = res.getStatus();
				this.transactionId = res.getTransactionID();
				Logger.getLogger(BankService.class.getName()).log(Level.INFO,
						"Updated Bank Details on  " + Config.CHAINCODE_1_NAME + ". Status - " + status);

			}
			return "Updated Bank Details Successfully";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error occured, Please try again";
		}
	}

	// Initializing Configuration

	private ChannelClient initialiseConfig() throws MalformedURLException, IllegalAccessException,
			InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException,
			NoSuchMethodException, InvocationTargetException, Exception, TransactionException {
		FabricClient fabClient = InitializeClientConfig.fabricClient();
		ChannelClient channelClient = InitializeClientConfig.channelClient(fabClient);
		InitializeClientConfig.channelConfiguration(fabClient, channelClient);
		return channelClient;
	}
}