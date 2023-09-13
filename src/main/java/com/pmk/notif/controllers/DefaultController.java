package com.pmk.notif.controllers;

import com.pmk.notif.dtos.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class DefaultController {

    private static final Logger logger = LogManager.getLogger(DefaultController.class);

    @GetMapping()
    public ResponseEntity<Response> defaultApi() throws Exception {
        logger.info("Test API!!");
        Response response = new Response();
        response.setRC("00");
        response.setMessage("OK");
        response.setData("Hello World");
        return ResponseEntity.ok(response);
    }
}
