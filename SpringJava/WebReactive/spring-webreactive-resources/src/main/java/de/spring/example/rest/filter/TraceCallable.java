package de.spring.example.rest.filter;

import java.util.concurrent.Callable;

import de.spring.example.context.UsernameThreadContext;

public class TraceCallable<V> implements Callable<V> {
	private final Callable<V> delegate;
    private final String userNameParent;

    public TraceCallable(Callable<V> delegate) {
		this.delegate = delegate;
        userNameParent = UsernameThreadContext.getUsername();
	}

	@Override public V call() throws Exception {
        UsernameThreadContext.setUsername(userNameParent);
		try {
			return this.delegate.call();
		} finally {
            UsernameThreadContext.clearUsername();
		}
	}
}
