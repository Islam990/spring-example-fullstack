package com.example.springbootexample.customer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final CustomerDAO customerDAO;

    public CustomerUserDetailsService(@Qualifier("JPA") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return customerDAO.getCustomerByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Email %S not found".formatted(email)));
    }
}
