package de.spring.stomp.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

public class SessionConnectedListener implements ApplicationListener<SessionConnectedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionConnectedListener.class);

	@Override
	public void onApplicationEvent(SessionConnectedEvent event) {
		LOGGER.info("SessionConnectedEvent timestamp: " + event.getTimestamp());
		LOGGER.info("SessionConnectedEvent user: " + event.getUser());
		LOGGER.info("SessionConnectedEvent: " + event.toString());
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
	    StompCommand command = accessor.getCommand();
	    
	    LOGGER.info("SessionConnectedEvent, StompCommand: " + command);
	    LOGGER.info("SessionConnectedEvent, login: " + accessor.getLogin()); 
	    
	    long[] heartBeats = accessor.getHeartbeat();
	    for (long heartBeat : heartBeats) {
	    	LOGGER.info("SessionConnectedEvent, heartBeat: " + heartBeat);
	    }
	    
	    LOGGER.info("SessionConnectedEvent, destination: " + accessor.getDestination());
	    LOGGER.info("SessionConnectedEvent, host: " + accessor.getHost());
	    LOGGER.info("SessionConnectedEvent, message: " + accessor.getMessage());
	    LOGGER.info("SessionConnectedEvent, sessionId: " + accessor.getSessionId());
	    LOGGER.info("SessionConnectedEvent, subscriptionId: " + accessor.getSubscriptionId());
	    
	    
	    byte[] payload = (byte[])event.getMessage().getPayload();
	    String stringPayload = new String(payload);
	    LOGGER.info("SessionConnectedEvent, payload: " + stringPayload);
	}

}
