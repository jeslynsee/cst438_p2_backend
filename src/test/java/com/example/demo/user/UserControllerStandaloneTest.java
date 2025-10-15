package com.example.demo.user;

/*
 * These are simple, passing tests that:
 * - DO NOT start Spring (no @WebMvcTest, no @SpringBootTest).
 * - DO NOT use a real database.
 * - Use MockMvc in "standalone" mode with a manually constructed controller.
 * - Mock the repository with Mockito.
 *
 * This keeps the tests fast and avoids any environment/config issues.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerStandaloneTest {

    private MockMvc mvc;
    private ObjectMapper json;
    private UserRepository repo; // mocked
    private UserController controller; // real controller, no Spring

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(UserRepository.class);
        controller = new UserController(repo);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        json = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/users -> 201 Created with Location & JSON")
    void createUser_created() throws Exception {
        // GIVEN: email not taken, repo will return saved user with id
        when(repo.existsByEmail("kass@example.com")).thenReturn(false);

        User saved = new User();
        saved.setId(10L);
        saved.setName("Kass");
        saved.setEmail("kass@example.com");
        saved.setPassword("pw"); // controller masks when returning
        when(repo.save(any(User.class))).thenReturn(saved);

        var body = """
          {"name":"Kass","email":"kass@example.com","password":"pw"}
        """;

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isCreated())
           .andExpect(header().string("Location", "/api/users/10"))
           .andExpect(jsonPath("$.id").value(10))
           .andExpect(jsonPath("$.email").value("kass@example.com"))
           .andExpect(jsonPath("$.password").value("********")); // masked
    }

    @Test
    @DisplayName("GET /api/users -> 200 with list")
    void listUsers_ok() throws Exception {
        User u1 = new User(); u1.setId(1L); u1.setName("Ava");  u1.setEmail("ava@example.com");  u1.setPassword("pw");
        User u2 = new User(); u2.setId(2L); u2.setName("Kass"); u2.setEmail("kass@example.com"); u2.setPassword("pw");
        when(repo.findAll()).thenReturn(List.of(u1, u2));

        mvc.perform(get("/api/users"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(2))
           .andExpect(jsonPath("$[0].id").value(1))
           .andExpect(jsonPath("$[1].email").value("kass@example.com"))
           .andExpect(jsonPath("$[0].password").value("********")); // controller masks
    }

    @Test
    @DisplayName("GET /api/users/{id} -> 404 when not found")
    void getOne_notFound() throws Exception {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/users/999"))
           .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/users/{id} -> 200 when updating and email free")
    void update_ok() throws Exception {
        // Existing user
        User existing = new User();
        existing.setId(3L);
        existing.setName("Old");
        existing.setEmail("old@example.com");
        existing.setPassword("pw");
        when(repo.findById(3L)).thenReturn(Optional.of(existing));

        // New email is not used by somebody else
        when(repo.existsByEmailAndIdNot("new@example.com", 3L)).thenReturn(false);

        // Save returns updated
        User updated = new User();
        updated.setId(3L);
        updated.setName("New");
        updated.setEmail("new@example.com");
        updated.setPassword("pw");
        when(repo.save(any(User.class))).thenReturn(updated);

        var body = """
          {"name":"New","email":"new@example.com","password":"pw"}
        """;

        mvc.perform(put("/api/users/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(3))
           .andExpect(jsonPath("$.name").value("New"))
           .andExpect(jsonPath("$.email").value("new@example.com"))
           .andExpect(jsonPath("$.password").value("********")); // controller masks
    }
}

