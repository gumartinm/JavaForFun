package de.example.spring.sns;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import de.example.spring.sns.service.SenderNotificationServiceImpl;
import de.example.spring.sns.service.dto.NotificationDTO;


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
  	  String subject = "test subject SNS";
  	  
  	  if (args.length > 0) {
  		  name = args[0];
  		  surname = args[1];
  		  subject = args[2];
  	  }
    	
  	  sender.send(subject, new NotificationDTO(name, surname));	
    };
  }

}
