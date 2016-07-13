package de.spring.stomp.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

public class SessionSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionSubscribeListener.class);

	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		LOGGER.info("SessionSubscribeEvent timestamp: " + event.getTimestamp());
		LOGGER.info("SessionSubscribeEvent user: " + event.getUser());
		LOGGER.info("SessionSubscribeEvent: " + event.toString());
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
	    StompCommand command = accessor.getCommand();
	    
	    LOGGER.info("SessionSubscribeEvent, StompCommand: " + command);
	    LOGGER.info("SessionSubscribeEvent, login: " + accessor.getLogin());
	    
	    long[] heartBeats = accessor.getHeartbeat();
	    for (long heartBeat : heartBeats) {
	    	LOGGER.info("CustomChannelInterceptor preSend, heartBeat: " + heartBeat);
	    }
	    
	    LOGGER.info("SessionSubscribeEvent, destination: " + accessor.getDestination());
	    LOGGER.info("SessionSubscribeEvent, host: " + accessor.getHost());
	    LOGGER.info("SessionSubscribeEvent, message: " + accessor.getMessage());
	    LOGGER.info("SessionSubscribeEvent, sessionId: " + accessor.getSessionId());
	    LOGGER.info("SessionSubscribeEvent, subscriptionId: " + accessor.getSubscriptionId());
	    
	    byte[] payload = (byte[])event.getMessage().getPayload();
	    String stringPayload = new String(payload);
	    LOGGER.info("SessionSubscribeEvent, payload: " + stringPayload);
	}

}
