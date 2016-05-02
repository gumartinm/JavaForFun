package de.spring.stomp.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;

public class BrokerAvailabilityListener implements ApplicationListener<BrokerAvailabilityEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BrokerAvailabilityListener.class);

	@Override
	public void onApplicationEvent(BrokerAvailabilityEvent event) {
		
		LOGGER.info("BrokerAvailabilityEvent timestamp: " + event.getTimestamp());
		LOGGER.info("BrokerAvailabilityEvent brokerAvailable: " + event.isBrokerAvailable());
		LOGGER.info("BrokerAvailabilityEvent: " + event.toString());
	}

}
