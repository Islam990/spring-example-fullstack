package com.example.springbootexample.customer;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CustomerRawMapper implements RowMapper<CustomerModel> {
    @Override
    public CustomerModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CustomerModel(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("age"),
                rs.getString("gender")
        );
    }
}
