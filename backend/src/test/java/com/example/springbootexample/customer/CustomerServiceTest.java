package com.example.springbootexample.customer;

import com.example.springbootexample.exception.ResourceDuplicationException;
import com.example.springbootexample.exception.ResourceNotFoundException;
import com.example.springbootexample.exception.ResourceValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDAO customerDAO;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final CustomerModelDTOMapper customerModelDTOMapper = new CustomerModelDTOMapper();


    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO, passwordEncoder, customerModelDTOMapper);
    }

    @Test
    void getAllCustomer() {

        // When
        underTest.getAllCustomer();

        // Then
        verify(customerDAO).selectAllUsers();

    }

    @Test
    void canGetCustomer() {

        // Given
        long id = 1;

        CustomerModel customerModel = new CustomerModel(
                id,
                "Islam",
                "password", "Islam@gmail.com",
                26,
                "Male"
        );

        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customerModel));

        CustomerModelDTO expected = customerModelDTOMapper.apply(customerModel);

        // When
        final CustomerModelDTO actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    void willThrowResourceNotFoundExceptionWhenCustomerNotFound() {

        // Given
        long id = 1;

        // When
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer id [%S] not found".formatted(id));
    }

    @Test
    void insertCustomer() {

        // Given
        String customerEmail = "islam.gad@gmail.com";
        when(customerDAO.userEmailExists(customerEmail)).thenReturn(false); // to avoid exception

        CustomerRegisterRequest customerRegisterRequest = new CustomerRegisterRequest(
                "Islam",
                "password", customerEmail,
                26,
                "Male"
        );

        String hashedPassword = "$%#@%^&FHF--)";
        when(passwordEncoder.encode(customerRegisterRequest.password())).thenReturn(hashedPassword);

        // When
        underTest.insertCustomer(customerRegisterRequest);

        // Then
        ArgumentCaptor<CustomerModel> argumentCaptor = ArgumentCaptor.forClass(
                CustomerModel.class
        );

        verify(customerDAO).insertCustomer(argumentCaptor.capture());

        final CustomerModel actual = argumentCaptor.getValue();

        assertThat(actual.getId()).isNull();
        assertThat(actual.getEmail()).isEqualTo(customerRegisterRequest.email());
        assertThat(actual.getName()).isEqualTo(customerRegisterRequest.name());
        assertThat(actual.getAge()).isEqualTo(customerRegisterRequest.age());
        assertThat(actual.getPassword()).isEqualTo(hashedPassword);

    }

    @Test
    void willThrowResourceDuplicationExceptionWhenInsertExistingEmail() {

        // Given
        String customerEmail = "islam.gad@gmail.com";
        when(customerDAO.userEmailExists(customerEmail)).thenReturn(true);

        CustomerRegisterRequest customerRegisterRequest = new CustomerRegisterRequest(
                "Islam",
                "password", customerEmail,
                26,
                "Male"
        );

        // When
        assertThatThrownBy(() -> underTest.insertCustomer(customerRegisterRequest))
                .isInstanceOf(ResourceDuplicationException.class)
                .hasMessage("Email Already Token");

        // Then
        verify(customerDAO, never()).insertCustomer(any());

    }

    @Test
    void deleteCustomer() {

        // Given
        long id = 1;

        when(customerDAO.userIDExists(id)).thenReturn(true); // to avoid exception

        // When
        underTest.deleteCustomer(id);

        // Then
        verify(customerDAO).deleteCustomerById(id);

    }

    @Test
    void willThrowExceptionWhenDeleteIdNotExists() {

        // Given
        long id = 1;

        when(customerDAO.userIDExists(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer id [%S] not found".formatted(id));


        // Then
        verify(customerDAO, never()).deleteCustomerById(id);

    }

    @Test
    void canUpdateAllCustomersProperties() {

        // Given
        long id = 10;
        final CustomerModel customerModel = new CustomerModel(
                id,
                "Islam",
                "password", "Islam.gad@gmail.com",
                26,
                "Male"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customerModel));

        final String newEmail = "mohamed@gmail.com";
        final CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "Mohamed",
                newEmail,
                21
        );
        when(customerDAO.userEmailExists(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, customerUpdateRequest);

        // Then
        ArgumentCaptor<CustomerModel> argumentCaptor = ArgumentCaptor.forClass(
                CustomerModel.class
        );

        verify(customerDAO).updateCustomer(argumentCaptor.capture());

        final CustomerModel updatedCustomer = argumentCaptor.getValue();

        assertThat(updatedCustomer.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(updatedCustomer.getEmail()).isEqualTo(customerUpdateRequest.email());
        assertThat(updatedCustomer.getAge()).isEqualTo(customerUpdateRequest.age());

    }

    @Test
    void canUpdateCustomerEmail() {

        // Given
        long id = 10;
        final CustomerModel customerModel = new CustomerModel(
                id,
                "Islam",
                "password", "Islam.gad@gmail.com",
                26,
                "Male"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customerModel));

        final String newEmail = "mohamed@gmail.com";
        final CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                null,
                newEmail,
                null
        );
        when(customerDAO.userEmailExists(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, customerUpdateRequest);

        // Then
        ArgumentCaptor<CustomerModel> argumentCaptor = ArgumentCaptor.forClass(
                CustomerModel.class
        );

        verify(customerDAO).updateCustomer(argumentCaptor.capture());

        final CustomerModel updatedCustomer = argumentCaptor.getValue();

        assertThat(updatedCustomer.getName()).isEqualTo(customerModel.getName());
        assertThat(updatedCustomer.getEmail()).isEqualTo(customerUpdateRequest.email());
        assertThat(updatedCustomer.getAge()).isEqualTo(customerModel.getAge());

    }

    @Test
    void canUpdateCustomerName() {

        // Given
        long id = 10;
        final CustomerModel customerModel = new CustomerModel(
                id,
                "Islam",
                "password", "Islam.gad@gmail.com",
                26,
                "Male"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customerModel));

        final CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "Mohamed",
                null,
                null
        );

        // When
        underTest.updateCustomer(id, customerUpdateRequest);

        // Then
        ArgumentCaptor<CustomerModel> argumentCaptor = ArgumentCaptor.forClass(
                CustomerModel.class
        );

        verify(customerDAO).updateCustomer(argumentCaptor.capture());

        final CustomerModel updatedCustomer = argumentCaptor.getValue();

        assertThat(updatedCustomer.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(updatedCustomer.getEmail()).isEqualTo(customerModel.getEmail());
        assertThat(updatedCustomer.getAge()).isEqualTo(customerModel.getAge());

    }

    @Test
    void canUpdateCustomerAge() {

        // Given
        long id = 10;
        final CustomerModel customerModel = new CustomerModel(
                id,
                "Islam",
                "password", "Islam.gad@gmail.com",
                26,
                "Male"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customerModel));

        final CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                null,
                null,
                21
        );

        // When
        underTest.updateCustomer(id, customerUpdateRequest);

        // Then
        ArgumentCaptor<CustomerModel> argumentCaptor = ArgumentCaptor.forClass(
                CustomerModel.class
        );

        verify(customerDAO).updateCustomer(argumentCaptor.capture());

        final CustomerModel updatedCustomer = argumentCaptor.getValue();

        assertThat(updatedCustomer.getName()).isEqualTo(customerModel.getName());
        assertThat(updatedCustomer.getEmail()).isEqualTo(customerModel.getEmail());
        assertThat(updatedCustomer.getAge()).isEqualTo(customerUpdateRequest.age());

    }

    @Test
    void willThrowExceptionWhenUpdateCustomerThatHaveEmailExists() {

        // Given
        long id = 10;
        final CustomerModel customerModel = new CustomerModel(
                id,
                "Islam",
                "password", "Islam.gad@gmail.com",
                26,
                "Male"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customerModel));

        final String newEmail = "mohamed@gmail.com";
        final CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "Mohamed",
                newEmail,
                21
        );
        when(customerDAO.userEmailExists(newEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, customerUpdateRequest))
                .isInstanceOf(ResourceDuplicationException.class)
                .hasMessage("Email Already Token");

        // Then
        verify(customerDAO, never()).updateCustomer(any());

    }

    @Test
    void willThrowExceptionWhenUpdateCustomerWithNoChange() {

        // Given
        long id = 10;
        final CustomerModel customerModel = new CustomerModel(
                id,
                "Islam",
                "password", "Islam.gad@gmail.com",
                26,
                "Male"
        );
        when(customerDAO.selectCustomerByID(id)).thenReturn(Optional.of(customerModel));

        final CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                customerModel.getName(),
                customerModel.getEmail(),
                customerModel.getAge()
        );

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, customerUpdateRequest))
                .isInstanceOf(ResourceValidationException.class)
                .hasMessage("No Data Changes");

        // Then
        verify(customerDAO, never()).updateCustomer(any());

    }

}