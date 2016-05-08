package de.spring.stomp.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

public class SessionConnectedListener implements ApplicationListener<SessionConnectedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionConnectedListener.class);

	@Override
	public void onApplicationEvent(SessionConnectedEvent event) {
		LOGGER.info("SessionConnectedEvent timestamp: " + event.getTimestamp());
		LOGGER.info("SessionConnectedEvent user: " + event.getUser());
		LOGGER.info("SessionConnectedEvent: " + event.toString());
	}

}
