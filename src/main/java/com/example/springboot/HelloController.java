package com.example.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/getExerciseNames")
    public String getExerciseNames() {
        return "List of exercise names";
    }

    @GetMapping("/getExerciseDetails")
    public String getExerciseDetails() {
        return "Details of exercises";
    }
}