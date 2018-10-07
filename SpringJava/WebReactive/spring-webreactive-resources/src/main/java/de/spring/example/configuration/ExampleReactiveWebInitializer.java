package de.spring.example.configuration;


import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.server.adapter.AbstractReactiveWebInitializer;

import de.spring.example.persistence.configuration.PersistenceConfig;
import de.spring.example.reactor.thread.context.enrichment.configuration.ThreadContextEnrichmentAutoConfiguration;
import de.spring.example.rest.configuration.ResourcesConfig;
import de.spring.example.services.configuration.ServicesConfig;

/**
 * Instead of using {@code web.xml}, web applications can be also loaded in this way (it is the only way available for Spring Web Reactive)
 *
 * <h2>Steps when loading applications using the programmatically way: </h2>
 *
 * <p>
 * <b>1.</b> Tomcat: {@code web.xml} requires {@code metadata-complete="false"} <br>
 *    In this way Tomcat scans for Servlet 3.1+ annotations and classes.
 * </p>
 * <p>
 * <b>2.</b> Spring implements {@link org.springframework.web.SpringServletContainerInitializer} which is annotated with {@code @HandlesTypes(WebApplicationInitializer.class)}
 *    and implements {@link javax.servlet.ServletContainerInitializer} <br>
 *    {@code @HandlesTypes} and {@code ServletContainerInitializer} are used by Tomcat for starting our application (instead of using {@code web.xml})
 * </p>
 * <p>
 * <b>3.</b> Tomcat runs {@link org.springframework.web.SpringServletContainerInitializer#onStartup}
 * </p>
 * <p>
 * <b>4.</b> {@code SpringServletContainerInitializer} searches for classes implementing {@link org.springframework.web.WebApplicationInitializer} and run them.
 * </p>
 * <p>
 * <b>5.</b> {@code ExampleReactiveWebInitializer} implements {@link org.springframework.web.server.adapter.AbstractReactiveWebInitializer} which implements {@code WebApplicationInitializer.}
 * </p>
 *
 *
 * <h2>Problem:</h2>
 * <p>
 * Using one parent/root Spring context for loading services and persistence does not seem cool anymore. Now, everything goes in the same Sping context :( <br>
 * Even if we have something called {@link org.springframework.web.context.AbstractContextLoaderInitializer}, Spring by default, does not allow us to connect
 * the root context (which would be implemented by {@code AbstractContextLoaderInitializer}) with the child context (implemented by {@code ExampleReactiveWebInitializer})
 * </p>
 *
 */
public class ExampleReactiveWebInitializer extends AbstractReactiveWebInitializer {
	public static final String DEFAULT_SERVLET_NAME = "spring-webreactive";
	
	
	@Override
	protected Class<?>[] getConfigClasses() {
		return new Class<?>[] { DelegatingWebFluxConfiguration.class,
								ResourcesConfig.class,
								ServicesConfig.class,
								PersistenceConfig.class,
								ThreadContextEnrichmentAutoConfiguration.class};
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
