package com.example.demo.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure POJO test (no Spring). Verifies getters/setters.
 */
class UserEntityTest {

    @Test
    void gettersAndSetters_work() {
        User u = new User();
        u.setId(42L);
        u.setName("Kass");
        u.setEmail("k@example.com");
        u.setPassword("pw");

        assertEquals(42L, u.getId());
        assertEquals("Kass", u.getName());
        assertEquals("k@example.com", u.getEmail());
        assertEquals("pw", u.getPassword());
    }
}

