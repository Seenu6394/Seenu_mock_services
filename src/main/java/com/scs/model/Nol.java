package com.scs.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scs.validation.ValidationGroups.NolBalance;
import com.scs.validation.ValidationGroups.NolPayment;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Nol {

	@NotNull(groups = { NolBalance.class, NolPayment.class })
	private String number;
	@NotNull(groups = { NolPayment.class })
	private String amount;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

}
