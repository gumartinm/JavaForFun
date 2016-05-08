package de.spring.stomp.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
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
	}

}
