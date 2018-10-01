package de.spring.example.rest.filter;

import java.util.function.Consumer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import de.spring.example.context.UsernameContext;
import de.spring.example.context.UsernameThreadContext;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

public class ThreadContextCoreSubscriber<T> implements Subscription, CoreSubscriber<T> {
	private final UsernameContext usernameContext;
	private final Context context;
	private final Subscriber<? super T> subscriber;

	private Subscription subscription;

	public ThreadContextCoreSubscriber(Subscriber<? super T> subscriber, Context ctx) {
		UsernameContext userNameContextParent = ctx != null ? ctx.getOrDefault(UsernameContext.class, null) : null;
		this.usernameContext = userNameContextParent;
		this.context = ctx != null && userNameContextParent != null
		        ? ctx.put(UsernameContext.class, userNameContextParent)
		        : ctx != null ? ctx : Context.empty();
		this.subscriber = subscriber;
	}

	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		fillContext(subscriber -> subscriber.onSubscribe(this));
	}

	@Override
	public void request(long n) {
		try {
			UsernameThreadContext.setUsernameContext(usernameContext);
			this.subscription.request(n);
		} finally {
			UsernameThreadContext.clearUsernameContext();
		}
	}

	@Override
	public void cancel() {
		try {
			UsernameThreadContext.setUsernameContext(usernameContext);
			this.subscription.cancel();
		} finally {
			UsernameThreadContext.clearUsernameContext();
		}
	}

	@Override
	public void onNext(T o) {
		fillContext(subscriber -> subscriber.onNext(o));
	}

	@Override
	public void onError(Throwable throwable) {
		fillContext(subscriber -> subscriber.onError(throwable));
	}

	@Override
	public void onComplete() {
		fillContext(subscriber -> subscriber.onComplete());
	}

	private void fillContext(Consumer<Subscriber<? super T>> fillContextFunction) {
		try {
			UsernameThreadContext.setUsernameContext(usernameContext);
			fillContextFunction.accept(this.subscriber);
		} finally {
			UsernameThreadContext.clearUsernameContext();
		}
	}

	@Override
	public Context currentContext() {
		return this.context;
	}
}
