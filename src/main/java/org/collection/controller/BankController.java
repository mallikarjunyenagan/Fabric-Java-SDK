package org.collection.controller;

import org.collection.request.BankRequest;
import org.collection.request.BankUpdateRequest;
import org.collection.response.postBankDetailsResponse;
import org.collection.response.updateBankDetailsResponse;
import org.collection.service.BankService;
import org.springframework.web.bind.annotation.*;

/**
 * 
 *
 *
 */

@RestController
@RequestMapping("api/bank")
public class BankController {

	/**
	 * Posting Bank Details to CouchDb
	 * 
	 * @returns the data insertion status
	 */
	@PostMapping(value = "postObject",consumes = {"application/json" , "application/xml"})
	public postBankDetailsResponse postBankDetails(@RequestBody Object object) throws Exception {

		String jsonString = new com.google.gson.Gson().toJson(object);

		postBankDetailsResponse response = new postBankDetailsResponse();
		BankService bankService = new BankService();

		String message = bankService.postObject(jsonString);
		String transactionId = bankService.getTransactionId();

		response.setMessage(message);
		response.setTransactionId(transactionId);
		return response;
	}



	/**
	 * Get All Bank Details from CouchDb
	 * 
	 * @returns All Bank Details from CouchDb
	 */
	@GetMapping("/getAllData")
	public String getAllBankDetails() throws Exception {
		BankService bankService = new BankService();
		return bankService.getAllData();

	}

	/**
	 * Get History of a record from ledger
	 * 
	 * @returns History of a record from ledger
	 */
	@GetMapping(value = "/getRecordHistory")
	public String getRecordHistory(@RequestParam String key) throws Exception {

		BankService bankService = new BankService();
		return bankService.getHistory(key);

	}

	/**
	 * Update Bank Details in CouchDb
	 * 
	 * @returns the data updation status
	 */
	@PostMapping(value = "/updateObject",consumes = {"application/json" , "application/xml"})
	public updateBankDetailsResponse updateObject(@RequestParam(value = "objectKeyOrId") String objectKeyOrId,@RequestBody Object object) throws Exception {

		updateBankDetailsResponse response = new updateBankDetailsResponse();
		BankService bankService = new BankService();
		String jsonString = new com.google.gson.Gson().toJson(object);
		String message = bankService.updateObjectByKey(objectKeyOrId,jsonString);
		String transactionId = bankService.getTransactionId();

		response.setMessage(message);
		response.setTransactionId(transactionId);
		return response;
	}
}
