package de.spring.webservices.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.spring.stomp.services.RestGreetingService;


@RestController
public class RestGreetingController {
	private final RestGreetingService restGreetingService;

    @Autowired
    public RestGreetingController(RestGreetingService restGreetingService) {
        this.restGreetingService = restGreetingService;
    }

    // Sending data to /topic/greeting from REST service.
	// POST http://localhost:8080/spring-stomp-server/greetings
    
	@RequestMapping(path="/greetings", method=RequestMethod.POST)
    public void handle(@RequestBody String greeting) {
		
		// STOMP clients subscribed to /topic/greeting will receive the data sent by the convertAndSend method.
		restGreetingService.doGreetings(greeting);
    }

}
