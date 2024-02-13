package com.example.springbootexample;

import com.example.springbootexample.customer.CustomerJPARepository;
import com.example.springbootexample.customer.CustomerModel;
import com.example.springbootexample.customer.utiles.Gender;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class SpringBootExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootExampleApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerJPARepository customerJPARepository) {
        return args -> {

            var faker = new Faker();
            var firstName = faker.name().firstName();
            var lastName = faker.name().lastName();
            var gender = firstName.contains("e") ? Gender.Male : Gender.Female;
            var random = new Random();

            CustomerModel customerModel = new CustomerModel(
                    firstName + " " + lastName,
                    firstName + "." + lastName + "@example.com",
                    random.nextInt(16, 99),
                    gender.name()
            );

            customerJPARepository.save(customerModel);

        };
    }

}
