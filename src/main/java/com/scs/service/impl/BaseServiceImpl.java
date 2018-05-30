package com.scs.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.scs.entity.model.CustomerDetails;
import com.scs.exception.ApiException;
import com.scs.model.BaseRequestModel;
import com.scs.model.Customer;
import com.scs.model.Nol;
import com.scs.model.Salik;
import com.scs.model.Sewa;
import com.scs.model.BeneficiaryListing;
import com.scs.model.Account;
import com.scs.model.TransfersRequestModel;
import com.scs.rest.model.ApiServiceResponse;
import com.scs.rest.model.ApiServicesRequest;
import com.scs.service.BaseService;
import com.scs.service.MobileRestService;
import com.scs.util.ApiServiceType;
import com.scs.util.ErrorConstants;
import com.scs.util.SessionUtil;
import com.scs.util.StorageConstants;
import com.scs.util.Utility;

@Service("baseService")

public class BaseServiceImpl implements BaseService {

	private static final Logger logger = Logger.getLogger(BaseServiceImpl.class);
	@Autowired
	private MobileRestService mobileRestService;

	@Autowired(required = false)
	private MessageSource messageSource;

	@Value("${enbd.service.rest.login}") private String loginUrl;
	@Value("${enbd.service.rest.accounts}") private String accountsUrl;
	@Value("${enbd.service.rest.validateTransfer}")	private String validateTransferUrl;
	@Value("${enbd.service.rest.transfer}") private String transferUrl;
	@Value("${enbd.service.rest.sewaBenefList}") private String sewaBenfLstUrl;
	@Value("${enbd.service.rest.sewaCheckBalance}") private String sewaCheckBalancetUrl;
	@Value("${enbd.service.rest.sewaPayment}") private String sewaPaymentUrl;
	
	@Value("${enbd.service.rest.nolBenefList}") private String nolBenfLstUrl;
	@Value("${enbd.service.rest.nolCheckBalance}") private String nolCheckBalancetUrl;
	@Value("${enbd.service.rest.nolPayment}") private String nolPaymentUrl;
	@Value("${enbd.service.rest.nolEntityListing}") private String nolEntityLstUrl;
	
	@Value("${enbd.service.rest.salikBenefList}") private String salikBenfLstUrl;
	@Value("${enbd.service.rest.salikCheckBalance}") private String salikCheckBalancetUrl;
	@Value("${enbd.service.rest.salikPayment}") private String salikPaymentUrl;
	@Value("${enbd.service.rest.salikEntityListing}") private String salikEntityLstUrl;


	@Override
	public Object authenticateMobileUser(BaseRequestModel baseModel, HttpSession session, CustomerDetails customerDao)
			throws ApiException {

		Customer customer = null;
		try {

			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.LOGIN);

			validateUserReq.setServiceUrl(loginUrl + "&uid=" + customerDao.getUserId());
			ApiServiceResponse responseNode = (ApiServiceResponse) mobileRestService.getAPIResponse(validateUserReq,
					session);
			customer = new Customer();
			customer.setEmail(responseNode.getObject("DATA").get("customerdetails").get("EMAIL").textValue());
			customer.setName(responseNode.getObject("DATA").get("customerdetails").get("CUSTFIRSTNAME").textValue());

		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.authenticateUser END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error("+++++ BaseServiceImpl.authenticateUser END SERVICE WITH EXCEPTION +++++");
			logger.error(Utility.getExceptionMessage(ex));
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return customer;
	}

	@Override
	public Object getAccounts(BaseRequestModel baseModel, HttpSession session) throws ApiException {

		List<Account> accountLst = null;
		try {

			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.ACCOUNTS);

			validateUserReq.setServiceUrl(accountsUrl);
			ApiServiceResponse responseNode = (ApiServiceResponse) mobileRestService.getAPIResponse(validateUserReq,
					session);

			accountLst = new ArrayList<>();
			Account account;
			for (JsonNode node : responseNode.getObject("DATA")) {

				account = new Account();
				account.setBalance(Double.parseDouble(node.get("BALANCE").textValue()));
				account.setAccountNumber(node.get("ACCOUNT_NUMBER").textValue());
				account.setAccountId(node.get("PRODUCTID").textValue());
				account.setAccountName(node.get("ACCOUNT_TYPE").textValue());
				accountLst.add(account);
			}

			logger.debug("+++++ BaseServiceImpl.getAccounts END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.getAccounts END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error("+++++ BaseServiceImpl.getAccounts END SERVICE WITH EXCEPTION +++++");
			logger.error(Utility.getExceptionMessage(ex));
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return accountLst;
	}

	@Override
	public Object validateTransfer(BaseRequestModel baseModel, HttpSession session) throws ApiException {
		TransfersRequestModel transfer = null;
		try {
			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.LOGIN);

			Account account = SessionUtil.getValue(session, StorageConstants.FAVOURITE_ACCOUNT);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(validateTransferUrl);
			builder.replaceQueryParam("amount", baseModel.getTransfer().getAmount().toString());
			builder.replaceQueryParam("mobileno", baseModel.getTransfer().getMobileNo());
			validateUserReq.setServiceUrl(builder.toUriString() + "&accproductid=" + account.getAccountId());

			mobileRestService.getAPIResponse(validateUserReq, session);

			transfer = new TransfersRequestModel();

			transfer.setAmount(baseModel.getTransfer().getAmount());
			transfer.setMobileNo(baseModel.getTransfer().getMobileNo());

			logger.debug("+++++ BaseServiceImpl.validateTransfer END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return transfer;
	}

	@Override
	public Object transfer(HttpSession session) throws ApiException {
		try {

			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.ACCOUNTS);

			validateUserReq.setServiceUrl(transferUrl);
			mobileRestService.getAPIResponse(validateUserReq, session);

			logger.debug("+++++ BaseServiceImpl.transfer END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.transfer END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error("+++++ BaseServiceImpl.transfer END SERVICE WITH EXCEPTION +++++");
			logger.error(Utility.getExceptionMessage(ex));
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return null;
	}

	@Override
	public Object sewaOutstanding(BaseRequestModel baseModel, HttpSession session) throws ApiException {
		Sewa sewa = new Sewa();
		try {
			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.SEWA_INQIRY);
			String benefProductId = getSewaBenfProdId(baseModel, session);
			SessionUtil.setValue(session, StorageConstants.PRODUCT_ID, benefProductId);

			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(sewaCheckBalancetUrl);
			validateUserReq.setServiceUrl(builder.toUriString() + "&benfproductid=" + benefProductId);

			ApiServiceResponse responseNode = (ApiServiceResponse) mobileRestService.getAPIResponse(validateUserReq,
					session);
			sewa.setAmount(responseNode.getObject("DATA").get("AMOUNT").textValue());

			logger.debug("+++++ BaseServiceImpl.validateTransfer END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return sewa;
	}

	@Override
	public Object sewaPayment(BaseRequestModel baseModel, HttpSession session) throws ApiException {
		Sewa sewa = null;
		try {
			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.LOGIN);

			Account account = SessionUtil.getValue(session, StorageConstants.FAVOURITE_ACCOUNT);
			String productId = SessionUtil.getValue(session, StorageConstants.PRODUCT_ID);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(sewaPaymentUrl);
			builder.replaceQueryParam("amount", baseModel.getSewa().getAmount().toString());
			validateUserReq.setServiceUrl(builder.toUriString() + "&accproductid=" + account.getAccountId() + "&benfproductid="+productId);

			mobileRestService.getAPIResponse(validateUserReq, session);

			sewa = new Sewa();

			sewa.setAmount(baseModel.getSewa().getAmount());
			sewa.setNumber(baseModel.getSewa().getNumber());

			logger.debug("+++++ BaseServiceImpl.validateTransfer END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return sewa;
	}

	private List<BeneficiaryListing> sewaBeneficiaryListing(BaseRequestModel baseModel, HttpSession session)
			throws ApiException {
		List<BeneficiaryListing> benfLst = null;
		try {

			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.ACCOUNTS);

			validateUserReq.setServiceUrl(sewaBenfLstUrl);
			ApiServiceResponse responseNode = (ApiServiceResponse) mobileRestService.getAPIResponse(validateUserReq,
					session);

			benfLst = new ArrayList<>();
			BeneficiaryListing benef;
			for (JsonNode node : responseNode.getObject("DATA")) {

				benef = new BeneficiaryListing();
				benef.setAccountNumber(node.get("BENFACCNO").textValue());
				benef.setId(node.get("BENFID").textValue());
				benef.setName(node.get("BENFNAME").textValue());
				benef.setProductId(node.get("PRODUCTID").textValue());
				benef.setStatus(node.get("BENSTATUS").textValue());
				benfLst.add(benef);
			}

			logger.debug("+++++ BaseServiceImpl.sewaBeneficiaryListing END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.sewaBeneficiaryListing END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error("+++++ BaseServiceImpl.sewaBeneficiaryListing END SERVICE WITH EXCEPTION +++++");
			logger.error(Utility.getExceptionMessage(ex));
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return benfLst;
	}
	
	private String getSewaBenfProdId(BaseRequestModel baseModel, HttpSession session) throws ApiException{
		
		String benefProductId = null;
		List<BeneficiaryListing> benefLst = sewaBeneficiaryListing(baseModel, session);
		for (BeneficiaryListing benefObj : benefLst) {
			if (!benefObj.getAccountNumber().equals(baseModel.getSewa().getNumber())) {
				// throw new ApiException(ErrorConstants.INVALID_SEWA_NUMBER, messageSource);
			} else {
				benefProductId = benefObj.getProductId();
				break;
			}
		}
		if (benefProductId == null) {
			throw new ApiException(ErrorConstants.INVALID_SEWA_NUMBER, messageSource);
		}
		
		return benefProductId;
	}

	@Override
	public Object nolBalance(BaseRequestModel baseModel, HttpSession session) throws ApiException {
		Nol nol = new Nol();
		try {
			
			
			
			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.NOL_INQUIRY);
			
			// pre-req calling accountListing is mandatory
			validateUserReq.setServiceUrl(nolEntityLstUrl);

			ApiServiceResponse responseNode = (ApiServiceResponse) mobileRestService.getAPIResponse(validateUserReq, session);

			// --------------------------------------------
			
			String benefProductId = getNolBenfProdId(baseModel, session);
			SessionUtil.setValue(session, StorageConstants.PRODUCT_ID, benefProductId);

			
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(nolCheckBalancetUrl);
			validateUserReq.setServiceUrl(builder.toUriString() + "&benfproductid=" + benefProductId);

			 responseNode = (ApiServiceResponse) mobileRestService.getAPIResponse(validateUserReq,
					session);
			nol.setAmount(responseNode.getObject("DATA").get("AVAILABLEBALANCE").textValue());

			logger.debug("+++++ BaseServiceImpl.validateTransfer END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return nol;
	}

	@Override
	public Object nolPayment(BaseRequestModel baseModel, HttpSession session) throws ApiException {
		Nol nol = null;
		try {
			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.GENERAL);

			Account account = SessionUtil.getValue(session, StorageConstants.FAVOURITE_ACCOUNT);
			String productId = SessionUtil.getValue(session, StorageConstants.PRODUCT_ID);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(nolPaymentUrl);
			builder.replaceQueryParam("amount", baseModel.getNol().getAmount().toString());
			validateUserReq.setServiceUrl(builder.toUriString() + "&accproductid=" + account.getAccountId() + "&benfproductid="+productId);

			mobileRestService.getAPIResponse(validateUserReq, session);

			nol = new Nol();

			nol.setAmount(baseModel.getNol().getAmount());
			nol.setNumber(baseModel.getNol().getNumber());

			logger.debug("+++++ BaseServiceImpl.validateTransfer END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return nol;
	}
	
	private String getNolBenfProdId(BaseRequestModel baseModel, HttpSession session) throws ApiException {

		String benefProductId = null;
		List<BeneficiaryListing> benefLst = nolBenificiaryListing(baseModel, session);
		for (BeneficiaryListing benefObj : benefLst) {
			if (!benefObj.getAccountNumber().equals(baseModel.getNol().getNumber())) {
				// throw new ApiException(ErrorConstants.INVALID_SEWA_NUMBER,
				// messageSource);
			} else {
				benefProductId = benefObj.getProductId();
				break;
			}
		}
		if (benefProductId == null) {
			throw new ApiException(ErrorConstants.INVALID_SEWA_NUMBER, messageSource);
		}

		return benefProductId;
	}
	
	private List<BeneficiaryListing> nolBenificiaryListing(BaseRequestModel baseModel, HttpSession session)
			throws ApiException {
		List<BeneficiaryListing> benfLst = null;
		try {

			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.GENERAL);

			validateUserReq.setServiceUrl(nolBenfLstUrl);
			ApiServiceResponse responseNode = (ApiServiceResponse) mobileRestService.getAPIResponse(validateUserReq,
					session);

			benfLst = new ArrayList<>();
			BeneficiaryListing benef;
			for (JsonNode node : responseNode.getObject("DATA")) {

				benef = new BeneficiaryListing();
				benef.setAccountNumber(node.get("BENFACCNO").textValue());
				benef.setId(node.get("BENFID").textValue());
				benef.setName(node.get("BENFNAME").textValue());
				benef.setProductId(node.get("PRODUCTID").textValue());
				benef.setStatus(node.get("BENSTATUS").textValue());
				benfLst.add(benef);
			}

			logger.debug("+++++ BaseServiceImpl.nolBeneficiaryListing END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.nolBeneficiaryListing END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error("+++++ BaseServiceImpl.nolBeneficiaryListing END SERVICE WITH EXCEPTION +++++");
			logger.error(Utility.getExceptionMessage(ex));
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return benfLst;
	}

	@Override
	public Object salikBalance(BaseRequestModel baseModel, HttpSession session) throws ApiException {
		Salik salik = new Salik();
		try {
			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.SALIK_INQUIRY);
			
			// pre-req calling accountListing is mandatory
			validateUserReq.setServiceUrl(salikEntityLstUrl);

			ApiServiceResponse responseNode = (ApiServiceResponse) mobileRestService.getAPIResponse(validateUserReq, session);

			// --------------------------------------------
			
			String benefProductId = getSalikBenfProdId(baseModel, session);
			SessionUtil.setValue(session, StorageConstants.PRODUCT_ID, benefProductId);

			
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(salikCheckBalancetUrl);
			validateUserReq.setServiceUrl(builder.toUriString() + "&benfproductid=" + benefProductId);

			 responseNode = (ApiServiceResponse) mobileRestService.getAPIResponse(validateUserReq,
					session);
			salik.setAmount(responseNode.getObject("DATA").get("AMOUNT").textValue());

			logger.debug("+++++ BaseServiceImpl.validateTransfer END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return salik;
	}

	@Override
	public Object salikPayment(BaseRequestModel baseModel, HttpSession session) throws ApiException {
		Salik salik = null;
		try {
			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.GENERAL);

			Account account = SessionUtil.getValue(session, StorageConstants.FAVOURITE_ACCOUNT);
			String productId = SessionUtil.getValue(session, StorageConstants.PRODUCT_ID);
			UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(salikPaymentUrl);
			builder.replaceQueryParam("amount", baseModel.getSalik().getAmount().toString());
			validateUserReq.setServiceUrl(builder.toUriString() + "&accproductid=" + account.getAccountId() + "&benfproductid="+productId);

			mobileRestService.getAPIResponse(validateUserReq, session);

			salik = new Salik();

			salik.setAmount(baseModel.getSalik().getAmount());
			salik.setNumber(baseModel.getSalik().getNumber());
			salik.setPin(baseModel.getSalik().getPin());
			logger.debug("+++++ BaseServiceImpl.validateTransfer END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error("+++++ BaseServiceImpl.validateTransfer END SERVICE WITH EXCEPTION +++++");
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return salik;
	}
	
	
	private String getSalikBenfProdId(BaseRequestModel baseModel, HttpSession session) throws ApiException {

		String benefProductId = null;
		List<BeneficiaryListing> benefLst = getBenificiaryListing(baseModel, session,salikBenfLstUrl);
		for (BeneficiaryListing benefObj : benefLst) {
			if (!benefObj.getAccountNumber().equals(baseModel.getSalik().getNumber())) {
				// throw new ApiException(ErrorConstants.INVALID_SEWA_NUMBER,
				// messageSource);
			} else {
				benefProductId = benefObj.getProductId();
				break;
			}
		}
		if (benefProductId == null) {
			throw new ApiException(ErrorConstants.INVALID_SALIK_NUMBER, messageSource);
		}

		return benefProductId;
	}
	
	private List<BeneficiaryListing> getBenificiaryListing(BaseRequestModel baseModel, HttpSession session, String serviceUrl)
			throws ApiException {
		List<BeneficiaryListing> benfLst = null;
		try {

			ApiServicesRequest validateUserReq = new ApiServicesRequest(ApiServiceType.GENERAL);

			validateUserReq.setServiceUrl(serviceUrl);
			ApiServiceResponse responseNode = (ApiServiceResponse) mobileRestService.getAPIResponse(validateUserReq,
					session);

			benfLst = new ArrayList<>();
			BeneficiaryListing benef;
			for (JsonNode node : responseNode.getObject("DATA")) {

				benef = new BeneficiaryListing();
				benef.setAccountNumber(node.get("BENFACCNO").textValue());
				benef.setId(node.get("BENFID").textValue());
				benef.setName(node.get("BENFNAME").textValue());
				benef.setProductId(node.get("PRODUCTID").textValue());
				benef.setStatus(node.get("BENSTATUS").textValue());
				benfLst.add(benef);
			}

			logger.debug("+++++ BaseServiceImpl.nolBeneficiaryListing END SERVICE +++++");
		} catch (ApiException ex) {
			logger.error("+++++ BaseServiceImpl.nolBeneficiaryListing END SERVICE WITH EXCEPTION +++++");
			logger.error(ex.getErrorCode() + " : " + ex.getErrorMessage());
			throw ex;
		} catch (Exception ex) {
			logger.error("+++++ BaseServiceImpl.nolBeneficiaryListing END SERVICE WITH EXCEPTION +++++");
			logger.error(Utility.getExceptionMessage(ex));
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return benfLst;
	}
	
}
