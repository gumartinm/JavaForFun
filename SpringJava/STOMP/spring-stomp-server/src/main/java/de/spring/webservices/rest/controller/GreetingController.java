package de.spring.webservices.rest.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class GreetingController {

	private SimpMessagingTemplate template;

    @Autowired
    public GreetingController(SimpMessagingTemplate template) {
        this.template = template;
    }

//    @MessageMapping("/greeting")
//    public String handle(String greeting) {
//        return "[" + LocalDateTime.now() + ": " + greeting;
//    }

	@RequestMapping(path="/greetings", method=RequestMethod.POST)
    public void handle(String greeting) {
		String text = "[" + LocalDateTime.now() + "]:" + greeting;
		this.template.convertAndSend("/topic/greeting", text);
    }

}
