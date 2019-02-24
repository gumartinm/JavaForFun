package de.example.mapstruct.infrastructure.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = CustomerDto.Builder.class)
public class CustomerDto {
	private final String name;
	private final String surname;
	private final BigDecimal age;

	private CustomerDto(String name, String surname, BigDecimal age) {
		this.name = name;
		this.surname = surname;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public BigDecimal getAge() {
		return age;
	}

	public static CustomerDto.Builder builder() {
		return new CustomerDto.Builder();
	}

	@JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
	public static class Builder {
		private String name;
		private String surname;
		private BigDecimal age;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder surname(String surname) {
			this.surname = surname;
			return this;
		}

		public Builder age(BigDecimal age) {
			this.age = age;
			return this;
		}

		public CustomerDto build() {
			return new CustomerDto(name, surname, age);
		}
	}

}
