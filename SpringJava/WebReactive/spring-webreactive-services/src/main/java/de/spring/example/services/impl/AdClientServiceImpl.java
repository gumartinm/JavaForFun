package de.spring.example.services.impl;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import de.spring.example.persistence.domain.Ad;
import de.spring.example.services.AdClientService;
import reactor.core.publisher.Flux;

public class AdClientServiceImpl implements AdClientService {
	private static final String URI_PATH_FIND_ALL = "/ads/?page=no";
	
	private final WebClient webClient;
	private final String uriFindAll;
	
	public AdClientServiceImpl(String uriHost, WebClient webClient) {
		this.webClient = webClient;
		this.uriFindAll = uriHost + URI_PATH_FIND_ALL;
	}

	@Override
	public Flux<Ad> findAll() {	    
		return webClient.get()
				.uri(this.uriFindAll).accept(MediaType.APPLICATION_JSON_UTF8)
				.exchange()
				.flatMapMany(clientResponse -> clientResponse.bodyToFlux(Ad.class));
	}
}
