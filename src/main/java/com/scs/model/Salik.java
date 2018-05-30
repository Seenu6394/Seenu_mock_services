package com.scs.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scs.validation.ValidationGroups.SalikBalance;
import com.scs.validation.ValidationGroups.SalikPayment;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Salik {

	@NotNull(groups = { SalikBalance.class, SalikPayment.class })
	private String number;
	@NotNull(groups = { SalikPayment.class })
	private String amount;

	@NotNull(groups = { SalikPayment.class })
	private String pin;
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

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

}
