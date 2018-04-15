package de.spring.webservices.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "de.spring.webservices.rest"
    })
@EnableAutoConfiguration
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class);
  }

}
