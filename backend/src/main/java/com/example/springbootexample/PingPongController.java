package com.example.springbootexample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingPongController {

    record PingPong(String name, int result) {
    }

    @GetMapping("/ping")
    public PingPong getPingPong() {
        return new PingPong("Islam PingPong", 90);
    }

}
