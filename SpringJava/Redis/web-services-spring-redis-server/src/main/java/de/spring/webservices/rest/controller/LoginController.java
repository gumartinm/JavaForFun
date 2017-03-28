package de.spring.webservices.rest.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String login() {
        return "login";
    }
}
	
