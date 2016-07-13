package de.spring.stomp.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;

public class SessionConnectListener implements ApplicationListener<SessionConnectEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionConnectListener.class);
	
	@Override
	public void onApplicationEvent(SessionConnectEvent event) {		
		LOGGER.info("SessionConnectEvent timestamp: " + event.getTimestamp());
		LOGGER.info("SessionConnectEvent user: " + event.getUser());
		LOGGER.info("SessionConnectEvent: " + event.toString());
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
	    StompCommand command = accessor.getCommand();
	    
	    LOGGER.info("SessionConnectEvent, StompCommand: " + command);
	    LOGGER.info("SessionConnectEvent, login: " + accessor.getLogin()); 
	    
	    long[] heartBeats = accessor.getHeartbeat();
	    for (long heartBeat : heartBeats) {
	    	LOGGER.info("SessionConnectEvent, heartBeat: " + heartBeat);
	    }
	    
	    LOGGER.info("SessionConnectEvent, destination: " + accessor.getDestination());
	    LOGGER.info("SessionConnectEvent, host: " + accessor.getHost());
	    LOGGER.info("SessionConnectEvent, message: " + accessor.getMessage());
	    LOGGER.info("SessionConnectEvent, sessionId: " + accessor.getSessionId());
	    LOGGER.info("SessionConnectEvent, subscriptionId: " + accessor.getSubscriptionId());
	    
	    byte[] payload = (byte[])event.getMessage().getPayload();
	    String stringPayload = new String(payload);
	    LOGGER.info("SessionConnectEvent, payload: " + stringPayload);
	}

}
