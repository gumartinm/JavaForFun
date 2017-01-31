package de.example.spring.kafka;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Sender.class })
@DirtiesContext
public class SenderIntegrationTest {

	@Inject
	Source source;
	
	@Inject
	Sender sender;
	
	@Inject
	private MessageCollector messageCollector;
	
	@Test
	public void sendSomeProduct() {
		Product product = new Product("hello", "this is some description");
		
		sender.sendMessage("hello");
		
		Message<Product> received = (Message<Product>) messageCollector.forChannel(source.output()).poll();
		
	    assertThat(received.getPayload().getDescription(), is(product.getDescription()));
	}

}
