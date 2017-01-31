package de.example.spring.kafka;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Sender.class, TestSupportBinderAutoConfiguration.class })
@DirtiesContext
public class SenderIntegrationTest {

	@Inject
	Source source;
	
	@Inject
	Sender sender;
	
	@Inject
	private MessageCollector messageCollector;
	
	@Test
	public void sendSomeProduct() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Product expected = new Product("hello", "this is some description");
		
		sender.sendMessage("hello");
		
		Message<String> received = (Message<String>) messageCollector.forChannel(source.output()).poll();
		Product receivedProduct = objectMapper.readValue(received.getPayload().toString(), Product.class);
		
	    assertThat(receivedProduct.getDescription(), is(expected.getDescription()));
	    assertThat(receivedProduct.getName(), is(expected.getName()));

	}

}
