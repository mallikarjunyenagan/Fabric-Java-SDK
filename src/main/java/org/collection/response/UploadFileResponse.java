package org.collection.response;

public class UploadFileResponse {
    private String fileName;

    private String fileType;
    private long size;
    private String hashKey;
    private String transactionId;
    public UploadFileResponse(String fileName, String fileType, long size, String hashKey, String transactionId) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
        this.hashKey=hashKey;
        this.transactionId=transactionId;
    }
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getHashKey() {
		return hashKey;
	}
	public void setHashKey(String hashKey) {
		this.hashKey = hashKey;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

    
	// Getters and Setters (Omitted for brevity)
    
}