package com.example.springbootexample.customer;

import com.example.springbootexample.AbstractTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class CustomerJDBCDataAccessServiceTest extends AbstractTestContainer {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRawMapper customerRawMapper = new CustomerRawMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJDBCTemplate(),
                customerRawMapper
        );
    }

    @Test
    void selectAllUsers() {

        //Given
        CustomerModel customerModel = new CustomerModel(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        underTest.insertCustomer(customerModel);

        //When
        List<CustomerModel> actual = underTest.selectAllUsers();

        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerByID() {

        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        CustomerModel customerModel = new CustomerModel(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customerModel);

        long id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(CustomerModel::getId)
                .findFirst()
                .orElseThrow();

        //When
        final Optional<CustomerModel> actual = underTest.selectCustomerByID(id);

        //Then
        assertThat(actual).isPresent().hasValueSatisfying(
                c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getEmail()).isEqualTo(customerModel.getEmail());
                    assertThat(c.getName()).isEqualTo(customerModel.getName());
                    assertThat(c.getAge()).isEqualTo(customerModel.getAge());
                }
        );

    }

    @Test
    void willReturnEmptyWhenIDIsIncorrect() {

        //Given
        long id = 0;

        //When
        final Optional<CustomerModel> actual = underTest.selectCustomerByID(id);

        //Then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {

        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        CustomerModel customerModel = new CustomerModel(
                FAKER.name().fullName(),
                email,
                20
        );

        //When
        underTest.insertCustomer(customerModel);

        //Then
        final List<CustomerModel> actual = underTest.selectAllUsers();
        assertThat(actual).isNotEmpty();
    }

    @Test
    void userEmailExists() {

        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        CustomerModel customerModel = new CustomerModel(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customerModel);

        //When
        boolean actual = underTest.userEmailExists(email);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void returnFalseWhenEmailNotExisting() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        var actual = underTest.userEmailExists(email);

        assertThat(actual).isFalse();
    }

    @Test
    void userIDExists() {

        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        CustomerModel customerModel = new CustomerModel(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customerModel);

        long id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(CustomerModel::getId)
                .findFirst()
                .orElseThrow();

        //When
        boolean actual = underTest.userIDExists(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void returnFalseWhenIDNotExisting() {

        //Given
        long id = 0;

        //When
        var actual = underTest.userIDExists(id);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {

        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        CustomerModel customerModel = new CustomerModel(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customerModel);

        long id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(CustomerModel::getId)
                .findFirst()
                .orElseThrow();

        //When
        underTest.deleteCustomerById(id);

        //Then
        final Optional<CustomerModel> actual = underTest.selectCustomerByID(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {

        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        CustomerModel customerModel = new CustomerModel(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customerModel);

        long id = underTest.selectAllUsers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(CustomerModel::getId)
                .findFirst()
                .orElseThrow();

        String newName = "TestUserName";

        //When
        CustomerModel update = new CustomerModel();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);

        //Then
        final Optional<CustomerModel> actual = underTest.selectCustomerByID(id);
        assertThat(actual).hasValueSatisfying(
                c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(newName); // Change
                    assertThat(c.getEmail()).isEqualTo(customerModel.getEmail());
                    assertThat(c.getAge()).isEqualTo(customerModel.getAge());
                }
        );
    }


    /* Decrease Hikari connection pools to work in gitHub actions
    *
    * org.postgresql.util.PSQLException: FATAL: sorry, too many clients already
    *
    * */

//    @Test
//    void updateCustomerEmail() {
//
//        //Given
//        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
//        CustomerModel customerModel = new CustomerModel(
//                FAKER.name().fullName(),
//                email,
//                20
//        );
//        underTest.insertCustomer(customerModel);
//
//        long id = underTest.selectAllUsers()
//                .stream()
//                .filter(c -> c.getEmail().equals(email))
//                .map(CustomerModel::getId)
//                .findFirst()
//                .orElseThrow();
//
//        String newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
//
//        //When
//        CustomerModel update = new CustomerModel();
//        update.setId(id);
//        update.setEmail(newEmail);
//
//        underTest.updateCustomer(update);
//
//        //Then
//        final Optional<CustomerModel> actual = underTest.selectCustomerByID(id);
//        assertThat(actual).hasValueSatisfying(
//                c -> {
//                    assertThat(c.getId()).isEqualTo(id);
//                    assertThat(c.getName()).isEqualTo(customerModel.getName());
//                    assertThat(c.getEmail()).isEqualTo(newEmail); // Change
//                    assertThat(c.getAge()).isEqualTo(customerModel.getAge());
//                }
//        );
//    }
//
//    @Test
//    void updateCustomerAge() {
//
//        //Given
//        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
//        CustomerModel customerModel = new CustomerModel(
//                FAKER.name().fullName(),
//                email,
//                20
//        );
//        underTest.insertCustomer(customerModel);
//
//        long id = underTest.selectAllUsers()
//                .stream()
//                .filter(c -> c.getEmail().equals(email))
//                .map(CustomerModel::getId)
//                .findFirst()
//                .orElseThrow();
//
//        int newAge = 26;
//
//        //When
//        CustomerModel update = new CustomerModel();
//        update.setId(id);
//        update.setAge(newAge);
//
//        underTest.updateCustomer(update);
//
//        //Then
//        final Optional<CustomerModel> actual = underTest.selectCustomerByID(id);
//        assertThat(actual).hasValueSatisfying(
//                c -> {
//                    assertThat(c.getId()).isEqualTo(id);
//                    assertThat(c.getName()).isEqualTo(customerModel.getName());
//                    assertThat(c.getEmail()).isEqualTo(customerModel.getEmail());
//                    assertThat(c.getAge()).isEqualTo(newAge); // Change
//                }
//        );
//    }
//
//    @Test
//    void updateAllCustomerProperties() {
//
//        //Given
//        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
//        CustomerModel customerModel = new CustomerModel(
//                FAKER.name().fullName(),
//                email,
//                20
//        );
//        underTest.insertCustomer(customerModel);
//
//        long id = underTest.selectAllUsers()
//                .stream()
//                .filter(c -> c.getEmail().equals(email))
//                .map(CustomerModel::getId)
//                .findFirst()
//                .orElseThrow();
//
//        //When
//        CustomerModel update = new CustomerModel();
//        update.setId(id);
//        update.setName(FAKER.name().fullName());
//        update.setEmail(FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID());
//        update.setAge(26);
//
//        underTest.updateCustomer(update);
//
//        //Then
//        final Optional<CustomerModel> actual = underTest.selectCustomerByID(id);
//        assertThat(actual).hasValue(update);
//    }
//
//    @Test
//    void willNotUpdateIfNoThingToUpdate() {
//
//        //Given
//        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
//        CustomerModel customerModel = new CustomerModel(
//                FAKER.name().fullName(),
//                email,
//                20
//        );
//        underTest.insertCustomer(customerModel);
//
//        long id = underTest.selectAllUsers()
//                .stream()
//                .filter(c -> c.getEmail().equals(email))
//                .map(CustomerModel::getId)
//                .findFirst()
//                .orElseThrow();
//
//        //When
//        CustomerModel update = new CustomerModel();
//        update.setId(id);
//
//        underTest.updateCustomer(update);
//
//        //Then
//        final Optional<CustomerModel> actual = underTest.selectCustomerByID(id);
//        assertThat(actual).hasValueSatisfying(c -> {
//            assertThat(c.getId()).isEqualTo(id);
//            assertThat(c.getName()).isEqualTo(customerModel.getName());
//            assertThat(c.getEmail()).isEqualTo(customerModel.getEmail());
//            assertThat(c.getAge()).isEqualTo(customerModel.getAge());
//        });
//    }
}