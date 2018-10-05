package de.spring.example.rest.filter;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.MDC;

import de.spring.example.context.ThreadContext;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

public class ThreadContextCoreSubscriber<T> implements Subscription, CoreSubscriber<T> {
	private final Context context;
	private final Subscriber<? super T> subscriber;

	private Subscription subscription;

	public ThreadContextCoreSubscriber(Subscriber<? super T> subscriber, Context parentContext) {
		this.context = parentContext != null ? fillNewContext(parentContext) : Context.empty();
		this.subscriber = subscriber;
	}

	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		fillSubscriberMDC(subscriber -> subscriber.onSubscribe(this));
	}

	@Override
	public void request(long n) {
		fillSubscriptionMDC(subscription -> subscription.request(n));
	}

	@Override
	public void cancel() {
		fillSubscriptionMDC(subscription -> subscription.cancel());
	}

	@Override
	public void onNext(T o) {
		fillSubscriberMDC(subscriber -> subscriber.onNext(o));
	}

	@Override
	public void onError(Throwable throwable) {
		fillSubscriberMDC(subscriber -> subscriber.onError(throwable));
	}

	@Override
	public void onComplete() {
		fillSubscriberMDC(subscriber -> subscriber.onComplete());
	}

	@Override
	public Context currentContext() {
		return this.context;
	}

	private void fillSubscriberMDC(Consumer<Subscriber<? super T>> function) {
		try {
			this.context.stream().forEach(entry -> {
				ThreadContext threadContext = (ThreadContext) entry.getValue();
				MDC.put(threadContext.getHeader(), threadContext.getValue());
			});

			function.accept(this.subscriber);
		} finally {
			this.context.stream().forEach(entry -> {
				ThreadContext threadContext = (ThreadContext) entry.getValue();
				MDC.remove(threadContext.getHeader());
			});
		}
	}

	private void fillSubscriptionMDC(Consumer<Subscription> function) {
		try {
			this.context.stream().forEach(entry -> {
				ThreadContext threadContext = (ThreadContext) entry.getValue();
				MDC.put(threadContext.getHeader(), threadContext.getValue());
			});

			function.accept(this.subscription);
		} finally {
			this.context.stream().forEach(entry -> {
				ThreadContext threadContext = (ThreadContext) entry.getValue();
				MDC.remove(threadContext.getHeader());
			});
		}
	}

	private Context fillNewContext(Context parentContext) {
		Context newContext = Context.empty();

		Iterator<Map.Entry<Object, Object>> iterator = parentContext.stream().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Object, Object> entry = iterator.next();
			ThreadContext threadContext = (ThreadContext) entry.getValue();
			newContext = newContext.put(threadContext.getClass(), threadContext);
		}

		return newContext;
	}
}
