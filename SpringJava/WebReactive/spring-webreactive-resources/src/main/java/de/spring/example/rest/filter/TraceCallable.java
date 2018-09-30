package de.spring.example.rest.filter;

import java.util.concurrent.Callable;

import de.spring.example.context.UsernameContext;
import de.spring.example.context.UsernameThreadContext;

public class TraceCallable<V> implements Callable<V> {
	private final Callable<V> delegate;
	private final UsernameContext userNameParentContext;

    public TraceCallable(Callable<V> delegate) {
		this.delegate = delegate;
		userNameParentContext = UsernameThreadContext.getUsernameContext();
	}

	@Override public V call() throws Exception {
		UsernameThreadContext.setUsernameContext(userNameParentContext);
		try {
			return this.delegate.call();
		} finally {
			UsernameThreadContext.clearUsernameContext();
		}
	}
}
