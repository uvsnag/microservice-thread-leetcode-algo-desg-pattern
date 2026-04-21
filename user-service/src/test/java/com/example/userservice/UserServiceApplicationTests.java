package com.example.userservice;

import org.junit.jupiter.api.Test;

/**
 * Aggregation test class. Actual unit tests are in:
 * - service/UserServiceTest (service layer unit tests)
 * - controller/UserControllerTest (controller layer unit tests)
 *
 * Full context-load test is skipped because MyBatis requires the real
 * PostgreSQL schema (adm_usr table). Run integration tests with Docker.
 */
class UserServiceApplicationTests {

    @Test
    void applicationClassExists() {
        // Verify the main class can be loaded (no context startup)
        UserServiceApplication.class.getName();
    }
}
