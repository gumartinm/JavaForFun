package de.spring.webservices.rest.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RestGreetingController {

	private SimpMessagingTemplate template;

    @Autowired
    public RestGreetingController(SimpMessagingTemplate template) {
        this.template = template;
    }

    // Sending data to /topic/greeting from REST service.
	// POST http://localhost:8080/spring-stomp-server/greetings
    
	@RequestMapping(path="/greetings", method=RequestMethod.POST)
    public void handle(@RequestBody String greeting) {
		String text = "[" + LocalDateTime.now() + "]:" + greeting;
		
		// STOMP clients subscribed to /topic/greeting will receive the data sent by the convertAndSend method.
		this.template.convertAndSend("/topic/greeting", text);
    }

}
