package com.example.demo.controller;

import com.example.demo.model.ExerciseEntity;
import com.example.demo.repository.ExerciseRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercise")
public class ExerciseController {

    private final ExerciseRepository repository;

    // Constructor-based dependency injection
    public ExerciseController(ExerciseRepository repository) {
        this.repository = repository;
    }

    // GET /test → returns all rows from the table
    @GetMapping
    public List<ExerciseEntity> getAll() {
        return repository.findAll();
    }

    // POST /test → inserts a new row
    @PostMapping
    public ExerciseEntity create(@RequestBody ExerciseEntity entity) {
        return repository.save(entity);
    }
}

