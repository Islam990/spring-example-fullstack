package com.example.springbootexample;

import com.example.springbootexample.customer.CustomerJPARepository;
import com.example.springbootexample.customer.CustomerModel;
import com.example.springbootexample.customer.utiles.Gender;
import com.example.springbootexample.jwt.JWTUtil;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class SpringBootExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootExampleApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerJPARepository customerJPARepository,
                             PasswordEncoder passwordEncoder) {
        return args -> {

            var faker = new Faker();
            var firstName = faker.name().firstName();
            var lastName = faker.name().lastName();
            String email = firstName + "." + lastName + "@example.com";
            var gender = firstName.contains("e") ? Gender.Male : Gender.Female;
            var random = new Random();

            CustomerModel customerModel = new CustomerModel(
                    firstName + " " + lastName,
                    passwordEncoder.encode(UUID.randomUUID().toString()),
                    email,
                    random.nextInt(16, 99),
                    gender.name()
            );

            customerJPARepository.save(customerModel);

        };
    }

}
