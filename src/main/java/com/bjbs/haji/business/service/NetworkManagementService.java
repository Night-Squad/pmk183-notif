package com.bjbs.haji.business.service;

import com.bjbs.haji.business.apis.dtos.Response;

public interface NetworkManagementService {
    Response connect(String networkType);
    Response connect();
    Response disconnect();
    void updateAfterConnect(String networkType, String rc, String message);
}
