package com.example.springbootexample.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Repository("JDBC")
public class CustomerJDBCDataAccessService implements CustomerDAO {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRawMapper customerRawMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRawMapper customerRawMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRawMapper = customerRawMapper;
    }

    @Override
    public List<CustomerModel> selectAllUsers() {
        var sql = """
                SELECT * FROM customer
                """;

        return jdbcTemplate.query(sql, customerRawMapper);
    }

    @Override
    public Optional<CustomerModel> selectCustomerByID(Long id) {
        var sql = """
                SELECT * FROM customer
                WHERE id =  ?
                """;

        return jdbcTemplate.query(sql, customerRawMapper, id).stream().findFirst();
    }

    @Override
    public void insertCustomer(CustomerModel customerModel) {
        var sql = """
                INSERT INTO customer(name, email, age, gender)
                VALUES(?, ?, ?, ?)
                """;

        int result = jdbcTemplate.update(sql,
                customerModel.getName(),
                customerModel.getEmail(),
                customerModel.getAge(),
                customerModel.getGender());

        System.out.println("Insert customer result = " + result);
    }


    @Override
    public boolean userEmailExists(String email) {
        var sql = """
                SELECT COUNT(id) FROM customer WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean userIDExists(Long id) {
        var sql = """
                SELECT COUNT(id) FROM customer WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Long id) {
        var sql = """
                DELETE FROM customer WHERE id = ?
                """;
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateCustomer(CustomerModel customerModel) {

        boolean isUpdated = false;
        StringJoiner joiner = new StringJoiner(", ");

        if (customerModel.getName() != null) {
            var sql = """
                    UPDATE customer SET name = ? WHERE id = ?
                    """;

            jdbcTemplate.update(sql,
                    customerModel.getName(),
                    customerModel.getId());

            isUpdated = true;
            joiner.add("name");
        }

        if (customerModel.getEmail() != null) {
            var sql = """
                    UPDATE customer SET email = ? WHERE id = ?
                    """;

            jdbcTemplate.update(sql,
                    customerModel.getEmail(),
                    customerModel.getId());

            isUpdated = true;
            joiner.add("email");

        }

        if (customerModel.getAge() != null) {
            var sql = """
                    UPDATE customer SET age = ? WHERE id = ?
                    """;

            jdbcTemplate.update(sql,
                    customerModel.getAge(),
                    customerModel.getId());

            isUpdated = true;
            joiner.add("age");
        }

        if (customerModel.getGender() != null) {
            var sql = """
                    UPDATE customer SET gender = ? WHERE id = ?
                    """;

            jdbcTemplate.update(sql,
                    customerModel.getGender(),
                    customerModel.getId());

            isUpdated = true;
            joiner.add("gender");
        }

        if (isUpdated) {
            System.out.println("Customer fields Updated = " + "[%S]".formatted(joiner.toString().formatted()));
        } else {
            System.out.println("No Thing To Update");
        }

    }

}
