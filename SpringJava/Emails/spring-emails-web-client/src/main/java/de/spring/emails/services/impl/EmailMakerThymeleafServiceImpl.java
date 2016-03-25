package de.spring.emails.services.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import de.spring.emails.services.EmailMakerService;

@Service("emailMakerThymeleafService")
public class EmailMakerThymeleafServiceImpl implements EmailMakerService {
	private final TemplateEngine templateEngine;
	private final MessageSource messageSource;

	@Autowired
	public EmailMakerThymeleafServiceImpl(TemplateEngine templateEngine, MessageSource messageSource) {
		this.templateEngine = templateEngine;
		this.messageSource = messageSource;
	}

	@Override
	public String emailMaker(Map<String, String> text, String templateLocation, Locale locale) {
		final Context ctx = new Context(locale);
		ctx.setVariable("name", "Gustavo Martin Morcuende");
		ctx.setVariable("subscriptionDate", new Date());
		ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
		ctx.setVariable("imageResourceName", "imageResourceName");
		
		return  this.templateEngine.process("email-inlineimage.html", ctx);
	}

	@Override
	public String getSubject(String code, Locale locale, Object... args) {
		return messageSource.getMessage(code, args, locale);
	}

}

