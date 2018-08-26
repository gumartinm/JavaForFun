package de.spring.example.rest.filter;

import de.spring.example.context.UsernameThreadContext;

public class TraceRunnable implements Runnable {
    private final Runnable delegate;
    private final String userNameParent;

    public TraceRunnable(Runnable delegate) {
		this.delegate = delegate;
        userNameParent = UsernameThreadContext.getUsername();
	}

	@Override
	public void run() {
        UsernameThreadContext.setUsername(userNameParent);
		try {
			this.delegate.run();
		} finally {
            UsernameThreadContext.clearUsername();
		}
	}
}
