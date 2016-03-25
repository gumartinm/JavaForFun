package de.spring.emails.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import de.spring.emails.services.impl.EmailServiceImpl;


public class EmailServiceTest {
	private static final String TO = "noemail@gumartinm.name";
	private static final String SUBJECT = "Email test";
	private static final String TEXT = "Some text in some email";
	
	private GreenMail testSmtp;
	private EmailService emailService;
	
	@Before
	public void setUp() throws Exception {
		JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
		senderImpl.setPort(3025);
		senderImpl.setHost("localhost");
		emailService = new EmailServiceImpl(senderImpl);
		
		testSmtp = new GreenMail(ServerSetupTest.SMTP);
        testSmtp.start();
	}
	
    @After
    public void cleanup(){
        testSmtp.stop();
    }

	@Test
	public void whenSendEmailWithSuccesThenEmailArrivesToServer() throws MessagingException, IOException {
		String[] to = { TO };
		
		emailService.sendEmail(to, SUBJECT, TEXT, true, null, null);
		
		Message[] messages = testSmtp.getReceivedMessages();
	    assertEquals(1, messages.length);
	    assertEquals(SUBJECT, messages[0].getSubject());
	    MimeMultipart mp = (MimeMultipart) messages[0].getContent();
	    String body = GreenMailUtil.getBody(mp.getBodyPart(0)).trim();
	    assertThat(body, CoreMatchers.containsString(TEXT));
	}
	
	@Test
	public void whenSendEmailAsyncWithSuccesThenEmailArrivesToServer() throws MessagingException, IOException {
		String[] to = { TO };
		
		emailService.sendEmailAsync(to, SUBJECT, TEXT, true, null, null);
		
		Message[] messages = testSmtp.getReceivedMessages();
	    assertEquals(1, messages.length);
	    assertEquals(SUBJECT, messages[0].getSubject());
	    MimeMultipart mp = (MimeMultipart) messages[0].getContent();
	    String body = GreenMailUtil.getBody(mp.getBodyPart(0)).trim();
	    assertThat(body, CoreMatchers.containsString(TEXT));
	}

}
