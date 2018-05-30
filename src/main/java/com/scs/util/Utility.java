package com.scs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scs.exception.ApiException;
import com.scs.rest.model.ApiServicesRequest;

public class Utility {

	private static final Logger logger = Logger.getLogger(Utility.class);
	

	private static Pattern LONG_TEXT_PATTERN = Pattern.compile("^[a-zA-Z0-9\\p{Punct} ]{0,512}$");
	
	private final static Path rootLocation = Paths.get(ApiConstants.FILE_PATH);

	@Autowired
	private static MessageSource messageSource;

	// Method to return Exception message from stack trace
	public static String getExceptionMessage(Exception ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();

		return exceptionAsString;
	}

	private static final String code = "971";

	// Get The field information for parameters validation from json request
	public static String getErrorInformation(BindingResult bindingResult) {

		logger.debug("++----Utility -------getErrorInformation()---++");
		StringBuffer sb = new StringBuffer();

		for (Object object : bindingResult.getAllErrors()) {
			if (object instanceof FieldError) {
				FieldError fieldError = (FieldError) object;
				sb.append(fieldError.getField() + "-" + fieldError.getDefaultMessage() + "--");
				logger.debug("FIELD ERROR_" + fieldError.getField());
			}

			if (object instanceof ObjectError) {
				ObjectError objectError = (ObjectError) object;
				sb.append(objectError.getObjectName());
				logger.debug("OBJECT_ERROR_" + objectError.getObjectName());
			}

		}

		return sb.toString();
	}

	public static String getFirstErrorInformation(BindingResult bindingResult) {
		String fieldName = null;
		logger.debug("++----Utility Methods--getErrorInformation()---++");
		StringBuffer sb = new StringBuffer();
		// Boolean check = false;
		for (Object object : bindingResult.getAllErrors()) {
			if (object instanceof FieldError) {
				FieldError fieldError = (FieldError) object;
				sb.append(fieldError.getDefaultMessage().trim());
				fieldName = fieldError.getField();
				logger.debug("FIELD ERROR_" + fieldError.getField());
				break;
			}

			if (object instanceof ObjectError) {
				ObjectError objectError = (ObjectError) object;
				sb.append(objectError.getObjectName());
				logger.debug("OBJECT_ERROR_" + objectError.getObjectName());
				break;
			}
			//
			// if(check == true)
			// break;
		}

		return fieldName + " " + sb.toString();
	}

	// Logging for complete object details

	public static String getLogDump(Object o, String logMessage) {
		StringBuffer logDump = new StringBuffer();
		logDump.append("<-----------------------------------------" + logMessage
				+ "------------------------------------------>");
		logDump.append(System.getProperty("line.separator"));
		if (o != null) {
			logDump.append(o.toString());
		}
		logDump.append(System.getProperty("line.separator"));
		return logDump.toString();
	}

	public static String getLogDump(String logMessage) {
		StringBuffer logDump = new StringBuffer();
		logDump.append(
				"<------------------------------------------------------------------------------------------------------------------>");
		logDump.append(System.getProperty("line.separator"));
		logDump.append(logMessage);
		logDump.append(System.getProperty("line.separator"));
		return logDump.toString();
	}

	/**
	 * Method to be used if a constant product hash is required throughout a
	 * single session.
	 * 
	 * @param productID
	 * @return
	 */
	public static String generateHashKey(String productID, String sessionId) {

		String uniqueSessionString = sessionId;
		MessageDigest messageDigest;
		try {
			String stringToHash = productID + uniqueSessionString;
			messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] bytes = messageDigest.digest(stringToHash.getBytes());
			// String digest = Base64A.encodeBytes(bytes);
			String digest = Base64.encodeBase64String(bytes);
			return digest;
		} catch (Exception e) {
			logger.debug(getExceptionMessage(e));

		}
		return "";
	}

	public static boolean checkNullEmpty(String value) {

		if (value != null && !value.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public static String getMaskedNumber(String number) {

		int ccLength = 16;
		int acLength = 13;
		int ciflength = 8;
		String result = "";
		if (checkNullEmpty(number)) {
			logger.debug("Length of Card " + number.length());
			if (ccLength == number.length()) {
				logger.debug(" CARD MASKING " + number.length());
				result = getX(11) + number.substring(12);
			} else if (acLength == number.length()) {
				logger.debug("ACCOUNT NUMBER MASKING " + number.length());
				result = number.substring(0, 3) + getX(3) + number.substring(5, 7) + getX(3) + number.substring(11, 13);
			} else if (ciflength == number.length()) {
				logger.debug("CIF MASKING " + number.length());
				result = getX(3) + number.substring(3, 5) + getX(3);
			} else {
				return result;
			}

		} else {
			return result;
		}

		logger.debug("Masked Number " + result);

		return result;
	}

	public static String getMaskedString(String text) {
		StringBuffer masked = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			masked.append('X');
		}
		return masked.toString();
	}

	public static Long generateRandom() {
		DateFormat df = new SimpleDateFormat("yyMMddHHmmssSSS");
		Date today = Calendar.getInstance().getTime();
		String tranDate = df.format(today);
		return Long.parseLong(tranDate);

	}

	public static String getX(int count) {

		String result = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++) {
			sb.append("X");
		}

		result = sb.toString();
		return result;
	}

	public static String getMessageByLocale(String id, MessageSource messageSource) {
		String message;
		try {
			message = messageSource.getMessage(id, null, LocaleContextHolder.getLocale());
		} catch (NoSuchMessageException ex) {
			message = messageSource.getMessage("SERVICEEXCEPTION", null, LocaleContextHolder.getLocale());
		}
		return message;
	}

	public static String logAPIRequest(ApiServicesRequest enbdServiceRequest) {
		try {
			ApiServicesRequest cloned = (ApiServicesRequest) enbdServiceRequest.clone();

			// if (enbdServiceRequest.getPassword() != null) {
			//
			// cloned.setPassword(getMaskedString(cloned.getPassword()));
			//
			// }
			// if (enbdServiceRequest.getOldPassword() != null) {
			//
			// cloned.setOldPassword(getMaskedString(cloned.getOldPassword()));
			//
			// }
			// if (enbdServiceRequest.getNewPassword() != null) {
			//
			// cloned.setNewPassword(getMaskedString(cloned.getNewPassword()));
			//
			// }

			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writer().withRootName(ApiConstants.API_REQUEST).writeValueAsString(cloned);

		} catch (CloneNotSupportedException | JsonProcessingException e) {
			logger.debug("Error logging the request" + e);
		}
		return null;
	}

	public static String logAPIResponse(ResponseEntity<JsonNode> serviceResponse, ObjectMapper mapper)
			throws JsonProcessingException {
		// if
		// (serviceResponse.getBody().get(ApiConstants.API_RESPONSE).has("imageStream"))
		// {
		// JsonNode duplicate = serviceResponse.getBody().deepCopy();
		// List<JsonNode> parentNodes = duplicate.findParents("imageStream");
		// if (parentNodes != null) {
		// for (JsonNode parentNode : parentNodes) {
		// logger.debug("Image found.....removed from logs" );
		// ((ObjectNode) parentNode).remove("imageStream");
		// }
		// }
		// return mapper.writer().writeValueAsString(duplicate);
		// } else {

		return mapper.writer().writeValueAsString(serviceResponse.getBody());
		// }
	}

	private static String canonicalize(String input) {
		String canonical = sun.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD, 0);
		return canonical;
	}

	public static String getRequestHeaderParam(HttpServletRequest request, String fieldName) {
		String input = request.getHeader(fieldName);
		if (checkNullEmpty(input)) {
			// important - always canonicalize before validating
			String canonical = canonicalize(input);
			if (!LONG_TEXT_PATTERN.matcher(canonical).matches()) {
				logger.debug("Improper format in " + fieldName + " field : " + input);
				return "";
			}
			return canonical;
		}
		return input;
	}

	public static String checkResponseStringNull(String responseString) throws ApiException {
		if (responseString == null) {
			logger.debug(responseString + " is coming null");
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, messageSource);
		} else {
			return responseString;
		}
	}

	public static void checkAuthCode(String authCode, HttpSession session, MessageSource messageSouce)
			throws ApiException {

		if (!authCode.equals(SessionUtil.getValue(session, StorageConstants.AUTH_CODE))) {
			throw new ApiException(ErrorConstants.INVALID_AUTH_CODE, messageSouce);
		}

	}
	
	public static String getMockResponse() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	
	public static String getGenericMockResponse(String filename) throws ApiException {
		try {

			String file = "/"+filename+".txt";
			
			File filepath = new File(ApiConstants.SERVICES_PATH + file);
			return FileUtils.readFileToString(filepath);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	
	public static String getMockResponse2() throws ApiException {
		try {
			
			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse2.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse3() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse3.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse4() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse4.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse5() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse5.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse6() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse6.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse7() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse7.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse8() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse8.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse9() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse9.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse10() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse10.txt");
			System.out.println("--------------------------------------------------------------------------------------");
			System.out.println(FileUtils.readFileToString(file,"utf-8"));
			return FileUtils.readFileToString(file,"utf-8");
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse11() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse11.txt");
			return FileUtils.readFileToString(file, "utf-8");
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse12() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse12.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse13() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse13.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse14() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse14.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMockResponse15() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mockResponse15.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	
	
	public static String getBalanceResponse() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/balance.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getMePayResponse() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/mePay.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getSewaPaymentResponse() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/sewaPayment.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getsewaOutstanding() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/sewaOutstanding.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getNolBalanceResponse() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/nolBalance.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getNolPaymentResponse() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/nolPayment.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getsalikBalanceResponse() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/salikBalance.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	public static String getsalikPaymentResponse() throws ApiException {
		try {

			File file = new File(ApiConstants.SERVICES_PATH + "/salikPayment.txt");
			return FileUtils.readFileToString(file);
		} catch (IOException ex) {
			throw new ApiException(ErrorConstants.SERVICEEXCEPTION, "Mock Response not recorded. Please try with mock = true");
		}

	}
	
	@SuppressWarnings("unchecked")
	public static void updateMePayFileService(Double amount,String mobileNo) throws IOException {
		
		JSONParser parser = new JSONParser();
		
		try {

			logger.info(" Transfer Amount " + amount);
			logger.info(" Mobile Number " + mobileNo);

			Object readbalanceObj = parser.parse(new FileReader(ApiConstants.SERVICES_PATH + "/balance.txt"));
            JSONObject balanceJsonObject = (JSONObject) readbalanceObj;
            Double balance = (Double) balanceJsonObject.get("balance");
            Double uppdateBalance = balance - amount;
			
            JSONObject updateBalanceJsonObject = new JSONObject();
            updateBalanceJsonObject.put("balance", uppdateBalance);
            
            @SuppressWarnings("resource")
			FileWriter balanceWrite = new FileWriter(ApiConstants.SERVICES_PATH + "/balance.txt");
            balanceWrite.write(updateBalanceJsonObject.toJSONString());
            balanceWrite.flush();
            
            logger.info(" Balance Updated ");
			
            JSONObject writeTransfer = new JSONObject();
            JSONObject writeAmountAndMobileNo = new JSONObject();
            writeAmountAndMobileNo.put("amount", amount);
            writeAmountAndMobileNo.put("mobileNo", mobileNo);
            writeTransfer.put("transfer", writeAmountAndMobileNo);
            
            @SuppressWarnings("resource")
			FileWriter file1 = new FileWriter(ApiConstants.SERVICES_PATH + "/mePay.txt");
            file1.write(writeTransfer.toJSONString());
            file1.flush();
            
            logger.info(" MePay Updated ");

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
        } catch (IOException e) {
        	logger.error(e.getMessage());
        } catch (ParseException e) {
        	logger.error(e.getMessage());
        }


	}
	
	@SuppressWarnings("unchecked")
	public static void updateSewaPaymentFileService(String sewaNumber,String sewaAmount) throws IOException {
		
		JSONParser parser = new JSONParser();
		
		try {

			logger.info(" Sewa Number " + sewaNumber);
			logger.info(" Sewa Amount " + sewaAmount);

			Object readSewaBalanceObj = parser.parse(new FileReader(ApiConstants.SERVICES_PATH + "/sewaOutstanding.txt"));
            JSONObject sewaBalanceJsonObject = (JSONObject) readSewaBalanceObj;
            JSONObject getSewa = (JSONObject) sewaBalanceJsonObject.get("sewa");
            String getAmount = (String) getSewa.get("amount");
            
            Double sewaAmountToDouble = Double.parseDouble(sewaAmount);
            
            Double getAmountToDouble = Double.parseDouble(getAmount);
            
            Double uppdatedSawaAmount = getAmountToDouble - sewaAmountToDouble ;
            
            
            Object readBalanceObj = parser.parse(new FileReader(ApiConstants.SERVICES_PATH + "/balance.txt"));
            JSONObject balanceJsonObject = (JSONObject) readBalanceObj;
            Double getBalance = (Double) balanceJsonObject.get("balance");
            Double uppdatedBalance = getBalance - sewaAmountToDouble;

            logger.info(" Sewa Amount " + sewaAmount);
            
            JSONObject updatedBalanceJsonObject = new JSONObject();
            updatedBalanceJsonObject.put("balance", uppdatedBalance);
            
            @SuppressWarnings("resource")
			FileWriter balanceFile = new FileWriter(ApiConstants.SERVICES_PATH + "/balance.txt");
            balanceFile.write(updatedBalanceJsonObject.toJSONString());
            balanceFile.flush();
            
            
            
            JSONObject writeSewa = new JSONObject();
            JSONObject writeSewaAmount = new JSONObject();
            writeSewaAmount.put("amount", uppdatedSawaAmount.toString());
            writeSewa.put("sewa", writeSewaAmount);
            
            @SuppressWarnings("resource")
			FileWriter file2 = new FileWriter(ApiConstants.SERVICES_PATH + "/sewaOutstanding.txt");
            file2.write(writeSewa.toJSONString());
            file2.flush();
            
            
            
            
            JSONObject writeSewaPayment = new JSONObject();
            JSONObject writeSewaNumberAndAmount = new JSONObject();
            writeSewaNumberAndAmount.put("number", sewaNumber);
            writeSewaNumberAndAmount.put("amount", sewaAmount);
            writeSewaPayment.put("sewa", writeSewaNumberAndAmount);
            
            @SuppressWarnings("resource")
			FileWriter sewaPaymentFile = new FileWriter(ApiConstants.SERVICES_PATH + "/sewaPayment.txt");
            sewaPaymentFile.write(writeSewaPayment.toJSONString());
            sewaPaymentFile.flush();
            
            logger.info(" MePay Updated ");

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
        } catch (IOException e) {
        	logger.error(e.getMessage());
        } catch (ParseException e) {
        	logger.error(e.getMessage());
        }


	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public static void updateNolFileService(String nolNumber,String nolAmount) throws IOException {
		
		JSONParser parser = new JSONParser();
		
		try {

			logger.info(" Nol Number " + nolNumber);
			logger.info(" Nol Amount " + nolAmount);

			Object readNolBalanceObj = parser.parse(new FileReader(ApiConstants.SERVICES_PATH + "/nolBalance.txt"));
            JSONObject sewaNolJsonObject = (JSONObject) readNolBalanceObj;
            JSONObject getNol = (JSONObject) sewaNolJsonObject.get("nol");
            String getAmount = (String) getNol.get("amount");
            
            Double nolAmountToDouble = Double.parseDouble(nolAmount);
            
            Double getAmountToDouble = Double.parseDouble(getAmount);
            
            Double uppdatedSawaAmount = nolAmountToDouble + getAmountToDouble;
            
            JSONObject writeNol = new JSONObject();
            JSONObject writeNolAmount = new JSONObject();
            writeNolAmount.put("amount", uppdatedSawaAmount.toString());
            writeNol.put("nol", writeNolAmount);
            
            @SuppressWarnings("resource")
			FileWriter nolFile = new FileWriter(ApiConstants.SERVICES_PATH + "/nolBalance.txt");
            nolFile.write(writeNol.toJSONString());
            nolFile.flush();
            
            logger.info(" Balance Update ");
            
            Object readbalanceObj = parser.parse(new FileReader(ApiConstants.SERVICES_PATH + "/balance.txt"));
            JSONObject balanceJsonObject = (JSONObject) readbalanceObj;
            Double balance = (Double) balanceJsonObject.get("balance");
            Double uppdateBalance = balance - nolAmountToDouble;
			
            JSONObject updateBalanceJsonObject = new JSONObject();
            updateBalanceJsonObject.put("balance", uppdateBalance);
            
            @SuppressWarnings("resource")
			FileWriter balanceWrite = new FileWriter(ApiConstants.SERVICES_PATH + "/balance.txt");
            balanceWrite.write(updateBalanceJsonObject.toJSONString());
            balanceWrite.flush();
            
            logger.info(" Balance Updated ");
            
            
            JSONObject writeNolPayment = new JSONObject();
            JSONObject writeNolNumberAndAmount = new JSONObject();
            writeNolNumberAndAmount.put("number", nolNumber);
            writeNolNumberAndAmount.put("amount", nolAmount);
            writeNolPayment.put("nol", writeNolNumberAndAmount);
            
            @SuppressWarnings("resource")
			FileWriter sewaPaymentFile = new FileWriter(ApiConstants.SERVICES_PATH + "/nolPayment.txt");
            sewaPaymentFile.write(writeNolPayment.toJSONString());
            sewaPaymentFile.flush();
            
            logger.info(" nolPayment ");

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
        } catch (IOException e) {
        	logger.error(e.getMessage());
        } catch (ParseException e) {
        	logger.error(e.getMessage());
        }


	}
	
	@SuppressWarnings("unchecked")
	public static void updateSalikFileService(String salikNumber,String salikAmount) throws IOException {
		
		JSONParser parser = new JSONParser();
		
		try {

			logger.info(" salik Number " + salikNumber);
			logger.info(" salik Amount " + salikAmount);

			Object readSalikBalanceObj = parser.parse(new FileReader(ApiConstants.SERVICES_PATH + "/salikBalance.txt"));
            JSONObject salikJsonObject = (JSONObject) readSalikBalanceObj;
            JSONObject getSalik = (JSONObject) salikJsonObject.get("salik");
            String getAmount = (String) getSalik.get("amount");
            
            Double salikAmountToDouble = Double.parseDouble(salikAmount);
            
            Double getAmountToDouble = Double.parseDouble(getAmount);
            
            Double uppdatedSawaAmount = salikAmountToDouble + getAmountToDouble;
            
            JSONObject writeSalik = new JSONObject();
            JSONObject writeSalikAmount = new JSONObject();
            writeSalikAmount.put("amount", uppdatedSawaAmount.toString());
            writeSalik.put("salik", writeSalikAmount);
            
            @SuppressWarnings("resource")
			FileWriter nolFile = new FileWriter(ApiConstants.SERVICES_PATH + "/salikBalance.txt");
            nolFile.write(writeSalik.toJSONString());
            nolFile.flush();
            
            logger.info(" Balance Update ");
            
            Object readbalanceObj = parser.parse(new FileReader(ApiConstants.SERVICES_PATH + "/balance.txt"));
            JSONObject balanceJsonObject = (JSONObject) readbalanceObj;
            Double balance = (Double) balanceJsonObject.get("balance");
            Double uppdateBalance = balance - salikAmountToDouble;
			
            JSONObject updateBalanceJsonObject = new JSONObject();
            updateBalanceJsonObject.put("balance", uppdateBalance);
            
            @SuppressWarnings("resource")
			FileWriter balanceWrite = new FileWriter(ApiConstants.SERVICES_PATH + "/balance.txt");
            balanceWrite.write(updateBalanceJsonObject.toJSONString());
            balanceWrite.flush();
            
            logger.info(" Balance Updated ");
            
            
            JSONObject writeNolPayment = new JSONObject();
            JSONObject writeNolNumberAndAmount = new JSONObject();
            writeNolNumberAndAmount.put("number", salikNumber);
            writeNolNumberAndAmount.put("amount", salikAmount);
            writeNolPayment.put("nol", writeNolNumberAndAmount);
            
            @SuppressWarnings("resource")
			FileWriter sewaPaymentFile = new FileWriter(ApiConstants.SERVICES_PATH + "/salikPayment.txt");
            sewaPaymentFile.write(writeNolPayment.toJSONString());
            sewaPaymentFile.flush();
            
            logger.info(" salik Payment ");

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
        } catch (IOException e) {
        	logger.error(e.getMessage());
        } catch (ParseException e) {
        	logger.error(e.getMessage());
        }


	}
	
	public static void store(MultipartFile multipartFile) throws ApiException {
		try {
			
			File file = new File(ApiConstants.FILE_PATH+"/"+multipartFile.getOriginalFilename());
			
			if (file.exists())
			{
				logger.info("File already exists");
				//Files.copy(multipartFile.getInputStream(), rootLocation.resolve(multipartFile.getOriginalFilename()));
				
			}else{
				
				logger.info( "You can create your new file");
				Files.copy(multipartFile.getInputStream(), rootLocation.resolve(multipartFile.getOriginalFilename()));
			}
			
			
		} catch (IOException ex) {
			throw new RuntimeException("FAIL!");
		}

	}
}
