package com.example.demo.controller;

import com.example.demo.model.TestEntity;
import com.example.demo.repository.TestEntityRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    private final TestEntityRepository repository;

    // Constructor-based dependency injection
    public TestController(TestEntityRepository repository) {
        this.repository = repository;
    }

    // GET /test → returns all rows from the table
    @GetMapping
    public List<TestEntity> getAll() {
        return repository.findAll();
    }

    // POST /test → inserts a new row
    @PostMapping
    public TestEntity create(@RequestBody TestEntity entity) {
        return repository.save(entity);
    }
}

