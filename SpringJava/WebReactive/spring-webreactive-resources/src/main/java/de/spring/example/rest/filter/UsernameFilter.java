package de.spring.example.rest.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import de.spring.example.context.UsernameContext;
import de.spring.example.context.UsernameThreadContext;
import reactor.core.publisher.Mono;

@Configuration
public class UsernameFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		if (!request.getHeaders().containsKey(UsernameThreadContext.USERNAME_HEADER)) {
			return chain.filter(exchange);
		}

		String username = request.getHeaders().get(UsernameThreadContext.USERNAME_HEADER).get(0);

		return SubscriberContext
				.contextSubscriber(
						context -> context.put(UsernameContext.class, new UsernameContext(username)),
						exchange,
						chain);
	}

}
