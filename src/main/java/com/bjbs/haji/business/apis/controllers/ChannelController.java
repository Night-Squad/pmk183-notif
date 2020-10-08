package com.bjbs.haji.business.apis.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjbs.haji.business.apis.dtos.ChannelDTO;
import com.bjbs.haji.business.models.Channel;
import com.io.iona.springboot.controllers.HibernateCRUDController;

/**
 * Created By Aristo Pacitra
 */

@RestController
@RequestMapping("/api/channel")
public class ChannelController extends HibernateCRUDController<Channel, ChannelDTO> {

}
