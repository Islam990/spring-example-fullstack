package com.example.springbootexample.journey;

import com.example.springbootexample.customer.CustomerModel;
import com.example.springbootexample.customer.CustomerRegisterRequest;
import com.example.springbootexample.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIT {

    @Autowired
    private WebTestClient webTestClient;
    private static final String BASE_URI = "/api/v1/customers";
    private static final Random random = new Random();

    @Test
    public void canRegisterCustomer() {

        // Create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@Hendy.com";
        int age = random.nextInt(1, 100);
        CustomerRegisterRequest customerRegisterRequest = new CustomerRegisterRequest(
                name,
                email,
                age,
                "Male"
        );

        // Send a post request
        webTestClient.post()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequest), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Get all customers
        final List<CustomerModel> allCustomers = webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerModel>() {
                })
                .returnResult()
                .getResponseBody();

        CustomerModel expected = new CustomerModel(
                name,
                email,
                age,
                "Male"
        );


        // Make sure that customer is present
        assertThat(allCustomers).
                usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expected);

        // Get customer by id
        assert allCustomers != null;
        long id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(CustomerModel::getId)
                .findFirst()
                .orElseThrow();

        expected.setId(id);

        webTestClient.get()
                .uri(BASE_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerModel>() {
                })
                .isEqualTo(expected);

    }

    @Test
    void canDeleteCustomer() {

        // Create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@Hendy.com";
        int age = random.nextInt(1, 100);
        CustomerRegisterRequest customerRegisterRequest = new CustomerRegisterRequest(
                name,
                email,
                age,
                "Male"
        );

        // Send a post request
        webTestClient.post()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequest), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Get all customers
        final List<CustomerModel> allCustomers = webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerModel>() {
                })
                .returnResult()
                .getResponseBody();


        // Get customer by id
        assert allCustomers != null;
        long id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(CustomerModel::getId)
                .findFirst()
                .orElseThrow();

        // Delete Customer
        webTestClient.delete()
                .uri(BASE_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();


        // Check if customer is present
        webTestClient.get()
                .uri(BASE_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void canUpdateCustomer() {

        // Create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@Hendy.com";
        int age = random.nextInt(1, 100);
        CustomerRegisterRequest customerRegisterRequest = new CustomerRegisterRequest(
                name,
                email,
                age,
                "Male"
        );

        // Send a post request
        webTestClient.post()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequest), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Get all customers
        final List<CustomerModel> allCustomers = webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerModel>() {
                })
                .returnResult()
                .getResponseBody();


        // Get customer by id
        assert allCustomers != null;
        long id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(CustomerModel::getId)
                .findFirst()
                .orElseThrow();

        String newName = "TestUser";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                newName, null, null, null
        );

        // Update Customer
        webTestClient.put()
                .uri(BASE_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        // get actual customer
        final CustomerModel updatedCustomer = webTestClient.get()
                .uri(BASE_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerModel.class)
                .returnResult()
                .getResponseBody();

        CustomerModel expected = new CustomerModel(
                id, newName, email, age, "Male"
        );

        assertThat(updatedCustomer).isEqualTo(expected);
    }
}
