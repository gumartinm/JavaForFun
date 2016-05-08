package de.spring.stomp.services.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Service;

import de.spring.stomp.services.RestGreetingService;

@Service("restGreetingService")
public class RestGreetingServiceImpl
		implements RestGreetingService, ApplicationListener<BrokerAvailabilityEvent> {
	private final SimpMessagingTemplate template;
	
	private volatile boolean isBrokerAvailable = true;

    @Autowired
    public RestGreetingServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }

	@Override
	public void doGreetings(String greeting) {
		String text = "[" + LocalDateTime.now() + "]:" + greeting;
		
		if (isBrokerAvailable) {
			// STOMP clients subscribed to /topic/greeting will receive the data sent by the convertAndSend method.
			template.convertAndSend("/topic/greeting", text);
		}
	}

	@Override
	public void onApplicationEvent(BrokerAvailabilityEvent event) {
		// Components using the SimpMessagingTemplate should subscribe to this event
		// and avoid sending messages at times when the broker is not available.
		// In any case they should be prepared to handle MessageDeliveryException
		// when sending a message.
		isBrokerAvailable = event.isBrokerAvailable();
	}
}
