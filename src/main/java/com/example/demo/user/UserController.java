package com.example.demo.user;

import com.example.demo.user.dto.CreateUserDTO;
import com.example.demo.user.dto.UpdateUserDTO;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

/**
 * REST controller for /api/users.
 * - The POST returns 201 + Location header when it creates a user.
 * - We reject any duplicate emails with 409 (Conflict).
 * - We never let the client set "id" during create. DB generates it.
 */

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    // --- CREATE ---
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserDTO dto) {
        // unique email check
        if (repo.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        User u = new User();
        u.setName(dto.name());
        u.setEmail(dto.email());
        u.setPassword(dto.password()); 

        User saved = repo.save(u);
        URI location = URI.create("/api/users/" + saved.getId());
        return ResponseEntity.created(location).body(mask(saved));
    }

    // --- READ (all) ---
    @GetMapping
    public List<User> list() {
        return repo.findAll().stream().map(this::mask).toList();
    }

    // --- READ (one) ---
    @GetMapping("/{id}")
    public User getOne(@PathVariable Long id) {
        User found = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mask(found);
    }

    // --- UPDATE ---
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO dto) {
        User existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // If email is changing, make sure no one else owns it
        if (!existing.getEmail().equalsIgnoreCase(dto.email())
                && repo.existsByEmailAndIdNot(dto.email(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        existing.setName(dto.name());
        existing.setEmail(dto.email());
        if (dto.password() != null && !dto.password().isBlank()) {
            existing.setPassword(dto.password()); 
        }

        return mask(repo.save(existing));
    }

    // --- DELETE ---
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        repo.deleteById(id);
    }

    // --- helper: so we never leak passwords in responses ---
    private User mask(User u) {
        User copy = new User();
        copy.setId(u.getId());
        copy.setName(u.getName());
        copy.setEmail(u.getEmail());
        copy.setPassword("********");
        return copy;
    }
}