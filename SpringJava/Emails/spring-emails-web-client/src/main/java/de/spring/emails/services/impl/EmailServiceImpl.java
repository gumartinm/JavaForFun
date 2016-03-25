package de.spring.emails.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.spring.emails.services.EmailService;


@Service("emailService")
public class EmailServiceImpl implements EmailService {
	private static final String DEFAULT_FROM_VALUE = "noreply@gumartinm.name";
	
	private final JavaMailSender mailSender;
	
	@Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
    	this.mailSender = mailSender;
    }
    
	@Override
	public void sendEmail(String[] to, String subject, String text,  boolean isHtml,
			List<Resource> attachments, Map<String, Resource> inline) throws MessagingException {
		Assert.notEmpty(to, "required email 'to' field");
		Assert.hasLength(subject, "required email 'subject' field");
		Assert.hasLength(text, "required email 'text' field");
		
		final MimeMessage mimeMessage = mailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
		
		message.setTo(to);
		message.setFrom(DEFAULT_FROM_VALUE);
		message.setSubject(subject);
		message.setSentDate(new Date());
		message.setText(text, isHtml);

		if (inline != null) {
			for (Map.Entry<String, Resource> entry : inline.entrySet()) {
				message.addInline(entry.getKey(), entry.getValue());
			}
		}
		
		if (attachments != null) {
			for (Resource attachment : attachments) {
				message.addAttachment(attachment.getFilename(), attachment);
			}
		}

		
		mailSender.send(mimeMessage);
	}
	
	@Override
	@Async("asyncEmailSender")
	public void sendEmailAsync(String[] to, String subject, String text, boolean isHtml,
			List<Resource> attachments, Map<String, Resource> inline) throws MessagingException {
		sendEmail(to, subject, text, isHtml, attachments, inline);
	}
}
