package de.spring.stomp.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public class SessionDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionUnsubscribeListener.class);

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		LOGGER.info("SessionDisconnectEvent timestamp: " + event.getTimestamp());
		LOGGER.info("SessionDisconnectEvent user: " + event.getUser());
		LOGGER.info("SessionDisconnectEvent sessionId: " + event.getSessionId());
		LOGGER.info("SessionDisconnectEvent close status: " + event.getCloseStatus());
		LOGGER.info("SessionDisconnectEvent: " + event.toString());
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
	    StompCommand command = accessor.getCommand();
	    
	    LOGGER.info("SessionDisconnectEvent, StompCommand: " + command);
	    LOGGER.info("SessionDisconnectEvent, login: " + accessor.getLogin()); 
	    
	    long[] heartBeats = accessor.getHeartbeat();
	    for (long heartBeat : heartBeats) {
	    	LOGGER.info("SessionDisconnectEvent, heartBeat: " + heartBeat);
	    }
	    
	    LOGGER.info("SessionDisconnectEvent, destination: " + accessor.getDestination());
	    LOGGER.info("SessionDisconnectEvent, host: " + accessor.getHost());
	    LOGGER.info("SessionDisconnectEvent, message: " + accessor.getMessage());
	    LOGGER.info("SessionDisconnectEvent, sessionId: " + accessor.getSessionId());
	    LOGGER.info("SessionDisconnectEvent, subscriptionId: " + accessor.getSubscriptionId());
	    
	    byte[] payload = (byte[])event.getMessage().getPayload();
	    String stringPayload = new String(payload);
	    LOGGER.info("SessionDisconnectEvent, payload: " + stringPayload);
	}

}
