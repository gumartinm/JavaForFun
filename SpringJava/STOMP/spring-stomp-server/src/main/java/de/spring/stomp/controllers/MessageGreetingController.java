package de.spring.stomp.controllers;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MessageGreetingController {

	// Sending data to /app/greeting from STOMP client (client must first connect to endpoint, in my case portfolio)
	// connecting to this URL -> http://172.17.0.3/spring-stomp-server/portfolio
	// sending data to /app/greeting
	
	// The data sent to /app/greeting will retrieved by this method.
	@MessageMapping("/greeting")
	public String handle(String greeting) {
		// STOMP clients subscribed to /topic/greeting will receive the returned data from this method.
		// Destination is selected based on a convention but can be overridden via @SendTo
		return "[" + LocalDateTime.now() + ": " + greeting;
	}
}
