package com.example.springbootexample.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("JPA")
public class CustomerJpaDataAccessService implements CustomerDAO {

    private final CustomerJPARepository customerJPARepository;

    public CustomerJpaDataAccessService(CustomerJPARepository customerJPARepository) {
        this.customerJPARepository = customerJPARepository;
    }

    @Override
    public List<CustomerModel> selectAllUsers() {
        return customerJPARepository.findAll();
    }

    @Override
    public Optional<CustomerModel> selectCustomerByID(Long id) {
        return customerJPARepository.findById(id);
    }

    @Override
    public void insertCustomer(CustomerModel customerModel) {
        customerJPARepository.save(customerModel);
    }

    @Override
    public boolean userEmailExists(String email) {
        return customerJPARepository.existsCustomerModelByEmail(email);
    }

    @Override
    public boolean userIDExists(Long id) {
        return customerJPARepository.existsCustomerModelById(id);
    }

    @Override
    public void deleteCustomerById(Long id) {
        customerJPARepository.deleteById(id);
    }

    @Override
    public void updateCustomer(CustomerModel customerModel) {
        customerJPARepository.save(customerModel);
    }

}
