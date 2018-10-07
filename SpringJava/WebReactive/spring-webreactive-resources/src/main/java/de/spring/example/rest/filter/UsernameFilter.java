package de.spring.example.rest.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import de.spring.example.context.UsernameContext;
import de.spring.example.reactor.thread.context.enrichment.subscriber.SubscriberContext;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Configuration
public class UsernameFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		if (!request.getHeaders().containsKey(UsernameContext.USERNAME_HEADER)) {
			return chain.filter(exchange);
		}

		String username = request.getHeaders().get(UsernameContext.USERNAME_HEADER).get(0);


		return SubscriberContext.contextSubscriber(context -> {

			Context updatedContext = context;
			if (!context.hasKey(UsernameContext.class)) {
				updatedContext = context.put(UsernameContext.class, new UsernameContext(username));
			}
			return updatedContext;
		}, exchange, chain);
	}

}
