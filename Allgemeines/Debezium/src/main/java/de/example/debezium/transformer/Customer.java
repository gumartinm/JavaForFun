package de.example.debezium.transformer;

public class Customer {
	private final Base base;
	private final Long customerId;
	private final String firstName;
	private final String lastName;
	private final String email;

	public Customer(Base base, Long customerId, String firstName, String lastName, String email) {
		this.base = base;
		this.customerId = customerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Base getBase() {
		return base;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public static class Builder {
		private Base base;
		private Long customerId;
		private String firstName;
		private String lastName;
		private String email;

		public Builder withBase(Base base) {
			this.base = base;
			return this;
		}

		public Builder withCustomerId(Long customerId) {
			this.customerId = customerId;
			return this;
		}

		public Builder withFirstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder withLastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		public Customer build() {
			return new Customer(this.base, this.customerId, this.firstName, this.lastName, this.email);
		}
	}
}