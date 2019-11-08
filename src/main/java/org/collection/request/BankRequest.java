package org.collection.request;

public class BankRequest {
	private String bankName;
	private String bankAddress;
	private String loanType;
	private Integer bucket;

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public Integer getBucket() {
		return bucket;
	}

	public void setBucket(Integer bucket) {
		this.bucket = bucket;
	}
}
