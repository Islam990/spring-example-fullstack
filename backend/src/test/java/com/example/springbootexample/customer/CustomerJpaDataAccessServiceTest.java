package com.example.springbootexample.customer;

import com.example.springbootexample.AbstractTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;


class CustomerJpaDataAccessServiceTest extends AbstractTestContainer {

    private CustomerJpaDataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerJPARepository customerJPARepositoryMock;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJpaDataAccessService(customerJPARepositoryMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllUsers() {

        // When
        underTest.selectAllUsers();

        // Then
        verify(customerJPARepositoryMock).findAll();
    }

    @Test
    void selectCustomerByID() {

        // Given
        long id = 1;

        // When
        underTest.selectCustomerByID(id);

        // Then
        verify(customerJPARepositoryMock).findById(id);
    }

    @Test
    void insertCustomer() {

        // Given
        CustomerModel customerModel = new CustomerModel(
                1L,
                "Islam",
                "i.gad@Elswedy.com",
                26,
                "Male"
        );

        // When
        underTest.insertCustomer(customerModel);

        // Then
        verify(customerJPARepositoryMock).save(customerModel);

    }

    @Test
    void userEmailExists() {

        // Given
        String email = "islam@gmaiol.com";

        // When
        underTest.userEmailExists(email);

        // Then
        verify(customerJPARepositoryMock).existsCustomerModelByEmail(email);

    }

    @Test
    void userIDExists() {

        // Given
        long id = 0;

        // When
        underTest.userIDExists(id);

        // Then
        verify(customerJPARepositoryMock).existsCustomerModelById(id);

    }

    @Test
    void deleteCustomerById() {

        // Given
        long id = 1;

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerJPARepositoryMock).deleteById(id);

    }

    @Test
    void updateCustomer() {

        // Given
        CustomerModel customerModel = new CustomerModel(
                1L,
                "Islam",
                "i.gad@Elswedy.com",
                26,
                "Male"
        );

        // When
        underTest.updateCustomer(customerModel);

        // Then
        verify(customerJPARepositoryMock).save(customerModel);

    }
}