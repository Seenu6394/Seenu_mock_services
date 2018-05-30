package com.scs.service;

import javax.servlet.http.HttpSession;

import com.scs.entity.model.CustomerDetails;
import com.scs.exception.ApiException;
import com.scs.model.BaseRequestModel;

public interface BaseService {

	public Object authenticateMobileUser(BaseRequestModel baseModel, HttpSession session, CustomerDetails customer) throws ApiException;

	public Object getAccounts(BaseRequestModel baseModel, HttpSession session) throws ApiException;
	
	public Object validateTransfer(BaseRequestModel baseModel, HttpSession session) throws ApiException;
	
	public Object sewaOutstanding(BaseRequestModel baseModel, HttpSession session) throws ApiException;

	public Object sewaPayment(BaseRequestModel baseModel, HttpSession session) throws ApiException;
	
	public Object nolBalance(BaseRequestModel baseModel, HttpSession session) throws ApiException;

	public Object nolPayment(BaseRequestModel baseModel, HttpSession session) throws ApiException;
	
	public Object salikBalance(BaseRequestModel baseModel, HttpSession session) throws ApiException;

	public Object salikPayment(BaseRequestModel baseModel, HttpSession session) throws ApiException;
	
	public Object transfer(HttpSession session) throws ApiException;
}
