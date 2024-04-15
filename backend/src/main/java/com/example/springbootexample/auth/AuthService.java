package com.example.springbootexample.auth;

import com.example.springbootexample.customer.CustomerModel;
import com.example.springbootexample.customer.CustomerModelDTO;
import com.example.springbootexample.customer.CustomerModelDTOMapper;
import com.example.springbootexample.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final CustomerModelDTOMapper customerModelDTOMapper;

    public AuthService(AuthenticationManager authenticationManager,
                       JWTUtil jwtUtil,
                       CustomerModelDTOMapper customerModelDTOMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.customerModelDTOMapper = customerModelDTOMapper;
    }

    public AuthResponse login(AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.userName(),
                        authRequest.password()
                )
        );

        CustomerModel principal = (CustomerModel) authenticate.getPrincipal();
        CustomerModelDTO customerModelDTO = customerModelDTOMapper.apply(principal);
        String token = jwtUtil.issueToken(customerModelDTO.userName(), customerModelDTO.roles());

        return new AuthResponse(
                token,
                customerModelDTO
        );
    }

}
