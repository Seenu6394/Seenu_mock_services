package com.scs.util;

public enum ApiServiceType {
	
	GENERAL("general"),
	ACCOUNTS("get accounts service"),
	LOGIN("mobile login service"),
	SEWA_INQIRY("SEWA_INQIRY"),
	SEWA_PAYMENT("SEWA_PAYMENT"),
	NOL_INQUIRY("NOL_INQUIRY"),
	NOL_PAYMENT("NOL_PAYMENT"),
	SALIK_INQUIRY("SALIK_INQUIRY"),
	SALIK_PAYMENT("SALIK_PAYMENT");
	private String serviceName;
	
	private ApiServiceType(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
}

