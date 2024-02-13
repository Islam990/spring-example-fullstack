package com.example.springbootexample.customer;

public record CustomerRegisterRequest(
        String name,
        String email,
        Integer age,
        String gender
) {
}
