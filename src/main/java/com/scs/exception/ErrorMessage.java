package com.scs.exception;

public class ErrorMessage {

	public String errorMessage;
	public String errorCode;
	
	
	public ErrorMessage(String code,String message){
		this.errorCode = code;
		this.errorMessage = message;
		
	}
}
