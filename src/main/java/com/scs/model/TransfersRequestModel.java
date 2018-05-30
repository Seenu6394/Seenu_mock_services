package com.scs.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scs.validation.ValidationGroups.TransferValidate;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TransfersRequestModel {

	@NotNull(groups = { TransferValidate.class })
	private Double amount;

	@NotNull(groups = { TransferValidate.class })
	@NotEmpty(groups = { TransferValidate.class })
	private String accountId;

	@NotNull(groups = { TransferValidate.class })
	@NotEmpty(groups = { TransferValidate.class })
	private String description;

	@NotNull(groups = { TransferValidate.class })
	@NotEmpty(groups = { TransferValidate.class })
	private String mobileNo;

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

}
