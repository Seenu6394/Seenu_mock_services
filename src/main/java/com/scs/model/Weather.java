
package com.scs.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scs.validation.ValidationGroups.NolBalance;
import com.scs.validation.ValidationGroups.NolPayment;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Weather {

	private String lang;
	
	private String accessCode;

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

}
