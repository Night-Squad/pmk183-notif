package com.bjbs.haji.business.service;

import com.bjbs.haji.business.apis.dtos.Response;

public interface NetworkManagementService {
    Response connect(String networkType);
    void updateAfterConnect(String networkType, String rc, String message);
}
