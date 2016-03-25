package de.spring.emails.services;

import java.util.Locale;
import java.util.Map;

public interface EmailMakerService {

	public String emailMaker(Map<String, String> text, String templateLocation, Locale locale);
	
	public String getSubject(String code, Locale locale, Object... args);
}
