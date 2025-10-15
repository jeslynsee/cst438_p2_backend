package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Simple Spring Data JPA repository.
 */

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    // helpful for the update: “does someone else (not this id) have this email?”
    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<User> findByEmail(String email);
}
