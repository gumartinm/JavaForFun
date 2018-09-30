package de.spring.example.rest.filter;

import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.BeanFactory;

import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

public abstract class ReactorSleuth {

	public static <T> Function<? super Publisher<T>, ? extends Publisher<T>> scopePassingSpanOperator(BeanFactory beanFactory) {
		return sourcePub -> {
			// TODO: Remove this once Reactor 3.1.8 is released
			// do the checks directly on actual original Publisher
			if (sourcePub instanceof ConnectableFlux // Operators.lift can't handle that
					|| sourcePub instanceof GroupedFlux // Operators.lift can't handle that
			) {
				return sourcePub;
			}
			// no more POINTCUT_FILTER since mechanism is broken
			Function<? super Publisher<T>, ? extends Publisher<T>> lift = Operators.lift((scannable, sub) -> {
				// rest of the logic unchanged...
				return new LazySpanSubscriber<T>(scopePassingSpanSubscription(beanFactory, scannable, sub));
			});

			return lift.apply(sourcePub);
		};
	}


	private static <T> SpanSubscriptionProvider<T> scopePassingSpanSubscription(BeanFactory beanFactory,
			Scannable scannable, CoreSubscriber<? super T> sub) {
		return new SpanSubscriptionProvider<T>(beanFactory, sub, sub.currentContext(), scannable.name()) {

			@Override
			public SpanSubscription newCoreSubscriber() {
				return new ScopePassingSpanSubscriber<T>(sub, sub != null ? sub.currentContext() : Context.empty());
			}
		};
	}

	private ReactorSleuth() {
	}
}