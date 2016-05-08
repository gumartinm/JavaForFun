package de.spring.stomp.controllers;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class UserMessageTradeController {

    @MessageMapping("/trade")
    @SendToUser(destinations="/topic/position-updates", broadcast=false /* No idea what is this for */)
    public String executeTrade(String trade, Principal principal) {

    	return "[" + LocalDateTime.now() + "]: " + trade;
    }
}
