package com.bjbs.haji.business.apis.controllers;

import com.bjbs.haji.business.apis.dtos.PelunasanReversalHistoryDTO;
import com.bjbs.haji.business.models.PelunasanReversalHistory;
import com.io.iona.springboot.controllers.HibernateCRUDController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pelunasan-reversal-history")
public class PelunasanReversalHistoryController extends HibernateCRUDController<PelunasanReversalHistory, PelunasanReversalHistoryDTO> {
}
