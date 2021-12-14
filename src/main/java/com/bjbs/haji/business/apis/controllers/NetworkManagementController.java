package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.Response;
import com.bjbs.haji.business.models.NetworkManagement;
import com.bjbs.haji.business.repositories.haji.NetworkManagementRepository;
import com.bjbs.haji.business.service.NetworkManagementService;
import com.bjbs.haji.business.utility.NetworkTypeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/network-management")
public class NetworkManagementController {

    @Autowired
    NetworkManagementService networkManagementService;

    @Autowired
    NetworkManagementRepository networkManagementRepository;

    @GetMapping("/sign-on")
    public ResponseEntity<Response> signOn(@RequestParam("userCode") String userCode) throws Exception {
        Response response = networkManagementService.connect(NetworkTypeConstant.SIGN_ON);
        networkManagementService.updateAfterConnect(userCode, response.getRC(), response.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/echo-test")
    public ResponseEntity<Response> echoTest(@RequestParam("userCode") String userCode) throws Exception {
        Response response = networkManagementService.connect(NetworkTypeConstant.ECHO_TEST);
        networkManagementService.updateAfterConnect(userCode, response.getRC(), response.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sign-off")
    public ResponseEntity<Response> signOff(@RequestParam("userCode") String userCode) throws Exception {
        Response response = networkManagementService.connect(NetworkTypeConstant.SIGN_OFF);
        networkManagementService.updateAfterConnect(userCode, response.getRC(), response.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cut-off")
    public ResponseEntity<Response> cutOff(@RequestParam("userCode") String userCode) throws Exception {
        Response response = networkManagementService.connect(NetworkTypeConstant.CUT_OFF);
        networkManagementService.updateAfterConnect(userCode, response.getRC(), response.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<Response> detail() {
        NetworkManagement networkManagement = networkManagementRepository.findById(1).orElse(null);
        Response response = new Response();
        response.setRC("00");
        response.setData(networkManagement);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/connect")
    public ResponseEntity<Response> connect() {
        Response response = networkManagementService.connect();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/disconnect")
    public ResponseEntity<Response> disconnect() {
        Response response = networkManagementService.disconnect();
        return ResponseEntity.ok(response);
    }

}
