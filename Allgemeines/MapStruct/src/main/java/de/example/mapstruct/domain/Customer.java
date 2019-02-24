package de.example.mapstruct.domain;

public class Customer {
	private final String name;
	private final String surname;
	private final short old;

	private Customer(String name, String surname, short old) {
		this.name = name;
		this.surname = surname;
		this.old = old;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public short getOld() {
		return old;
	}

	public static Customer.Builder builder() {
		return new Customer.Builder();
	}

	public static class Builder {
		private String name;
		private String surname;
		private short old;
		
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder surname(String surname) {
			this.surname = surname;
			return this;
		}

		public Builder old(short old) {
			this.old = old;
			return this;
		}

		public Customer build() {
			return new Customer(name, surname, old);
		}
	}

}
