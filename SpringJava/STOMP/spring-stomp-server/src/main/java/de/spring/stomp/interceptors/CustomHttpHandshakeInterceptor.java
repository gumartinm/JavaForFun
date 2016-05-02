package de.spring.stomp.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class CustomHttpHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomHttpHandshakeInterceptor.class);


	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Exception ex) {
		super.afterHandshake(request, response, wsHandler, ex);
		
		LOGGER.info("Request URI:" + request.getURI());
		LOGGER.info("Request remote address:" + request.getRemoteAddress());
		LOGGER.info("Request local address:" + request.getLocalAddress());
		LOGGER.info("Request headers:" + request.getHeaders());
		
		LOGGER.info("Response headers:" + response.getHeaders());		
	}
}
