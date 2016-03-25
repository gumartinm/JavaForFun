package de.spring.webservices.rest.controller;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.spring.emails.services.EmailMakerService;
import de.spring.emails.services.EmailService;

@RestController
@RequestMapping("/api/emails/")
public class EmailController {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class);
	private static final String USER = "Gustavo Martin Morcuende";
	private static final String USER_ADDRESS = "noemail@gumartinm.name";
	private static final String TEMPLATE = "email-template";
	private static final String LOGO = "logo";
	private static final String LOGO_RESOURCE = "email/logo.png";
	private static final String SUBJECT_MESSAGE_KEY = "email.subject";

	private final EmailService emailService;
	private final EmailMakerService emailMakerVelocityService;
	
	@Autowired
    public EmailController(EmailService emailService, EmailMakerService emailMakerVelocityService) {
		this.emailService = emailService;
		this.emailMakerVelocityService = emailMakerVelocityService;
	}

	@RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void emails() throws MessagingException {
		final String emailSubject = emailMakerVelocityService.getSubject(SUBJECT_MESSAGE_KEY, Locale.getDefault());
		final String emailText = doEmailText();
		final Map<String, Resource> inline = new HashMap<>();
		inline.put(LOGO, new ClassPathResource(LOGO_RESOURCE));
		final String[] to = { USER_ADDRESS };

		try {
			emailService.sendEmailAsync(to, emailSubject, emailText, true, null, inline);
		} catch (MessagingException ex) {
			LOGGER.error("Send email error", ex);
		}
    }
	
	private String doEmailText() {
	    final String isoDateTime = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		final Map<String, String> text = new HashMap<>();
		text.put("user", USER);
		text.put("date", isoDateTime);
		text.put(LOGO, LOGO);
		return emailMakerVelocityService.emailMaker(text, TEMPLATE, Locale.getDefault());
	}
}
