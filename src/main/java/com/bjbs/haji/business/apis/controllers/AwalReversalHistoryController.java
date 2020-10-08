package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.AwalReversalHistoryDTO;
import com.bjbs.haji.business.models.AwalReversalHistory;
import com.io.iona.springboot.controllers.HibernateCRUDController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/awal-reversal-history")
public class AwalReversalHistoryController extends HibernateCRUDController<AwalReversalHistory, AwalReversalHistoryDTO> {
}
