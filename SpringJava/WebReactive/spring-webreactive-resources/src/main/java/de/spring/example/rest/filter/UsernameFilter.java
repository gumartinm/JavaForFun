package de.spring.example.rest.filter;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import de.spring.example.context.UsernameContext;
import de.spring.example.context.UsernameThreadContext;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

/**
 * Based on https://github.com/spring-cloud/spring-cloud-sleuth/blob/c159949484c580182631845cabe705a880215159/spring-cloud-sleuth-core/src/main/java/org/springframework/cloud/sleuth/instrument/web/TraceWebFilter.java
 *
 */
public class UsernameFilter implements WebFilter, Ordered {
	private static final Logger LOGGER = LoggerFactory.getLogger(UsernameFilter.class);
	
	public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 5;
	
	@Override
	public int getOrder() {
		return ORDER;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		
		ServerHttpRequest request = exchange.getRequest();
		
		if (!request.getHeaders().containsKey(UsernameThreadContext.USERNAME_HEADER)) {
			return null;
		}
		
		String username = request.getHeaders().get(UsernameThreadContext.USERNAME_HEADER).get(0);
		
		
		ServerHttpResponse response = exchange.getResponse();
		String uri = request.getPath().pathWithinApplication().value();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Received a request to uri [" + uri + "]");
		}
		UsernameContext spanFromAttribute = getSpanFromAttribute(exchange);
		final String CONTEXT_ERROR = "username.filter.context.error";
		return chain
				.filter(exchange)
				.compose(f -> f.then(Mono.subscriberContext())
						.onErrorResume(t -> Mono.subscriberContext()
								.map(c -> c.put(CONTEXT_ERROR, t)))
						.flatMap(c -> {
							//reactivate span from context
							UsernameContext usernameContext = spanFromContext(c);
							Mono<Void> continuation;
							Throwable t = null;
							if (c.hasKey(CONTEXT_ERROR)) {
								t = c.get(CONTEXT_ERROR);
								continuation = Mono.error(t);
							} else {
								continuation = Mono.empty();
							}
							Object attribute = exchange
									.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
							return continuation;
						})
						.subscriberContext(c -> {
							UsernameContext span = null;
							if (c.hasKey(UsernameContext.class)) {
								UsernameContext parent = c.get(UsernameContext.class);
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug("Found span in reactor context" + span);
								}
							} else {
								if (spanFromAttribute != null) {
									span = spanFromAttribute;
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("Found span in attribute " + span);
									}
								} else {

									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("Not found span " + span);
									}
								}
								exchange.getAttributes().put(UsernameThreadContext.USERNAME_HEADER, span);
							}
							return c.put(UsernameContext.class, span);
						}));
	}

    private UsernameContext spanFromContext(Context c) {
    	UsernameContext usernameContext = null;
    	
        if (!c.hasKey(UsernameContext.class)) {
            if (LOGGER.isDebugEnabled()) {
            	LOGGER.debug("No username found in context.");
            }
        }

    	usernameContext = c.get(UsernameContext.class);
        if (LOGGER.isDebugEnabled()) {
        	LOGGER.debug("Found username in context " + usernameContext.getUsername());
        }
        return usernameContext;
    }

    private UsernameContext getSpanFromAttribute(ServerWebExchange exchange) {
        return exchange.getAttribute(UsernameThreadContext.USERNAME_HEADER);
    }
  

//	private static <T> Subscriber<Signal<T>> logOnNext(Subscriber<T> logStatement) {
//		  return signal -> {
//			  if (!signal.isOnNext()) return null;
//			    Optional<String> apiIDMaybe = signal.getContext().getOrEmpty("apiID");
//
//			    apiIDMaybe.ifPresentOrElse(apiID -> {
//				  try (MDC.MDCCloseable closeable = MDC.putCloseable("apiID", apiID)) {
//					  logStatement.accept(signal.get());
//				  }
//			  }, () -> logStatement.accept(signal.get()));
//		  }
//		}	

}
