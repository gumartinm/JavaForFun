package de.spring.stomp.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionConnectEvent;

public class SessionConnectListener implements ApplicationListener<SessionConnectEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionConnectListener.class);
	
	@Override
	public void onApplicationEvent(SessionConnectEvent event) {
		LOGGER.info("SessionConnectEvent timestamp: " + event.getTimestamp());
		LOGGER.info("SessionConnectEvent user: " + event.getUser());
		LOGGER.info("SessionConnectEvent: " + event.toString());
	}

}
