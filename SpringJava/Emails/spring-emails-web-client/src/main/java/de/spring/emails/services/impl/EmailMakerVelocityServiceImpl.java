package de.spring.emails.services.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import de.spring.emails.services.EmailMakerService;


@Service("emailMakerVelocityService")
public class EmailMakerVelocityServiceImpl implements EmailMakerService {
	private static final String TEMPLATES_DEFAULT_EXTENSION = ".vm";
	private static final String TEMPLATES_DEFAULT_PATH = "email/";
	private static final String EMAIL_CONTENT_ENCODING = "UTF-8";
	
    private final VelocityEngine velocityEngine;
	private final MessageSource messageSource;

	@Autowired
    public EmailMakerVelocityServiceImpl(VelocityEngine velocityEngine, MessageSource messageSource) {
    	this.velocityEngine = velocityEngine;
    	this.messageSource = messageSource;
    }

	@Override
	public String emailMaker(Map<String, String> text, String templateName, Locale locale) {
		final String templateLocation = TEMPLATES_DEFAULT_PATH + templateName + TEMPLATES_DEFAULT_EXTENSION;
		final Map<String, Object> model = new HashMap<>();
		model.put("text", text);
		model.put("messageSource", messageSource);
		model.put("locale", locale);

		return VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, templateLocation, EMAIL_CONTENT_ENCODING, model);
	}

	@Override
	public String getSubject(String code, Locale locale, Object... args) {
		return messageSource.getMessage(code, args, locale);
	}

}
