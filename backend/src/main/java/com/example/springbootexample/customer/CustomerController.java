package com.example.springbootexample.customer;

import com.example.springbootexample.jwt.JWTUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final JWTUtil jwtUtil;

    private final CustomerService customerService;

    public CustomerController(JWTUtil jwtUtil, CustomerService customerService) {
        this.jwtUtil = jwtUtil;
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerModelDTO> getAllCustomer() {
        return customerService.getAllCustomer();
    }

    @GetMapping("{id}")
    public CustomerModelDTO getCustomer(@PathVariable Long id) {
        return customerService.getCustomer(id);
    }

    @PostMapping
    public ResponseEntity<CustomerModel> insertCustomer(@Valid @RequestBody CustomerRegisterRequest customerRegisterRequest) {

        customerService.insertCustomer(customerRegisterRequest);
        Long id = customerService.getCustomerByEmail(customerRegisterRequest.email()).getId();

        String token = jwtUtil.issueToken(customerRegisterRequest.email(), "ROLE_USER");

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location)
                .header(HttpHeaders.AUTHORIZATION, token)
                .build();
    }

    @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @PutMapping("{id}")
    public void updateCustomer(@PathVariable Long id, @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        customerService.updateCustomer(id, customerUpdateRequest);
    }

}
