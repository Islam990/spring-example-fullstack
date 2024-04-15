package com.example.springbootexample.customer;

import com.example.springbootexample.exception.ResourceDuplicationException;
import com.example.springbootexample.exception.ResourceNotFoundException;
import com.example.springbootexample.exception.ResourceValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDAO customerDAO;
    private final PasswordEncoder passwordEncoder;

    private final CustomerModelDTOMapper customerModelDTOMapper;

    public CustomerService(@Qualifier("JDBC") CustomerDAO customerDAO, PasswordEncoder passwordEncoder, CustomerModelDTOMapper customerModelDTOMapper) {
        this.customerDAO = customerDAO;
        this.passwordEncoder = passwordEncoder;
        this.customerModelDTOMapper = customerModelDTOMapper;
    }

    public List<CustomerModelDTO> getAllCustomer() {
        return customerDAO.selectAllUsers()
                .stream()
                .map(customerModelDTOMapper).collect(Collectors.toList());
    }

    public CustomerModelDTO getCustomer(Long id) {
        return customerDAO.selectCustomerByID(id)
                .map(customerModelDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Customer id [%S] not found".formatted(id)));
    }

    public void insertCustomer(CustomerRegisterRequest customerRegisterRequest) {

        String customerEmail = customerRegisterRequest.email();

        if (customerDAO.userEmailExists(customerEmail)) {
            throw new ResourceDuplicationException("Email Already Token");
        }

        CustomerModel newCustomer = new CustomerModel(
                customerRegisterRequest.name(),
                passwordEncoder.encode(customerRegisterRequest.password()),
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

        System.out.println("customerUpdateRequest in updateCustomer service  = " + customerUpdateRequest);

        CustomerModel customerModel = customerDAO.selectCustomerByID(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer id [%S] not found".formatted(id)));

        System.out.println("customerModel in updateCustomer service  = " + customerModel);

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

        if (!anyChange) {
            throw new ResourceValidationException("No Data Changes");
        }

        customerDAO.updateCustomer(customerModel);
    }

    public CustomerModel getCustomerByEmail(String email) {
        return customerDAO.getCustomerByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Email [%S] not found".formatted(email)));
    }
}
