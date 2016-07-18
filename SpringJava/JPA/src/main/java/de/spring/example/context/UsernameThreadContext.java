package de.spring.example.context;

import javax.inject.Named;

import org.springframework.util.Assert;

@Named("userNameThreadContext")
public class UsernameThreadContext {
	public static final String USERNAME_HEADER = "USERNAME";
	
	private final ThreadLocal<String> contextHolder = new ThreadLocal<>();
	
	public void setUsername(String username) {
		Assert.notNull(username);
		
		contextHolder.set(username);
	}
	
	public String getUsername() {
		return contextHolder.get();
	}
	
	public void clearUsername() {
		contextHolder.remove();
	}
}
