package de.spring.stomp.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

public class SessionSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionSubscribeListener.class);

	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		LOGGER.info("SessionSubscribeEvent timestamp: " + event.getTimestamp());
		LOGGER.info("SessionSubscribeEvent user: " + event.getUser());
		LOGGER.info("SessionSubscribeEvent: " + event.toString());
	}

}
