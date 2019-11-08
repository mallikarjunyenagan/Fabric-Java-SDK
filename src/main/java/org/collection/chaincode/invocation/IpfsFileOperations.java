
package org.collection.chaincode.invocation;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.web.multipart.MultipartFile;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

/**
 * 
 * @author Vivek Gani
 *
 */
public class IpfsFileOperations {

	private String fileName;
	private String transactionId;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	// File Upload Method
	public String ipfsFileUpload(MultipartFile file) {
		try {
			// Uploading file to local system
			byte[] bytes = file.getBytes();
			Path path = Paths.get("src/main/resources/files/" + file.getOriginalFilename());
			Files.write(path, bytes);
			String filePathToUpload = "src/main/resources/files/" + file.getOriginalFilename();
			this.fileName = file.getOriginalFilename();

			// Uploading file to IPFS
			IPFS ipfs = new IPFS("/ip4/192.168.1.172/tcp/5001");
			NamedStreamable.FileWrapper files = new NamedStreamable.FileWrapper(new File(filePathToUpload));
			MerkleNode addResult = ipfs.add(files).get(0);
			Multihash pointer = addResult.hash;

			// Adding FileHash to Blockchain
			AddIpfsHash add = new AddIpfsHash();
			this.transactionId = add.invokeChaincode(file.getOriginalFilename(), pointer.toString());
			return pointer.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "Error occured, Please try again";
		}
	}

	// File Download Method
	public byte[] ipfsFileDownload(String hash) {
		try {
			// Download file from Ipfs
			IPFS ipfs = new IPFS("/ip4/192.168.1.172/tcp/5001");

			Multihash filePointer = Multihash.fromBase58(hash);
			byte[] contents = ipfs.cat(filePointer);

			return contents;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}