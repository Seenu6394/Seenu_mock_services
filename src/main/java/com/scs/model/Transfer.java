package com.scs.model;

public class Transfer {

	private String jsonRef;
	private String confirmationNumber;
	private String trackingId;
	private String transId;

	public String getJsonRef() {
		return jsonRef;
	}

	public void setJsonRef(String jsonRef) {
		this.jsonRef = jsonRef;
	}

	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	public void setConfirmationNumber(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}
}
