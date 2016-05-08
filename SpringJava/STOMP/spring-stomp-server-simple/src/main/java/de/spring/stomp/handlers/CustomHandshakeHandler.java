package de.spring.stomp.handlers;

import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * In some cases it may be useful to assign an identity to a WebSocket session even when
 * the user has not been formally authenticated. For example, a mobile app might assign some
 * identity to anonymous users, perhaps based on geographical location. The do that currently,
 * an application can sub-class DefaultHandshakeHandler and override the determineUser method.
 * The custom handshake handler can then be plugged in (see examples in
 * Section 25.2.4, “Deployment Considerations”)
 */
public class CustomHandshakeHandler extends DefaultHandshakeHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomHandshakeHandler.class);

	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {

		Principal principal = request.getPrincipal();
		if (principal != null) {
			LOGGER.info("CustomHandshakeHandler: " + principal.getName());
		}
		
		return super.determineUser(request, wsHandler, attributes);		
	}

}
