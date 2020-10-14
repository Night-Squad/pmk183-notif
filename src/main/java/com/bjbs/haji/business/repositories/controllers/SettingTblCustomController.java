package com.bjbs.haji.business.repositories.controllers;

import com.bjbs.haji.business.models.SettingTbl;
import com.bjbs.haji.business.repositories.haji.SettingTblRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repo/setting")
public class SettingTblCustomController {

    @Autowired
    SettingTblRepository settingTblRepository;

    @GetMapping("/official_account_number")
    public SettingTbl getOfficialAccountNumber() {
        return settingTblRepository.getSettingBySettingCode("rekening003");
    }
}
