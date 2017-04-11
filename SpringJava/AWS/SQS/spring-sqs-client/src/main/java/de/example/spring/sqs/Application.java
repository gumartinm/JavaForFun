package de.example.spring.sqs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import de.example.spring.sqs.service.SenderNotificationServiceImpl;
import de.example.spring.sqs.service.dto.NotificationDTO;


@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  CommandLineRunner lookup(SenderNotificationServiceImpl sender) {

    return args -> {
  	  String name = "Gustavo";
  	  String surname = "Martin";
  	  
  	  if (args.length > 0) {
  		  name = args[0];
  		  surname = args[1];
  	  }
    	
  	  sender.send(new NotificationDTO(name, surname));	
    };
  }

}
