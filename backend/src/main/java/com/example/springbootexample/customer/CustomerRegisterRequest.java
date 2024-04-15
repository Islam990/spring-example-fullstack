package com.example.springbootexample.customer;

import jakarta.validation.constraints.Pattern;

public record CustomerRegisterRequest(
        String name,
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                message = "Password must be at least 8 characters and contains at least one uppercase letter, one lowercase letter, and one special character")
        String password,
        String email,
        Integer age,
        String gender
) {
}
