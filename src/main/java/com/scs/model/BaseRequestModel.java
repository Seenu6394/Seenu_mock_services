package com.scs.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scs.validation.ValidationGroups.AuthenticateUser;
import com.scs.validation.ValidationGroups.CheckBalance;
import com.scs.validation.ValidationGroups.SewaOutStanding;
import com.scs.validation.ValidationGroups.SewaPayment;
import com.scs.validation.ValidationGroups.TransferValidate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseRequestModel {

	@NotNull(groups = { AuthenticateUser.class, TransferValidate.class, CheckBalance.class, SewaOutStanding.class,
			SewaPayment.class })
	@NotEmpty(groups = { AuthenticateUser.class })
	private String authCode;

	private TransfersRequestModel transfer;
	private Sewa sewa;

	private Nol nol;

	private Salik salik;
	
	private String accountNumber;
	
	private Weather weather;


	public TransfersRequestModel getTransfer() {
		return transfer;
	}

	public void setTransfer(TransfersRequestModel transfer) {
		this.transfer = transfer;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public Sewa getSewa() {
		return sewa;
	}

	public void setSewa(Sewa sewa) {
		this.sewa = sewa;
	}

	public Nol getNol() {
		return nol;
	}

	public void setNol(Nol nol) {
		this.nol = nol;
	}

	public Salik getSalik() {
		return salik;
	}

	public void setSalik(Salik salik) {
		this.salik = salik;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Weather getWeather() {
		return weather;
	}

	public void setWeather(Weather weather) {
		this.weather = weather;
	}

}