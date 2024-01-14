package com.example.springbootexample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingPongController {

    private static int COUNTER = 0;

    record PingPong(String name, int result) {
    }

    @GetMapping("/ping")
    public PingPong getPingPong() {
        return new PingPong("Islam PingPong %S Hits".formatted(++COUNTER), 70);
    }

}
