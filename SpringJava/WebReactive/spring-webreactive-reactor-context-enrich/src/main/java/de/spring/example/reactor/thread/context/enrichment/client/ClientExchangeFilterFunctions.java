package de.spring.example.reactor.thread.context.enrichment.client;

import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import de.spring.example.reactor.thread.context.enrichment.ObjectContext;
import reactor.core.publisher.Mono;

public class ClientExchangeFilterFunctions {

	public static <T> ExchangeFilterFunction filter(Class<T> classContext) {

		return (request, next) -> {
			return Mono.subscriberContext().flatMap(context -> {
				if (context.hasKey(classContext)) {
					ObjectContext objectContext = (ObjectContext) context.get(classContext);
					request.headers().add(objectContext.getHeader(), objectContext.getValue());
				}
				return next.exchange(request);
			});
		};
	}

}
