package com.example.springbootexample.journey;

import com.example.springbootexample.customer.CustomerModelDTO;
import com.example.springbootexample.customer.CustomerRegisterRequest;
import com.example.springbootexample.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
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
                "P@ssW0rd",
                email,
                age,
                "Male"
        );

        // Send a post request
        String jwtToken = webTestClient.post()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequest), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        // Get all customers
        final List<CustomerModelDTO> allCustomers = webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerModelDTO>() {
                })
                .returnResult()
                .getResponseBody();


        // Get customer by id
        assert allCustomers != null;

        long id = allCustomers.stream()
                .filter(c -> c.email().equals(email))
                .map(CustomerModelDTO::id)
                .findFirst()
                .orElseThrow();

        CustomerModelDTO expected = new CustomerModelDTO(
                id,
                name,
                email,
                "Male",
                age,
                List.of("ROLE_USER"),
                email
        );


        // Make sure that customer is present
        assertThat(allCustomers).contains(expected);


        webTestClient.get()
                .uri(BASE_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerModelDTO>() {
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

        CustomerRegisterRequest customerRegisterRequest1 = new CustomerRegisterRequest(
                name,
                "P@ssw0rd",
                email,
                age,
                "Male"
        );

        CustomerRegisterRequest customerRegisterRequest2 = new CustomerRegisterRequest(
                name,
                "P@ssw0rd",
                email + ".EG",
                age,
                "Male"
        );

        // Send a post request 1
        webTestClient.post()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequest1), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        // Send a post request 2
        String jwtToken = webTestClient.post()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequest2), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        // Get all customers
        final List<CustomerModelDTO> allCustomers = webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerModelDTO>() {
                })
                .returnResult()
                .getResponseBody();


        // Get customer by id
        assert allCustomers != null;
        long id = allCustomers.stream()
                .filter(c -> c.email().equals(email))
                .map(CustomerModelDTO::id)
                .findFirst()
                .orElseThrow();

        // Delete Customer 1 by 2
        webTestClient.delete()
                .uri(BASE_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();


        // Check if customer is present
        webTestClient.get()
                .uri(BASE_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
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
                "P@ssw0rd", email,
                age,
                "Male"
        );

        // Send a post request
        String jwtToken = webTestClient.post()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequest), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        // Get all customers
        final List<CustomerModelDTO> allCustomers = webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerModelDTO>() {
                })
                .returnResult()
                .getResponseBody();


        // Get customer by id
        assert allCustomers != null;
        long id = allCustomers.stream()
                .filter(c -> c.email().equals(email))
                .map(CustomerModelDTO::id)
                .findFirst()
                .orElseThrow();

        String newName = "TestUser";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                newName, null, null
        );

        // Update Customer
        webTestClient.put()
                .uri(BASE_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk();


        // get actual customer
        final CustomerModelDTO updatedCustomer = webTestClient.get()
                .uri(BASE_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerModelDTO.class)
                .returnResult()
                .getResponseBody();

        CustomerModelDTO expected = new CustomerModelDTO(
                id, newName,  email, "Male", age, List.of("ROLE_USER"), email
        );

        assertThat(updatedCustomer).isEqualTo(expected);
    }
}
