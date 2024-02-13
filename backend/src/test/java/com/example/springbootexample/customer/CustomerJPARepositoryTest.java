package com.example.springbootexample.customer;

import com.example.springbootexample.AbstractTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerJPARepositoryTest extends AbstractTestContainer {

    @Autowired
    private CustomerJPARepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println("applicationContext = " + applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerModelByEmail() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        CustomerModel customerModel = new CustomerModel(
                FAKER.name().fullName(),
                email,
                20,
                "Male"
        );

        underTest.save(customerModel);


        //When
        boolean actual = underTest.existsCustomerModelByEmail(email);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void testFailsWhenCustomerEmailNotExists(){
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean actual = underTest.existsCustomerModelByEmail(email);

        // Then
        assertThat(actual).isFalse();

    }


    @Test
    void existsCustomerModelById() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        CustomerModel customerModel = new CustomerModel(
                FAKER.name().fullName(),
                email,
                20,
                "Male"
        );
        underTest.save(customerModel);


        long id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(CustomerModel::getId)
                .findFirst()
                .orElseThrow();

        //When
        boolean actual = underTest.existsCustomerModelById(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void testFailsWhenCustomerIdNotExists(){
        // Given
        long id = 0;

        // When
        boolean actual = underTest.existsCustomerModelById(id);

        // Then
        assertThat(actual).isFalse();

    }
}