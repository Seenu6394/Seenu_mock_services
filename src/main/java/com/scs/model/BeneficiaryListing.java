package com.scs.model;

public class BeneficiaryListing {

	private String accountNumber;
	private String status;
	private String name;
	private String productId;
	private String id;

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return name;
	}

	public String getProductId() {
		return productId;
	}

	public String getId() {
		return id;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public void setId(String id) {
		this.id = id;
	}
}
