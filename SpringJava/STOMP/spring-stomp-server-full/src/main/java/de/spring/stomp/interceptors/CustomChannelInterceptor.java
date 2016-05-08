package de.spring.stomp.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class CustomChannelInterceptor extends ChannelInterceptorAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomChannelInterceptor.class);

	  @Override
	  public Message<?> preSend(Message<?> message, MessageChannel channel) {
	    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
	    StompCommand command = accessor.getCommand();

	    LOGGER.info("CustomChannelInterceptor preSend, StompCommand: " + command);
	    LOGGER.info("CustomChannelInterceptor preSend, login: " + accessor.getLogin());
	    LOGGER.info("CustomChannelInterceptor preSend, heartBeat: " + accessor.getHeartbeat());
	    LOGGER.info("CustomChannelInterceptor preSend, destination: " + accessor.getDestination());
	    LOGGER.info("CustomChannelInterceptor preSend, host: " + accessor.getHost());
	    
	    return message;
	  }
	  
	  @Override
	  public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
	    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
	    StompCommand command = accessor.getCommand();

	    LOGGER.info("CustomChannelInterceptor postSend, StompCommand: " + command);
	    LOGGER.info("CustomChannelInterceptor postSend, login: " + accessor.getLogin());
	    LOGGER.info("CustomChannelInterceptor postSend, heartBeat: " + accessor.getHeartbeat());
	    LOGGER.info("CustomChannelInterceptor postSend, destination: " + accessor.getDestination());
	    LOGGER.info("CustomChannelInterceptor postSend, host: " + accessor.getHost());
	    
	  }
}
