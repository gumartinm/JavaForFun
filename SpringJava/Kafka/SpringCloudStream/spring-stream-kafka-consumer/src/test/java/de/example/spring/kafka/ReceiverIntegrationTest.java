package de.example.spring.kafka;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Receiver.class, TestSupportBinderAutoConfiguration.class })
@DirtiesContext
public class ReceiverIntegrationTest {

	@Inject
	Sink source;
	
	@MockBean
	DummyService dummyService;
	
	@Test
	public void callSomeDummy() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String productName = "product";
		String productDescription = "productDescription";
		Product product = new Product(productName, productDescription);
	    ArgumentCaptor<String> dummyArgCaptor = ArgumentCaptor.forClass(String.class);
		doNothing().when(dummyService).iAmVeryDummy(dummyArgCaptor.capture());
		
		
	    Message<String> message = MessageBuilder
	    							.withPayload(objectMapper.writeValueAsString(product))
	    							.build();
	    source
	    	.input()
	    	.send(message);

	    assertThat(dummyArgCaptor.getValue(), is(product.getName()));
	}

}
