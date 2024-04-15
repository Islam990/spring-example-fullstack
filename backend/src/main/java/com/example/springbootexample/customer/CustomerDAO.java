package com.example.springbootexample.customer;


import java.util.List;
import java.util.Optional;

public interface CustomerDAO {

     List<CustomerModel> selectAllUsers();
     Optional<CustomerModel> selectCustomerByID(Long id);
     void insertCustomer(CustomerModel customerModel);
     boolean userEmailExists(String email);
     boolean userIDExists(Long id);
     void deleteCustomerById(Long id);
     void updateCustomer(CustomerModel customerModel);
     Optional<CustomerModel> getCustomerByEmail(String email);

}
