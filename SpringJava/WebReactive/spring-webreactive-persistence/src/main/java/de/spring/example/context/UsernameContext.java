package de.spring.example.context;

import java.util.Objects;

public class UsernameContext {
	private final String username;

	public UsernameContext(String username) {
		Objects.requireNonNull(username, "Username, null value is not allowed");
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
