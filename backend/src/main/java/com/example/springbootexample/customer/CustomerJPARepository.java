package com.example.springbootexample.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJPARepository extends JpaRepository<CustomerModel, Long> {

    boolean existsCustomerModelByEmail(String email);
    boolean existsCustomerModelById(Long id);

}
