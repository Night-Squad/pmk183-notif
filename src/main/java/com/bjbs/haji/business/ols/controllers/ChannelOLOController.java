package com.bjbs.haji.business.ols.controllers;

import com.bjbs.haji.business.models.Channel;
import com.bjbs.haji.business.ols.ChannelOLO;
import com.io.iona.springboot.controllers.HibernateOptionListController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ol/channel")
public class ChannelOLOController extends HibernateOptionListController<Channel, ChannelOLO> {
    
}