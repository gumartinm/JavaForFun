package de.example.mapstruct.infrastructure;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import de.example.mapstruct.domain.Customer;
import de.example.mapstruct.infrastructure.dto.CustomerDto;

@Mapper
public interface CustomerMapper {
	CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

	@Mapping(source = "old", target = "age")
	CustomerDto mapToCustomerDto(Customer customer);
}
