package de.spring.example.configuration;

import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.server.adapter.AbstractReactiveWebInitializer;

import de.spring.example.persistence.configuration.PersistenceConfig;
import de.spring.example.rest.configuration.ResourcesConfig;
import de.spring.example.services.configuration.ServicesConfig;

public class ExampleReactiveWebInitializer extends AbstractReactiveWebInitializer {
	public static final String DEFAULT_SERVLET_NAME = "spring-webreactive";
	
	
	@Override
	protected Class<?>[] getConfigClasses() {
		return new Class<?>[] { DelegatingWebFluxConfiguration.class,
								ResourcesConfig.class,
								ServicesConfig.class,
								PersistenceConfig.class};
	}
	
	@Override
	protected String getServletName() {
		return DEFAULT_SERVLET_NAME;
	}

	@Override
	protected String getServletMapping() {
		return "/";
	}
}
