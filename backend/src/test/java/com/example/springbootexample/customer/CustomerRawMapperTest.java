package com.example.springbootexample.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRawMapperTest {

    @Test
    void mapRow() throws SQLException {

        // Given
        CustomerRawMapper customerRawMapper = new CustomerRawMapper();

        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Islam");
        when(resultSet.getString("email")).thenReturn("islam.gad@Gmail.com");
        when(resultSet.getInt("age")).thenReturn(26);
        when(resultSet.getString("gender")).thenReturn("Male");

        // When
        CustomerModel actual = customerRawMapper.mapRow(resultSet, 1);

        // Then
        CustomerModel expected = new CustomerModel(
                1L,
                "Islam",
                "islam.gad@Gmail.com",
                26,
                "Male"
        );

        assertThat(actual).isEqualTo(expected);

    }
}