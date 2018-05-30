package com.scs.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scs.entity.model.CustomerDetails;

import com.scs.entity.model.ServerDetails;
import com.scs.exception.ApiException;
import com.scs.model.Account;
import com.scs.model.BaseRequestModel;
import com.scs.model.Customer;
import com.scs.model.Nol;
import com.scs.model.Salik;
import com.scs.model.Sewa;
import com.scs.service.BaseService;
import com.scs.service.DBServices;
import com.scs.util.ApiConstants;
import com.scs.util.ErrorConstants;
import com.scs.util.SessionUtil;
import com.scs.util.StorageConstants;
import com.scs.util.Utility;
import com.scs.validation.ValidationGroups.AuthenticateUser;
import com.scs.validation.ValidationGroups.CheckBalance;
import com.scs.validation.ValidationGroups.NolBalance;
import com.scs.validation.ValidationGroups.NolPayment;
import com.scs.validation.ValidationGroups.SalikBalance;
import com.scs.validation.ValidationGroups.SalikPayment;
import com.scs.validation.ValidationGroups.TransferValidate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

@RestController
@RequestMapping(ApiConstants.API)
public class BaseController {

	private static final Logger logger = Logger.getLogger(BaseController.class);
	private static final String CONTROLLER_END_EXCEPTION = "Base CONTROLLER  ENDED WITH EXCEPTION";

	@Autowired
	private BaseService baseService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private DBServices dbService;

	@Value("${enbd.test.response}")
	private String testUrl;

	List<String> files = new ArrayList<String>();

	@GetMapping(value = ApiConstants.LOGOUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object doLogout(@Valid BaseRequestModel baseModel, BindingResult bindingResult, HttpSession session)
			throws ApiException {
		session.invalidate();
		return ApiConstants.SUCCESS;
	}

	@GetMapping(value = ApiConstants.API_DOCS)
	@ResponseBody
	public ModelAndView init(HttpServletRequest request) {
		logger.debug("**************LOGIN CONTROLLER API - INIT  START*****************");
		ModelAndView model = new ModelAndView();
		model.addObject("title", "SCS 1.0");
		model.addObject("IP", "http://10.10.10.212:7001/");
		model.addObject("context", "SCS");
		model.setViewName(ApiConstants.WELCOME_SCS);
		logger.debug("**************LOGIN CONTROLLER API - INIT  END*******************");
		return model;
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = ApiConstants.GET_CUSTOMER, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object getCustomer(@RequestBody @Validated(AuthenticateUser.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session, HttpServletRequest request) throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();

		try {

			if (bindingResult.hasErrors()) {
				logger.debug(ApiConstants.BINDING_ERRORS);
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}

			logger.debug(Utility.generateHashKey("ELQADI", session.getId()));

			CustomerDetails customerDao = (CustomerDetails) dbService.getCustomerDetails(baseModel);

			Customer customer = (Customer) baseService.authenticateMobileUser(baseModel, session, customerDao);
			List<Account> accounts = (List<Account>) baseService.getAccounts(baseModel, session);
			responseObject.put("customer", customer);

			SessionUtil.setValue(session, StorageConstants.LOGIN_RESPONSE, "loggedIn");
			SessionUtil.setValue(session, StorageConstants.FAVOURITE_ACCOUNT, accounts.get(0));
			SessionUtil.setValue(session, StorageConstants.AUTH_CODE, baseModel.getAuthCode());
		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject;
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = ApiConstants.ACCOUNT_LISTING, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object getUserAccounts(@RequestBody @Validated(AuthenticateUser.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session, HttpServletRequest request) throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();

		try {

			if (bindingResult.hasErrors()) {
				logger.debug(ApiConstants.BINDING_ERRORS);
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}

			Utility.checkAuthCode(baseModel.getAuthCode(), session, messageSource);
			List<Account> accounts = (List<Account>) baseService.getAccounts(baseModel, session);
			responseObject.put("accounts", accounts);

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject;
	}

	@PostMapping(value = ApiConstants.ME_PAY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object transfers(@RequestBody @Validated(TransferValidate.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();

		try {
			if (bindingResult.hasErrors()) {
				logger.debug("++---- ERRORS---++");
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}

			Utility.checkAuthCode(baseModel.getAuthCode(), session, messageSource);
			Object response = baseService.validateTransfer(baseModel, session);
			// confirm transfer is here
			baseService.transfer(session);

			responseObject.put("transfer", response);

			logger.debug("++---- LOGIN CONTROLLER UPDATE USER END ---++");
		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject;
	}

	@SuppressWarnings("unchecked")
	@PostMapping(value = ApiConstants.BALANCE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object balance(@RequestBody @Validated(CheckBalance.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();

		try {

			if (bindingResult.hasErrors()) {
				logger.debug("++---- ERRORS---++");
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}
			Utility.checkAuthCode(baseModel.getAuthCode(), session, messageSource);

			List<Account> accounts = (List<Account>) baseService.getAccounts(baseModel, session);
			// empty check

			if (baseModel.getAccountNumber() == null) {
				responseObject.put("balance", accounts.get(0).getBalance());

			} else {
				Boolean flag = false;
				for (Account account : accounts) {
					if (account.getAccountNumber().equals(baseModel.getAccountNumber())) {
						responseObject.put("balance", account.getBalance());
						flag = true;
						break;
					}
				}

				if (flag == false) {
					throw new ApiException("INVALID_ACCOUNT_NUMBER", "GIVEN ACCOUNT NUMBER IS NOT VALID	");
				}

			}

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject;
	}

	@PostMapping(value = ApiConstants.SEWA_INQUIRY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object sewaOutstanding(@RequestBody @Validated(CheckBalance.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Sewa response;
		try {

			if (bindingResult.hasErrors()) {
				logger.debug("++---- ERRORS---++");
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}
			Utility.checkAuthCode(baseModel.getAuthCode(), session, messageSource);
			response = (Sewa) baseService.sewaOutstanding(baseModel, session);
			responseObject.put("sewa", response);

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject;
	}

	@PostMapping(value = ApiConstants.SEWA_PAY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object sewaPayment(@RequestBody @Validated(TransferValidate.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();

		try {
			if (bindingResult.hasErrors()) {
				logger.debug("++---- ERRORS---++");
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}

			Utility.checkAuthCode(baseModel.getAuthCode(), session, messageSource);
			baseService.sewaOutstanding(baseModel, session);
			Object response = baseService.sewaPayment(baseModel, session);

			responseObject.put("sewa", response);

			logger.debug("++---- LOGIN CONTROLLER UPDATE USER END ---++");
		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject;
	}

	@PostMapping(value = ApiConstants.NOL_INQUIRY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object nolCheck(@RequestBody @Validated(NolBalance.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Nol response;
		try {

			if (bindingResult.hasErrors()) {
				logger.debug("++---- ERRORS---++");
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}
			Utility.checkAuthCode(baseModel.getAuthCode(), session, messageSource);
			response = (Nol) baseService.nolBalance(baseModel, session);
			responseObject.put("nol", response);

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject;
	}

	@PostMapping(value = ApiConstants.NOL_PAY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object nolPay(@RequestBody @Validated(NolPayment.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();

		try {
			if (bindingResult.hasErrors()) {
				logger.debug("++---- ERRORS---++");
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}

			Utility.checkAuthCode(baseModel.getAuthCode(), session, messageSource);
			baseService.nolBalance(baseModel, session);
			Object response = baseService.nolPayment(baseModel, session);

			responseObject.put("nol", response);

			logger.debug("++---- LOGIN CONTROLLER UPDATE USER END ---++");
		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject;
	}

	@PostMapping(value = ApiConstants.SALIK_INQUIRY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object salikBalance(@RequestBody @Validated(SalikBalance.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Salik response;
		try {

			if (bindingResult.hasErrors()) {
				logger.debug("++---- ERRORS---++");
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}
			Utility.checkAuthCode(baseModel.getAuthCode(), session, messageSource);
			response = (Salik) baseService.salikBalance(baseModel, session);
			responseObject.put("salik", response);

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject;
	}

	@PostMapping(value = ApiConstants.SALIK_PAY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object salikPay(@RequestBody @Validated(SalikPayment.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();

		try {
			if (bindingResult.hasErrors()) {
				logger.debug("++---- ERRORS---++");
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}

			Utility.checkAuthCode(baseModel.getAuthCode(), session, messageSource);
			baseService.salikBalance(baseModel, session);
			Object response = baseService.salikPayment(baseModel, session);

			responseObject.put("salik", response);

			logger.debug("++---- LOGIN CONTROLLER UPDATE USER END ---++");
		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject;
	}

	@GetMapping(value = "/genericMockResponse", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object getIntentById(@RequestParam("file") String filename, BaseRequestModel baseModel) throws ApiException {

		Object response = null;
		try {

			response = Utility.getGenericMockResponse(filename);

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse2", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse2() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse2();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse3", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse3() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse3();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse4", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse4() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse4();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse5", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse5() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse5();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse6", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse6() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse6();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse9", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse9() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse9();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse7", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse7() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse7();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse8", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse8() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse8();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse10", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object mockResponse10() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse10();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse11", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse11() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse11();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse12", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse12() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse12();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse13", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse13() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse13();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse14", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse14() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse14();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@GetMapping(value = "/mockResponse15", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mockResponse15() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();
		Object response = null;
		try {

			response = Utility.getMockResponse15();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@PostMapping(value = "/balance1", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object balanceResponse() throws ApiException {

		Object response = null;
		try {

			response = Utility.getBalanceResponse();

		} catch (ApiException ex) {

			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());

		} catch (Exception ex) {

			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);

		}
		return response;
	}

	@PostMapping(value = "/success", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object successResponse() throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();

		try {

			responseObject.put(ApiConstants.API_RESPONSE, "Success");

		} catch (Exception ex) {

			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);

		}
		return responseObject;
	}

	@PostMapping(value = "/mePay1", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object mepay(@RequestBody @Validated(TransferValidate.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		Object response = null;
		Double amount = baseModel.getTransfer().getAmount();
		String mobileNo = baseModel.getTransfer().getMobileNo();

		try {
			Utility.updateMePayFileService(amount, mobileNo);
			response = Utility.getMePayResponse();
		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@PostMapping(value = "/sewaOutstanding1", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object sewaOutstanding1(@RequestBody @Validated(TransferValidate.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		Object response = null;

		try {

			response = Utility.getsewaOutstanding();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@PostMapping(value = "/sewaPayment1", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object sewaPayment1(@RequestBody @Validated(TransferValidate.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		Object response = null;
		String sewaNumber = baseModel.getSewa().getNumber();
		String sewaAmount = baseModel.getSewa().getAmount();

		try {

			Utility.updateSewaPaymentFileService(sewaNumber, sewaAmount);
			response = Utility.getSewaPaymentResponse();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@PostMapping(value = "/nolBalance1", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object nolBalance1(@RequestBody @Validated(TransferValidate.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		Object response = null;
		// String sewaAmount =baseModel.getNol().getAmount();

		try {

			response = Utility.getNolBalanceResponse();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@PostMapping(value = "/nolPayment1", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object sewaNolPayment1(@RequestBody @Validated(TransferValidate.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		Object response = null;
		String nolNumber = baseModel.getNol().getNumber();
		String nolAmount = baseModel.getNol().getAmount();

		try {

			Utility.updateNolFileService(nolNumber, nolAmount);
			response = Utility.getNolPaymentResponse();
		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@PostMapping(value = "/salikBalance1", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object salikBalance1(@RequestBody @Validated(TransferValidate.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		Object response = null;

		try {

			response = Utility.getsalikBalanceResponse();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	@PostMapping(value = "/salikPayment1", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object salikPayment1(@RequestBody @Validated(TransferValidate.class) BaseRequestModel baseModel,
			BindingResult bindingResult, HttpSession session) throws ApiException {

		Object response = null;
		String salikNumber = baseModel.getSalik().getNumber();
		String salikAmount = baseModel.getSalik().getAmount();

		try {

			Utility.updateSalikFileService(salikNumber, salikAmount);
			response = Utility.getsalikPaymentResponse();

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return response;
	}

	/*
	 * @PostMapping(value = "/fileUpload")
	 * 
	 * @ResponseBody public String
	 * handleFileUpload(@RequestBody @RequestParam("file") MultipartFile file, Model
	 * model) {
	 * 
	 * 
	 * System.out.println("@@@@@@@@@@@@@   "+file.getOriginalFilename()); String
	 * reponse="";
	 * 
	 * try { Utility.store(file); reponse = "You successfully uploaded " +
	 * file.getOriginalFilename() ; files.add(file.getOriginalFilename()); } catch
	 * (Exception e) { reponse = "FAIL to upload " + file.getOriginalFilename(); }
	 * return reponse; }
	 */

	@PostMapping(value = "/fileUpload", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object fileUpload(@RequestParam("file") MultipartFile multiparFile, HttpSession session)
			throws ApiException {

		HashMap<String, Object> responseObject = new HashMap<>();

		try {

			Utility.store(multiparFile);

		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}
		return responseObject.put("response", ApiConstants.SUCCESS);
	}

	@PostMapping(value = "/weather", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object weatherReport(@RequestBody @Valid BaseRequestModel baseModel, BindingResult bindingResult,
			HttpSession session) throws ApiException {

		String weather = "";
		HashMap<String, Object> responseObject = new HashMap<>();

		try {

			if (bindingResult.hasErrors()) {
				logger.debug(ApiConstants.BINDING_ERRORS);
				throw new ApiException(ErrorConstants.INVALIDDATA, Utility.getFirstErrorInformation(bindingResult));
			}

			if (baseModel.getWeather() != null) {

				if (baseModel.getWeather().getLang().equals("ar")) {
					weather = "الطقس اليوم مشمس 24 درجة";

				} else if (baseModel.getWeather().getLang().equals("en")) {
					weather = "The weather today is Sunny 24 degrees";

				}

			}
			responseObject.put("response", weather);

		} catch (ApiException ex) {
			logger.error(ex.getErrorCode() + " : " + ex.getErrorCode(), ex);
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ex.getErrorCode(), ex.getErrorMessage());
		} catch (Exception ex) {
			logger.error(Utility.getExceptionMessage(ex));
			logger.error(CONTROLLER_END_EXCEPTION);
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		}

		return responseObject;

	}

}
