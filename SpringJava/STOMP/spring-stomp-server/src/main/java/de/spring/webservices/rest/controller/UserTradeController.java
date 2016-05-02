package de.spring.webservices.rest.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.spring.stomp.services.UserTradeService;

public class UserTradeController {
	private final UserTradeService userTradeService;

    @Autowired
    public UserTradeController(UserTradeService userTradeService) {
        this.userTradeService = userTradeService;
    }

    // Sending data to /topic/greeting from REST service.
	// POST http://localhost:8080/spring-stomp-server/trade
    
	@RequestMapping(path="/trade", method=RequestMethod.POST)
    public void handle(@RequestBody String user) {
		userTradeService.doTrade(user);
    }
}
