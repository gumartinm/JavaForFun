package de.spring.stomp.services.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Service;

import de.spring.stomp.services.UserTradeService;

@Service("userTradeService")
public class UserTradeServiceImpl
		implements UserTradeService, ApplicationListener<BrokerAvailabilityEvent> {
	private final SimpMessagingTemplate template;
	
	private volatile boolean isBrokerAvailable = true;

    @Autowired
    public UserTradeServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }
    
	@Override
	public void doTrade(String user) {
		String text = "[" + LocalDateTime.now() + "]:" + user;
		
		if (isBrokerAvailable) {
			// STOMP clients subscribed to /topic/position-updates will receive the data sent by the convertAndSend method.
			template.convertAndSendToUser(user, "/topic/position-updates", text);
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
