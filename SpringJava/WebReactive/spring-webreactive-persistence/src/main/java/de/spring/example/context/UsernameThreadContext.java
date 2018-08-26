package de.spring.example.context;

import java.util.Objects;

/**
 * I had to implement this class in a static way because JPA Entity objects do not allow you
 * to inject beans. StaticContextHolder did not either work :(
 * No way of injecting beans in JPA Entity classes :( 
 */
public class UsernameThreadContext {
	public static final String USERNAME_HEADER = "USERNAME";
	
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
	
	public static final void setUsername(String username) {
        Objects.requireNonNull(username);
		
		contextHolder.set(username);
	}
	
	public static final String getUsername() {
		return contextHolder.get();
	}
	
	public static final void clearUsername() {
		contextHolder.remove();
	}
}
