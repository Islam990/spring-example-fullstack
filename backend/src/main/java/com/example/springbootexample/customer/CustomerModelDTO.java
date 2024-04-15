package com.example.springbootexample.customer;

import java.util.List;

public record CustomerModelDTO(
        Long id,
        String name,
        String email,
        String gender,
        int age,
        List<String> roles,
        String userName
) {
}
