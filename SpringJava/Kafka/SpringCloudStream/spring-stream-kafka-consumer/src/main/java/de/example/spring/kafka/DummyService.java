package de.example.spring.kafka;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DummyService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DummyService.class);


	public void iAmVeryDummy(String message) {
		LOGGER.info("I am a dummy service: '{}'", message);
	}
}
