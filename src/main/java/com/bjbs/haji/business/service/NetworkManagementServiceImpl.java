package com.bjbs.haji.business.service;

import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.models.NetworkManagement;
import com.bjbs.haji.business.repositories.haji.NetworkManagementRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class NetworkManagementServiceImpl implements NetworkManagementService {

    @Value("${url.switching.app}")
    String urlSwitchingApp;

    @Autowired
    NetworkManagementRepository networkManagementRepository;

    @Override
    public Response connect(String networkType) {
        Response response = new Response();
        RestTemplate restTemplate = new RestTemplate();
        try {
            String networkManagementUrl = urlSwitchingApp + "/api/switching_haji/network_management";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> parameter = new HashMap<>();
            parameter.put("networkCode", networkType);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(parameter, headers);
            ResponseEntity<String> networkManagementResponse = restTemplate.exchange(networkManagementUrl, HttpMethod.GET, entity, String.class);
            JSONObject networkManagementObject = new JSONObject(networkManagementResponse.getBody());
            if (networkManagementObject.get("rc").toString().equals("00")) {
                response.setRC("00");
                response.setData(null);
                switch (networkType) {
                    case "001" :
                    case "301" :
                        response.setMessage("Connect");
                        break;
                    case "002" :
                        response.setMessage("Sign Off");
                        break;
                    case "201" : {
                        response.setMessage("Cut Off");
                        break;
                    }
                }

            } else {
                response.setRC("99");
                response.setData(null);
                response.setMessage("Disconnected");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setRC("99");
            response.setData(null);
            response.setMessage("Disconnected");
        }
        return response;
    }

    @Override
    public Response connect() {
        Response response = new Response();
        RestTemplate restTemplate = new RestTemplate();
        try {
            String networkManagementUrl = urlSwitchingApp + "/api/switching_haji/connect";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> parameter = new HashMap<>();

            HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(parameter, headers);
            ResponseEntity<String> networkManagementResponse = restTemplate.exchange(networkManagementUrl, HttpMethod.GET, entity, String.class);
            JSONObject networkManagementObject = new JSONObject(networkManagementResponse.getBody());
            response.setRC(networkManagementObject.get("rc").toString());
            response.setData(null);
            response.setMessage(networkManagementObject.get("message").toString());
            return  response;
        } catch (Exception e) {
            e.printStackTrace();
            response.setRC("99");
            response.setData(null);
            response.setMessage("Connect Fail");
            return  response;
        }
    }

    @Override
    public Response disconnect() {
        Response response = new Response();
        RestTemplate restTemplate = new RestTemplate();
        try {
            String networkManagementUrl = urlSwitchingApp + "/api/switching_haji/disconnect";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> parameter = new HashMap<>();

            HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(parameter, headers);
            ResponseEntity<String> networkManagementResponse = restTemplate.exchange(networkManagementUrl, HttpMethod.GET, entity, String.class);
            JSONObject networkManagementObject = new JSONObject(networkManagementResponse.getBody());
            response.setRC(networkManagementObject.get("rc").toString());
            response.setData(null);
            response.setMessage(networkManagementObject.get("message").toString());
            return  response;
        } catch (Exception e) {
            e.printStackTrace();
            response.setRC("99");
            response.setData(null);
            response.setMessage("Disconnect Fail");
            return  response;
        }
    }

    @Override
    public void updateAfterConnect(String networkType, String rc, String message) {
        try {
            NetworkManagement networkManagement = networkManagementRepository.findById(1).orElse(null);
            if (networkManagement == null) {
                networkManagement = new NetworkManagement();
                networkManagement.setNetworkManagementId(1);
            }
            switch (networkType) {
                case "001" : {
                    networkManagement.setLastSignOn(new Date());
                    break;
                }
                case "002" : {
                    networkManagement.setLastSignOff(new Date());
                    break;
                }
            }
            networkManagement.setNetworkManagementStatus(message);
            networkManagementRepository.save(networkManagement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
