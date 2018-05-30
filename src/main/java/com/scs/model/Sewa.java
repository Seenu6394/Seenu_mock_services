package com.scs.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scs.validation.ValidationGroups.SewaOutStanding;
import com.scs.validation.ValidationGroups.SewaPayment;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Sewa {

	@NotNull(groups = { SewaOutStanding.class, SewaPayment.class })
	private String number;
	@NotNull(groups = { SewaPayment.class })
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
