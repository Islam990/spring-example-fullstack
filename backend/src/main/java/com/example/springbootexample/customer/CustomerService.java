package com.example.springbootexample.customer;

import com.example.springbootexample.exception.ResourceDuplicationException;
import com.example.springbootexample.exception.ResourceNotFoundException;
import com.example.springbootexample.exception.ResourceValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("JDBC") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<CustomerModel> getAllCustomer() {
        return customerDAO.selectAllUsers();
    }

    public CustomerModel getCustomer(Long id) {
        return customerDAO.selectCustomerByID(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer id [%S] not found".formatted(id)));
    }

    public void insertCustomer(CustomerRegisterRequest customerRegisterRequest) {

        String customerEmail = customerRegisterRequest.email();

        if (customerDAO.userEmailExists(customerEmail)) {
            throw new ResourceDuplicationException("Email Already Token");
        }

        CustomerModel newCustomer = new CustomerModel(
                customerRegisterRequest.name(),
                customerRegisterRequest.email(),
                customerRegisterRequest.age(),
                customerRegisterRequest.gender()
        );

        customerDAO.insertCustomer(newCustomer);
    }

    public void deleteCustomer(Long id) {
        if (!customerDAO.userIDExists(id)) {
            throw new ResourceNotFoundException("Customer id [%S] not found".formatted(id));
        }
        customerDAO.deleteCustomerById(id);
    }

    public void updateCustomer(Long id, CustomerUpdateRequest customerUpdateRequest) {

        CustomerModel customerModel = getCustomer(id);
        boolean anyChange = false;

        if (customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customerModel.getName())) {

            customerModel.setName(customerUpdateRequest.name());
            anyChange = true;
        } else {
            customerModel.setName(null);
        }

        if (customerUpdateRequest.email() != null && !customerUpdateRequest.email().equals(customerModel.getEmail())) {

            if (customerDAO.userEmailExists(customerUpdateRequest.email())) {
                throw new ResourceDuplicationException("Email Already Token");
            }

            customerModel.setEmail(customerUpdateRequest.email());
            anyChange = true;
        } else {
            customerModel.setEmail(null);
        }

        if (customerUpdateRequest.age() != null && !customerUpdateRequest.age().equals(customerModel.getAge())) {

            customerModel.setAge(customerUpdateRequest.age());
            anyChange = true;
        } else {
            customerModel.setAge(null);
        }

        if (customerUpdateRequest.gender() != null && !customerUpdateRequest.gender().equals(customerModel.getGender())) {

            customerModel.setGender(customerUpdateRequest.gender());
            anyChange = true;
        } else {
            customerModel.setGender(null);
        }

        if (!anyChange) {
            throw new ResourceValidationException("No Data Changes");
        }

        customerDAO.updateCustomer(customerModel);
    }
}
