package com.pmk.notif.response;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class ResponseMessage {

    public Map<String, Object> success(String rc, int status, String message, Object obj) {

        Map<String, Object> resp = new HashMap<>();

        resp.put("rc", rc);
        resp.put("statusCode", HttpStatus.ACCEPTED);
        resp.put("status", status);
        resp.put("message", message);
        resp.put("data", obj);
        return resp;

    }
    
}
