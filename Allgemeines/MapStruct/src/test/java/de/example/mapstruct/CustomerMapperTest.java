package de.example.mapstruct;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import de.example.mapstruct.domain.Customer;
import de.example.mapstruct.infrastructure.CustomerMapper;
import de.example.mapstruct.infrastructure.dto.CustomerDto;

public class CustomerMapperTest {

	@Test
	public void shouldMapToCustomerDto() {
		Customer customer = Customer.builder()
		        .old((short) 12)
		        .name("Gustavo")
		        .surname("Morcuende")
				.build();

		CustomerDto customerDto = CustomerMapper.INSTANCE.mapToCustomerDto(customer);

		assertThat(customerDto, is(notNullValue()));
		assertThat(customerDto.getAge(), is(new BigDecimal("12")));
		assertThat(customerDto.getName(), is("Gustavo"));
		assertThat(customerDto.getSurname(), is("Morcuende"));
	}
}
