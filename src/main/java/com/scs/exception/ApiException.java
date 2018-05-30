package com.scs.exception;

import org.springframework.context.MessageSource;

import com.scs.util.Utility;

public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;
	private String errorCode;
	private String errorMessage;

	public ApiException(String errCode, MessageSource messageSource) {
		this.setErrorCode(errCode);
		this.errorMessage = Utility.getMessageByLocale(errCode, messageSource);
	}

	public ApiException(String errCode, String errMessage) {
		this.setErrorCode(errCode);
		this.setErrorMessage(errMessage);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
