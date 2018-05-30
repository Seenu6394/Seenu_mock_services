package com.scs.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scs.exception.ApiException;
import com.scs.exception.ServiceException;
import com.scs.rest.model.ApiServiceResponse;
import com.scs.rest.model.ApiServicesRequest;
import com.scs.service.MobileRestService;
import com.scs.util.ApiConstants;
import com.scs.util.DateUtil;
import com.scs.util.ErrorConstants;
import com.scs.util.SessionUtil;
import com.scs.util.StorageConstants;
import com.scs.util.Utility;

@Service("mobileRestService")
public class MobileRestServiceImpl implements MobileRestService {

	private static final Logger LOGGER = Logger.getLogger(MobileRestServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	
	@Autowired(required = false)
	private MessageSource messageSource;
	
	
	@Autowired
	private ObjectMapper mapper;

	@Override
	public ApiServiceResponse getAPIResponse(ApiServicesRequest enbdServiceRequest, HttpSession session) throws ApiException {
		JsonNode response;

		try {
			HttpEntity<?> entity;
			ResponseEntity<JsonNode> serviceResponse;
			entity = new HttpEntity<>(createHttpHeaders(session));
			LOGGER.info("Headers : " + createHttpHeaders(session));
			LOGGER.info("Service request " + Utility.logAPIRequest(enbdServiceRequest));
			LOGGER.info("Request TO Mobile SERVICE Start: " + enbdServiceRequest.getServiceUrl() + ":: Start Time=" + DateUtil.getCurrentDateTime());
			serviceResponse = restTemplate.exchange(enbdServiceRequest.getServiceUrl(), HttpMethod.GET, entity,JsonNode.class);

			LOGGER.info("Request TO Mobile SERVICE End: " +  enbdServiceRequest.getServiceUrl() + ":: End Time=" 	+ DateUtil.getCurrentDateTime());
			LOGGER.info("RESPONSE FROM Mobile SERVICE " + Utility.logAPIResponse(serviceResponse, mapper));

			if (serviceResponse.getStatusCode().equals(HttpStatus.OK)) {
				response = serviceResponse.getBody();
				checkErrorResponse(response);
			
				if (!SessionUtil.objectExists(session, StorageConstants.SESSION_COOKIE)) {
					SessionUtil.setValue(session, StorageConstants.SESSION_COOKIE,extractSessionId(serviceResponse));
				}
				return new ApiServiceResponse(response);
				

			} else {
				throw new ServiceException("Service failed with status : " + serviceResponse.getStatusCodeValue());
			}

		} catch (RestClientException e) {

			LOGGER.error("ERROR FROM REST SERVICE " + Utility.getExceptionMessage(e));
			throw new ApiException(ErrorConstants.SERVICEFAILURE, messageSource);
		} catch (ApiException ex) {
			LOGGER.error("ApiException in MobileRestServiceImp",ex);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (JsonProcessingException e) {
			LOGGER.error("JsonProcessingException in MobileRestServiceImp",e);
			throw new ApiException("INVALIDREQDATA", e.getMessage());
		} catch (ServiceException e) {
			LOGGER.error("ServiceException in MobileRestServiceImp",e);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, e.getErrMessage());
		} catch (Exception ex) {
			LOGGER.error("Exception in MobileRestServiceImp",ex);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, ex.getMessage());
		}
		
	
	}
	
	
	HttpHeaders createHttpHeaders(HttpSession session) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		if (SessionUtil.objectExists(session, StorageConstants.SESSION_COOKIE)) {
			headers.add("Cookie", "JSESSIONID=" + SessionUtil.getValue(session, StorageConstants.SESSION_COOKIE));
		}
		return headers;
	}
	 public String extractSessionId(ResponseEntity<JsonNode>  response) throws IOException {
         List<String> cookies = response.getHeaders().get("Cookie");
         if (cookies == null) {
             cookies = response.getHeaders().get("Set-Cookie");
         }
         String cookie = cookies.get(cookies.size() - 1);
         int start = cookie.indexOf('=');
         int end = cookie.indexOf(';');

         return cookie.substring(start + 1, end);
     }
	
	
	private void checkErrorResponse(JsonNode response) throws ApiException {
		if (response.has("ERROR")) {
			JsonNode node = response.get("ERROR");
			String errorMessage = node.has("ERROR_MESSAGE") ? node.get("ERROR_MESSAGE").textValue() : "";
			String errorDescription = node.has("ERROR_DESCRIPTION") ? node.get("ERROR_DESCRIPTION").textValue() : "";

			throw new ApiException(errorMessage, errorDescription);
		}
	}
	
	private String getServiceRequest(ApiServicesRequest enbdServiceRequest) throws ApiException {
		try {
			String filename = enbdServiceRequest.getServiceType();
			File file = new File(ApiConstants.SERVICES_PATH + "/" + filename + ".txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	private String getMockResponse(ApiServicesRequest enbdServiceRequest) throws ApiException {
		try {
			String filename = enbdServiceRequest.getServiceType();
			File file = new File(ApiConstants.SERVICES_PATH + "/" + filename + ".txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	

	private JsonNode readMockResponse(HttpSession session, ApiServicesRequest enbdServiceRequest, ObjectMapper mapper) throws ApiException {
		try {
			String filename = enbdServiceRequest.getServiceType();
			File file = new File(SessionUtil.getValue(session, StorageConstants.MOCK_PATH) + "/" + filename + ".txt");
			String string = FileUtils.readFileToString(file);
			return mapper.readTree(string);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}

	private void saveMockResponse(HttpSession session, ApiServicesRequest enbdServiceRequest, JsonNode response,ObjectMapper mapper) {
		{
			try {
				String filename = enbdServiceRequest.getServiceType();

				FileUtils.writeStringToFile(new File(SessionUtil.getValue(session, StorageConstants.MOCK_PATH) + "/" + filename + ".txt"),
						mapper.writeValueAsString(response));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	

	
}
