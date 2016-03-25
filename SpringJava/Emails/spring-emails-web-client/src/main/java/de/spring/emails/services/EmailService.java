package de.spring.emails.services;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.core.io.Resource;

public interface EmailService {

	/**
	 * This method sends mails.
	 * 
	 * @param to the email destination. Required parameter.
	 * @param subject the email subject. Required parameter.
	 * @param text the email text. Required parameter.
	 * @param isHtml if true email is HTML type, otherwise false.
	 * @param attachments file attachments. Optional parameter.
	 * @param inline inline content in mail. Optional parameter.
	 * @throws MessagingException in case of any error.
	 */
	public void sendEmail(String[] to, String subject, String text, boolean isHtml,
			List<Resource> attachments, Map<String, Resource> inline) throws MessagingException;
	
	/**
	 * This method sends mails. It is asynchronous, what means, this method always returns before sending the email.
	 * 
	 * @param to the email destination. Required parameter.
	 * @param subject the email subject. Required parameter.
	 * @param text the email text. Required parameter.
	 * @param isHtml if true email is HTML type, otherwise false.
	 * @param attachments file attachments. Optional parameters.
	 * @param inline inline content in mail. Optional parameter.
	 * @throws MessagingException in case of any error.
	 */
	public void sendEmailAsync(String[] to, String subject, String text, boolean isHtml,
			List<Resource> attachments, Map<String, Resource> inline) throws MessagingException;	
}