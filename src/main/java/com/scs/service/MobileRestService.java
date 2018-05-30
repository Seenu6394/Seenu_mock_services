package com.scs.service;

import javax.servlet.http.HttpSession;

import com.scs.exception.ApiException;
import com.scs.rest.model.ApiServicesRequest;

public interface MobileRestService {

	public Object getAPIResponse(ApiServicesRequest enbdServiceRequest, HttpSession session) throws ApiException;


}
