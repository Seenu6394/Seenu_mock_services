package com.scs.service;

import com.scs.exception.ApiException;
import com.scs.model.BaseRequestModel;

public interface DBServices {

	public Object getCustomerDetails(BaseRequestModel baseModel) throws ApiException;
	
	public Object getServerDetails(BaseRequestModel baseModel) throws ApiException;
	
}
