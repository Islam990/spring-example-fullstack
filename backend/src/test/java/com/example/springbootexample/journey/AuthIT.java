package com.example.springbootexample.journey;

import com.example.springbootexample.auth.AuthRequest;
import com.example.springbootexample.auth.AuthResponse;
import com.example.springbootexample.customer.CustomerModelDTO;
import com.example.springbootexample.customer.CustomerRegisterRequest;
import com.example.springbootexample.jwt.JWTUtil;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthIT {

    @Autowired
    private WebTestClient webTestClient;
    private static final String BASE_URI = "/api/v1/customers";
    private static final String AUTH_URI = "/api/v1/auth";
    private static final Random random = new Random();
    @Autowired
    private JWTUtil jwtUtil;

    @Test
    void canLogin() {

        // Create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();
        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@Hendy.com";

        int age = random.nextInt(1, 100);
        String password = "P@ssW0rd";
        String gender = "Male";

        CustomerRegisterRequest customerRegisterRequest = new CustomerRegisterRequest(
                name,
                password,
                email,
                age,
                gender
        );

        /*
         * Check if method will return unauthorized for not found user
         */

        AuthRequest authRequest = new AuthRequest(email, password);
        webTestClient.post()
                .uri(AUTH_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authRequest), AuthRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        // Send a post request to register new user
        webTestClient.post()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegisterRequest), CustomerRegisterRequest.class)
                .exchange()
                .expectStatus()
                .isCreated();

        EntityExchangeResult<AuthResponse> result = webTestClient.post()
                .uri(AUTH_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authRequest), AuthRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthResponse>() {
                })
                .returnResult();

        String token = result.getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        AuthResponse responseBody = result.getResponseBody();
        CustomerModelDTO customerModelDTO = responseBody.customerModelDTO();

        assertThat(jwtUtil.isTokenValid(token,
                customerModelDTO.userName())).isTrue();

        assertThat(customerModelDTO.name()).isEqualTo(name);
        assertThat(customerModelDTO.email()).isEqualTo(email);
        assertThat(customerModelDTO.age()).isEqualTo(age);
        assertThat(customerModelDTO.gender()).isEqualTo(gender);
        assertThat(customerModelDTO.userName()).isEqualTo(email);
        assertThat(customerModelDTO.roles()).isEqualTo(List.of("ROLE_USER"));

    }
}
