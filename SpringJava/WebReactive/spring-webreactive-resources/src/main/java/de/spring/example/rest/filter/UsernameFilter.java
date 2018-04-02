package de.spring.example.rest.filter;

import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import de.spring.example.context.UsernameContext;
import de.spring.example.context.UsernameThreadContext;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;


public class UsernameFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		
		if (!request.getHeaders().containsKey(UsernameThreadContext.USERNAME_HEADER)) {
            return chain.filter(exchange);
		}
		
		String username = request.getHeaders().get(UsernameThreadContext.USERNAME_HEADER).get(0);
		return chain
				.filter(exchange)
                .compose(function -> function
                        .then(Mono.subscriberContext())
                        .doOnSubscribe(onSubscribe -> {
                            MDC.put(UsernameThreadContext.USERNAME_HEADER, username);
                        })
                        .doOnError(throwable -> {
                            MDC.put(UsernameThreadContext.USERNAME_HEADER, username);
                        })
                        .onErrorMap(throwable -> {
                            MDC.put(UsernameThreadContext.USERNAME_HEADER, username);
                            return throwable;
                        })
                        .doFinally(onFinally -> {
                            MDC.remove(UsernameThreadContext.USERNAME_HEADER);
                        })
                        .flatMap(context -> {
                            Mono<Void> continuation = Mono.empty();
                            return continuation;
                        })
						.subscriberContext(context -> {
                            Context updatedContext = context;
							if (!context.hasKey(UsernameContext.class)) {
                                updatedContext = context.put(UsernameContext.class, new UsernameContext(username));
							}

                            return updatedContext;
						})
					);
	}
}
