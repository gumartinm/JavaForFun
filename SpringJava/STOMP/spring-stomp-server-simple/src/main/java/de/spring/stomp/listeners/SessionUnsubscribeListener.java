package de.spring.stomp.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

public class SessionUnsubscribeListener implements ApplicationListener<SessionUnsubscribeEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionUnsubscribeListener.class);

	@Override
	public void onApplicationEvent(SessionUnsubscribeEvent event) {
		LOGGER.info("SessionUnsubscribeEvent timestamp: " + event.getTimestamp());
		LOGGER.info("SessionUnsubscribeEvent user: " + event.getUser());
		LOGGER.info("SessionUnsubscribeEvent: " + event.toString());
	}

}
