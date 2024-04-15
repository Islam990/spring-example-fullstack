package com.example.springbootexample.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("List")
public class CustomerListDataAccessService implements CustomerDAO {

    private final static List<CustomerModel> customerModelList;

    static {
        customerModelList = new ArrayList<>();
        CustomerModel islam = new CustomerModel(
                1l,
                "Islam Gad",
                "password", "islam.gad@elswedy.com",
                26,
                "Male"
        );

        CustomerModel mohamed = new CustomerModel(
                2l,
                "Mohamed Gad",
                "password", "m.gad@gmail.com",
                21,
                "Female"
        );

        customerModelList.add(islam);
        customerModelList.add(mohamed);
    }

    @Override
    public List<CustomerModel> selectAllUsers() {
        return customerModelList;
    }

    @Override
    public Optional<CustomerModel> selectCustomerByID(Long id) {
        return customerModelList.stream()
                .filter(customerModel -> customerModel.getId().equals(id))
                .findFirst();
    }

    @Override
    public void insertCustomer(CustomerModel customerModel) {
        customerModelList.add(customerModel);
    }

    @Override
    public boolean userEmailExists(String email) {
        return customerModelList.stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public boolean userIDExists(Long id) {
        return customerModelList.stream()
                .anyMatch(c -> c.getId().equals(id));
    }

    @Override
    public void deleteCustomerById(Long id) {
        customerModelList.removeIf(c -> c.getId().equals(id));
    }

    @Override
    public void updateCustomer(CustomerModel customerModel) {
        customerModelList.add(customerModel);
    }

    @Override
    public Optional<CustomerModel> getCustomerByEmail(String email) {
        return customerModelList.stream()
                .filter(customerModel -> customerModel.getEmail().equals(email))
                .findFirst();
    }
}
