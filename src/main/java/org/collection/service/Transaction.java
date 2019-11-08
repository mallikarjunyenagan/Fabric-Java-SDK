package org.collection.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.collection.client.ChannelClient;
import org.collection.client.FabricClient;
import org.collection.config.Config;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

/**
 * 
 * @author Vivek Gani
 *
 */

public class Transaction {

	private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	private static final String EXPECTED_EVENT_NAME = "event";

	static Collection<ProposalResponse> sendTransaction(FabricClient fabClient, ChannelClient channelClient,
			String[] arguments, String functionName) throws InvalidArgumentException, ProposalException {

		// Transaction Proposal
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

		// Returning transaction Response
		Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
		return responses;
	}
}
