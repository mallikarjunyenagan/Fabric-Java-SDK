package org.collection.controller;

import java.io.IOException;

import org.collection.chaincode.invocation.InvokeChaincode;
import org.collection.chaincode.invocation.IpfsFileOperations;
import org.collection.chaincode.invocation.QueryChaincode;
import org.collection.network.CreateChannel;
import org.collection.network.DeployInstantiateChaincode;
import org.collection.response.createChannelResponse;
import org.collection.response.deployChaincodeResponse;
import org.collection.response.invokeChaincodeResponse;
import org.collection.response.ipfsFileDownloadResponse;
import org.collection.response.ipfsFileUploadResponse;
import org.collection.response.queryResponse;
import org.collection.response.registerUserResponse;
import org.collection.user.RegisterEnrollUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@RestController
public class ChaincodeController {

	/**
	 * Creates the channel
	 * 
	 * @returns the channel creation status
	 */
	@RequestMapping(value = "/api/createChannel", method = RequestMethod.POST)
	public createChannelResponse createChannel() throws Exception {

		createChannelResponse create = new createChannelResponse();

		CreateChannel channelClass = new CreateChannel();
		String response = channelClass.createChannel();
		create.setResponse(response);

		if (response == "Channel created Successfully") {
			create.setStatus("success");

		} else {
			create.setStatus("failed");

		}
		return create;
	}
	

	/**
	 * Installs and Instantiates the Chaincode
	 * 
	 * @returns the Installation and Instantiation status
	 */
	@RequestMapping(value = "/api/deployChaincode", method = RequestMethod.POST)
	public deployChaincodeResponse deployChaincode() throws Exception {

		deployChaincodeResponse deploy = new deployChaincodeResponse();
		DeployInstantiateChaincode instantiate = new DeployInstantiateChaincode();

		String result = instantiate.deployChaincode().toString();
		deploy.setResponse(result);
		System.out.println(deploy.getResponse());

		return deploy;

	}

	/**
	 * Registers the User
	 * 
	 * @returns the User Registration status
	 */
	@RequestMapping(value = "/api/registerUser", method = RequestMethod.POST)
	public registerUserResponse registerUser() throws Exception {

		registerUserResponse registerResponse = new registerUserResponse();

		RegisterEnrollUser registerUser = new RegisterEnrollUser();
		String response = registerUser.registerEnrollUser();
		registerResponse.setMessage(response);
		if (response == "Registered and Enrolled User Successfully") {
			registerResponse.setStatus("success");

			return registerResponse;
		} else {
			registerResponse.setStatus("failed");

			return registerResponse;
		}

	}

	/**
	 * Invokes the Chaincode
	 * 
	 * @returns the Chaincode Invocation status
	 */
	@RequestMapping(value = "/api/invokeChaincode", method = RequestMethod.POST)
	public invokeChaincodeResponse invokeChaincode() throws Exception {

		invokeChaincodeResponse invokeResponse = new invokeChaincodeResponse();

		InvokeChaincode invoke = new InvokeChaincode();
		String response = invoke.invokeChaincode();
		invokeResponse.setMessage(response);

		if (response == "Invoked Chaincode Successfully") {
			invokeResponse.setStatus("success");
			invokeResponse.setTransactionId(invoke.getTransactionId());

			return invokeResponse;
		} else {
			invokeResponse.setStatus("failed");
			invokeResponse.setTransactionId(null);
			return invokeResponse;
		}

	}

	@RequestMapping(value = "/api/invokeChaincodeCollectionInfo", method = RequestMethod.POST)
	public invokeChaincodeResponse invokeChaincodeCollectionInfo() throws Exception {

		invokeChaincodeResponse invokeResponse = new invokeChaincodeResponse();

		InvokeChaincode invoke = new InvokeChaincode();
		String response = invoke.invokeChaincodeCollectionInfo();
		invokeResponse.setMessage(response);

		if (response == "Invoked Chaincode Successfully") {
			invokeResponse.setStatus("success");
			invokeResponse.setTransactionId(invoke.getTransactionId());

			return invokeResponse;
		} else {
			invokeResponse.setStatus("failed");
			invokeResponse.setTransactionId(null);
			return invokeResponse;
		}

	}

	/**
	 * Queries the Chaincode
	 * 
	 * @returns the Chaincode Query status
	 */
	@RequestMapping(value = "/api/queryChaincode", method = RequestMethod.GET)
	public queryResponse queryChaincode(@RequestParam String name) throws Exception {

		QueryChaincode query = new QueryChaincode();
		String response = query.queryChaincode(name);

		Gson gson = new Gson();
		queryResponse responseObject = gson.fromJson(query.getStringResponse(), queryResponse.class);

		return responseObject;

	}

	/**
	 * Queries the Chaincode
	 * 
	 * @returns the Chaincode Query status
	 */
	@RequestMapping(value = "/api/queryChaincodeAllData", method = RequestMethod.GET, produces = "application/json")
	public String queryChaincodeAllData() throws Exception {

		QueryChaincode query = new QueryChaincode();
		query.queryChaincodeAllData();

		return query.getStringResponse();

	}

	/**
	 * 
	 * @param file
	 * @return payload returned from the chaincode
	 * @throws IOException
	 */
	@RequestMapping(value = "/api/uploadFile", method = RequestMethod.POST)
	public ipfsFileUploadResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

		IpfsFileOperations fileUpload = new IpfsFileOperations();
		String hash = fileUpload.ipfsFileUpload(file);

		ipfsFileUploadResponse response = new ipfsFileUploadResponse();
		response.setFileHash(hash);
		response.setTransactionId(fileUpload.getTransactionId());
		response.setFileName(fileUpload.getFileName());
		return response;

	}

	/**
	 * 
	 * @param hash
	 * @return
	 * @return payload returned from the ipfs
	 */
	@RequestMapping(value = "/api/downloadFile", method = RequestMethod.GET)
	public ipfsFileDownloadResponse downloadFile(@RequestParam String hash) throws IOException {

		IpfsFileOperations fileDownload = new IpfsFileOperations();
		byte[] byteStream = fileDownload.ipfsFileDownload(hash);

		ipfsFileDownloadResponse response = new ipfsFileDownloadResponse();
		response.setByteStream(byteStream);
		return response;

	}

	@RequestMapping(value = "/api/GetAllRecords", method = RequestMethod.GET)
	public String GetDetails() throws Exception {
		{

			final String uri = "http://localhost:5984/channel_records/_all_docs?include_docs=true";
			Gson gson = new Gson();

			RestTemplate restTemplate = new RestTemplate();
			String result = restTemplate.getForObject(uri, String.class);

			JsonObject convertedObject = new Gson().fromJson(result, JsonObject.class);
			JsonArray a = convertedObject.get("rows").getAsJsonArray();

			String bankJson = gson.toJson(a);

			return bankJson;
		}

	}
}
