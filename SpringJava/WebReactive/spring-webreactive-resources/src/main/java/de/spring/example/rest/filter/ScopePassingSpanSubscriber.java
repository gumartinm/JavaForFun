package de.spring.example.rest.filter;

import java.util.function.Consumer;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import de.spring.example.context.UsernameContext;
import de.spring.example.context.UsernameThreadContext;
import reactor.util.context.Context;

public class ScopePassingSpanSubscriber<T> implements SpanSubscription<T> {
	private final UsernameContext usernameContext;
	private final Subscriber<? super T> subscriber;
	private final Context context;

	private Subscription subscription;

	public ScopePassingSpanSubscriber(Subscriber<? super T> subscriber, Context ctx) {
		UsernameContext userNameContextParent = ctx != null ? ctx.getOrDefault(UsernameContext.class, null) : null;
		this.usernameContext = userNameContextParent;
		this.context = ctx != null && userNameContextParent != null ? ctx.put(UsernameContext.class, userNameContextParent)
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