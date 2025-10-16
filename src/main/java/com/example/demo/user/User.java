package com.example.demo.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL auto-increment
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Email(message = "Must be a valid email")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    // ---- getters/setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // not used on create

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
