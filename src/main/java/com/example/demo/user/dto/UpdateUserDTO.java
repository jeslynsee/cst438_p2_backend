package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for PUT /api/users/{id}.
 * We keep the fields required, but you could make them optional if you want PATCH-style updates.
 */

public record UpdateUserDTO(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password
) {}
