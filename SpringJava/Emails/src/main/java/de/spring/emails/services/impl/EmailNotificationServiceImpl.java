package de.spring.emails.services.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import de.spring.emails.services.EmailNotificationService;

public class EmailNotificationServiceImpl implements EmailNotificationService {
	private final JavaMailSender mailSender;
    private final VelocityEngine velocityEngine;
	
    public EmailNotificationServiceImpl(JavaMailSender mailSender, VelocityEngine velocityEngine) {
    	this.mailSender = mailSender;
    	this.velocityEngine = velocityEngine;
    }
    
	@Override
	public void sendEmail() {
	      final MimeMessagePreparator preparator = new MimeMessagePreparator() {
	    	  
	    	  public void prepare(MimeMessage mimeMessage) throws Exception {
	    		  final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
		          message.setTo("toemail@example.com");
		          message.setBcc("bccemail@example.com");
		          message.setFrom(new InternetAddress("fromemail@example.com") );
		          message.setSubject("New funny alert");

		          final LocalDateTime dateTime = LocalDateTime.now();
		          message.setSentDate(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
		          Map model = new HashMap<>();	             
		          model.put("newMessage", "");
		             
		          final String text = VelocityEngineUtils.mergeTemplateIntoString(
		        		  velocityEngine, "templates/email-template.vm", "UTF-8", model);
		          message.setText(text, true);		       
	    	  }
	      };
	      
	      mailSender.send(preparator);
	}

}
