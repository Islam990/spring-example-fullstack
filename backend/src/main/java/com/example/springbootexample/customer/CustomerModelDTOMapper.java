package com.example.springbootexample.customer;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerModelDTOMapper implements Function<CustomerModel, CustomerModelDTO> {
    @Override
    public CustomerModelDTO apply(CustomerModel customer) {
        return new CustomerModelDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getGender(),
                customer.getAge(),
                customer.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()),
                customer.getEmail()
        );
    }
}
