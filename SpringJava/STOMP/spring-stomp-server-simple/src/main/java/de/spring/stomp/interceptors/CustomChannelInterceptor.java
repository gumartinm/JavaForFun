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
	    
	    long[] heartBeats = accessor.getHeartbeat();
	    for (long heartBeat : heartBeats) {
	    	LOGGER.info("CustomChannelInterceptor preSend, heartBeat: " + heartBeat);
	    }
	    
	    LOGGER.info("CustomChannelInterceptor preSend, destination: " + accessor.getDestination());
	    LOGGER.info("CustomChannelInterceptor preSend, host: " + accessor.getHost());
	    LOGGER.info("CustomChannelInterceptor preSend, message: " + accessor.getMessage());
	    LOGGER.info("CustomChannelInterceptor preSend, sessionId: " + accessor.getSessionId());
	    
	    
	    byte[] payload = (byte[])message.getPayload();
	    String stringPayload = new String(payload);
	    LOGGER.info("CustomChannelInterceptor preSend, payload: " + stringPayload);
	    
	    return message;
	  }
	  
	  @Override
	  public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
	    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
	    StompCommand command = accessor.getCommand();

	    LOGGER.info("CustomChannelInterceptor postSend, StompCommand: " + command);
	    LOGGER.info("CustomChannelInterceptor postSend, login: " + accessor.getLogin());
	    
	    long[] heartBeats = accessor.getHeartbeat();
	    for (long heartBeat : heartBeats) {
	    	LOGGER.info("CustomChannelInterceptor postSend, heartBeat: " + heartBeat);
	    }
	    
	    LOGGER.info("CustomChannelInterceptor postSend, destination: " + accessor.getDestination());
	    LOGGER.info("CustomChannelInterceptor postSend, host: " + accessor.getHost());
	    LOGGER.info("CustomChannelInterceptor postSend, message: " + accessor.getMessage());
	    LOGGER.info("CustomChannelInterceptor postSend, sessionId: " + accessor.getSessionId());
	    
	    byte[] payload = (byte[])message.getPayload();
	    String stringPayload = new String(payload);
	    LOGGER.info("CustomChannelInterceptor preSend, payload: " + stringPayload);
	    
	  }

}
