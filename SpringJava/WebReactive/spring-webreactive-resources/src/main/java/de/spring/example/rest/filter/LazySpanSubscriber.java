package de.spring.example.rest.filter;

import java.util.function.Supplier;

import org.reactivestreams.Subscription;

import reactor.util.context.Context;

final class LazySpanSubscriber<T> implements SpanSubscription<T> {

	private final Supplier<SpanSubscription<T>> supplier;

	LazySpanSubscriber(Supplier<SpanSubscription<T>> supplier) {
		this.supplier = supplier;
	}

	@Override public void onSubscribe(Subscription subscription) {
		this.supplier.get().onSubscribe(subscription);
	}

	@Override public void request(long n) {
		this.supplier.get().request(n);
	}

	@Override public void cancel() {
		this.supplier.get().cancel();
	}

	@Override public void onNext(T o) {
		this.supplier.get().onNext(o);
	}

	@Override public void onError(Throwable throwable) {
		this.supplier.get().onError(throwable);
	}

	@Override public void onComplete() {
		this.supplier.get().onComplete();
	}

	@Override public Context currentContext() {
		return this.supplier.get().currentContext();
	}
}

