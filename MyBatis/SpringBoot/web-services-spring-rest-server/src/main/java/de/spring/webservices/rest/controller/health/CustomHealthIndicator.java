package de.spring.webservices.rest.controller.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

public class CustomHealthIndicator extends AbstractHealthIndicator {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomHealthIndicator.class);
	
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		if (isEveryThingOK()) {
			builder.up();
		}
		else {
			LOGGER.warn(String.format(
					"CustomHealthIndicator: Free disk space below threshold. "
							+ "Available: %d bytes (threshold: %d bytes)",
					10L, 10L));
			builder.down();
		}
		builder.withDetail("CustomHealthIndicator some value", 20L)
				.withDetail("CustomHealthIndicator another value", 100L);
	}

	private boolean isEveryThingOK() {
		return true;
	}
}
