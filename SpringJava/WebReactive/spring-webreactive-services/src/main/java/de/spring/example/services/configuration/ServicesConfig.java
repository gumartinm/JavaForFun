package de.spring.example.services.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import de.spring.example.context.UsernameContext;
import de.spring.example.reactor.thread.context.enrichment.client.ClientExchangeFilterFunctions;
import de.spring.example.services.AdClientService;
import de.spring.example.services.impl.AdClientServiceImpl;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

@Configuration
@PropertySource("classpath:services.properties")
@ComponentScan({ "de.spring.example.services", "org.resthub.common.service" })
public class ServicesConfig {
	@Value("${app.uri.host}")
	private String uriHost;

	@Value("${app.uri.host.read-timeout}")
	private Integer readTimeOut;

	@Value("${app.uri.host.write-timeout}")
	private Integer writeTimeout;

	@Value("${app.uri.host.connection-timeout}")
	private Integer connectionTimeOut;

	@Bean
	public AdClientService adClientService(WebClient.Builder webClientBuilder) {
		WebClient webClient = webClientBuilder.build();
		return new AdClientServiceImpl(uriHost, webClient);
	}

	@Bean
	public WebClient.Builder webClientBuilder() {
		ClientHttpConnector connector = new ReactorClientHttpConnector(options -> {
			options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeOut)
				   .onChannelInit(channel -> {
					   channel.pipeline().addLast(new ReadTimeoutHandler(readTimeOut, TimeUnit.MILLISECONDS));
					   channel.pipeline().addLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS));
					   return true;
				   });
			});

		WebClient.Builder webClientBuilder = WebClient.builder();
		return webClientBuilder
		        .filter(ClientExchangeFilterFunctions.filter(UsernameContext.class))
				.clientConnector(connector);
	}
}
