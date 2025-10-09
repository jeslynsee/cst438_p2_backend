package com.example.demo.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User body) {
        if (users.existsByEmail(body.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        return users.save(body);
    }

    // READ all
    @GetMapping
    public List<User> list() {
        return users.findAll();
    }

    // READ one
    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return users.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
    }

    // UPDATE (simple fields)
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody User body) {
        User existing = users.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        if (!existing.getEmail().equals(body.getEmail()) && users.existsByEmail(body.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        existing.setName(body.getName());
        existing.setEmail(body.getEmail());
        existing.setPassword(body.getPassword()); // later: hash with BCrypt
        return users.save(existing);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!users.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        users.deleteById(id);
    }
}
