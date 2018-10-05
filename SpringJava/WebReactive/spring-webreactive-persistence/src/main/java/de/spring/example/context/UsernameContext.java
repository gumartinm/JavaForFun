package de.spring.example.context;

import java.util.Objects;

public class UsernameContext extends ThreadContext {
	public static final String USERNAME_HEADER = "USERNAME";

	private final String username;

	public UsernameContext(String username) {
		Objects.requireNonNull(username, "Username, null value is not allowed");
		this.username = username;
	}

	@Override
	public String getValue() {
		return username;
	}

	@Override
	public String getHeader() {
		return USERNAME_HEADER;
	}
}
