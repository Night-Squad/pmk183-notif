package com.bjbs.haji.business.apis.controllers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("api/core_banking")
public class CoreBankingController {
	
	@Value("${url.core-bank-server}")
	private String urlCoreBankServer;

	@Value("${url.core-bank-server-2}")
	private String urlCoreBankServer2;

	@Value("${url.core-bank-client}")
	private String urlCoreBankClient;

	static final Logger logger = LogManager.getLogger(CoreBankingController.class.getName());
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	@PostMapping(value = "/getCif", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getCif(@RequestBody String request) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        
		String url = urlCoreBankServer + "Gateway/service/v2/postData";
        
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormatter = new SimpleDateFormat("hhmmss");
		SimpleDateFormat dateGenerateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeGenerateFormatter = new SimpleDateFormat("HH:mm:ss");

        Date date = new Date();
		
		String ipCoreBank = urlCoreBankClient;
        String data = "00035" + ipCoreBank + dateGenerateFormatter.format(date) + timeGenerateFormatter.format(date);

        String authKey = calculateRFC2104HMAC(data, "fosgw");
        JSONObject requestJson=new JSONObject(request);
        requestJson.put("authKey", authKey);
        requestJson.put("reqId", "00035");
        requestJson.put("txDate", dateFormatter.format(date));
        requestJson.put("txHour", timeFormatter.format(date));
        requestJson.put("channelId", "27");
        requestJson.put("userGtw", "fosgw");

        RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestBody = new HttpEntity<String>(requestJson.toString());
		String response = restTemplate.postForObject(url, requestBody, String.class);

		logger.debug("data : " + data);
		logger.debug("authKey : " + authKey);
		logger.debug("dateFormatter : " + dateFormatter.format(date));
		logger.debug("timeFormatter : " + timeFormatter.format(date));
		logger.debug("new Request : " + requestJson.toString());
		
		logger.debug("Core Banking getAccount response : " + response);
		
		return response;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
	public String login(@RequestBody String body) {

		String url = urlCoreBankServer2 + "api/developer/auth/sign-in";
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		try {
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<String>(body, headers);
			String response = restTemplate.postForObject(url, request, String.class);
			return response;
		} catch (HttpClientErrorException e) {
			return e.getResponseBodyAsString();
		}
	}

	@PostMapping(value = "/getAccount", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAccount(@RequestBody String request) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		
		String url = urlCoreBankServer + "Gateway/service/v2/postData";
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");  
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HHmmss");  
		SimpleDateFormat dateGenerateFormatter = new SimpleDateFormat("yyyy-MM-dd");  
		SimpleDateFormat timeGenerateFormatter = new SimpleDateFormat("HH:mm:ss");

		Date date = new Date();

		String ipCoreBank = urlCoreBankClient;
		String data = "00002" + ipCoreBank + dateGenerateFormatter.format(date) + timeGenerateFormatter.format(date);
		
		String authKey = calculateRFC2104HMAC(data, "fosgw");
		JSONObject requestJson = new JSONObject(request);
		requestJson.put("authKey", authKey);
		requestJson.put("txDate", dateFormatter.format(date));
		requestJson.put("txHour", timeFormatter.format(date));
		
		logger.debug("data : " + data);
		logger.debug("authKey : " + authKey);
		logger.debug("dateFormatter : " + dateFormatter.format(date));
		logger.debug("timeFormatter : " + timeFormatter.format(date));
		logger.debug("new Request : " + requestJson.toString());
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestBody = new HttpEntity<String>(requestJson.toString());
		String response = restTemplate.postForObject(url, requestBody, String.class);
		
		logger.debug("Core Banking getAccount response : " + response);
		
		return response;
	}
	
	@PostMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_VALUE)
	public String transaction(@RequestBody String request) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		
		String url = urlCoreBankServer + "Gateway/service/v2/postData";
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");  
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HHmmss");  
		SimpleDateFormat dateGenerateFormatter = new SimpleDateFormat("yyyy-MM-dd");  
		SimpleDateFormat timeGenerateFormatter = new SimpleDateFormat("HH:mm:ss");  
		SimpleDateFormat dateRkFormatter = new SimpleDateFormat("dd-MM-yyyy");  
		Date date = new Date();
		String ipCoreBank = urlCoreBankClient;
		String data = "00005" + ipCoreBank + dateGenerateFormatter.format(date) + timeGenerateFormatter.format(date);
		
		String authKey = calculateRFC2104HMAC(data, "fosgw");
		JSONObject requestJson = new JSONObject(request);
		requestJson.put("authKey", authKey);
		requestJson.put("txDate", dateFormatter.format(date));
		requestJson.put("txHour", timeFormatter.format(date));
		requestJson.put("date", dateRkFormatter.format(date));
		requestJson.put("date_rk", dateRkFormatter.format(date));
		
		logger.debug("data : " + data);
		logger.debug("authKey : " + authKey);
		logger.debug("dateFormatter : " + dateFormatter.format(date));
		logger.debug("timeFormatter : " + timeFormatter.format(date));
		logger.debug("new Request : " + requestJson.toString());
		
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestBody = new HttpEntity<String>(requestJson.toString());
		String response = restTemplate.postForObject(url, requestBody, String.class);
		
		logger.debug("Core Banking transaction response : " + response);
		
		return response;
	}

	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}

		String result=formatter.toString();
		formatter.close();
		return result;
	}

	public static String calculateRFC2104HMAC(String data, String key)
		throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
	{
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		return toHexString(mac.doFinal(data.getBytes()));
	}
	
}
