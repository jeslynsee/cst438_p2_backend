package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /api/users.
 * Using a DTO avoids accidentally accepting an "id" from the client.
 */

public record CreateUserDTO(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password
) {}
