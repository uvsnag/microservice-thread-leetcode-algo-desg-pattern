package com.example.userservice.controller;

import java.util.List;
import java.util.Optional;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse sampleUser() {
        return new UserResponse(
                "admin", "DEMO", "Admin User", "Admin Full Name",
                "admin@example.com", "010-1234-5678", 30, "Y",
                null, "KR", "Seoul", "HQ", "MAIN", "EMP001", "FT"
        );
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("GET /users - returns all users")
    void getUsers_shouldReturnList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].usrId", is("admin")))
                .andExpect(jsonPath("$[0].coCd", is("DEMO")));
    }

    @Test
    @DisplayName("GET /users/{coCd}/{usrId} - returns user when found")
    void getUserById_shouldReturnUser() throws Exception {
        when(userService.getUserByKey("DEMO", "admin")).thenReturn(Optional.of(sampleUser()));

        mockMvc.perform(get("/users/DEMO/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usrId", is("admin")))
                .andExpect(jsonPath("$.usrNm", is("Admin User")));
    }

    @Test
    @DisplayName("GET /users/{coCd}/{usrId} - returns 404 when not found")
    void getUserById_shouldReturn404WhenNotFound() throws Exception {
        when(userService.getUserByKey("DEMO", "unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/DEMO/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /users/company/{coCd} - returns company users")
    void getUsersByCompany_shouldReturnList() throws Exception {
        when(userService.getUsersByCompany("DEMO")).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/users/company/DEMO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /users/search?keyword=admin - returns search results")
    void searchUsers_shouldReturnResults() throws Exception {
        when(userService.searchUsers("admin")).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/users/search").param("keyword", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /users/by-age/30 - returns users by age")
    void getUsersByAge_shouldReturnList() throws Exception {
        when(userService.getUsersByAge(30)).thenReturn(List.of(sampleUser()));

        mockMvc.perform(get("/users/by-age/30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /users/count - returns user count")
    void countUsers_shouldReturnCount() throws Exception {
        when(userService.countUsers()).thenReturn(42L);

        mockMvc.perform(get("/users/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(42)));
    }
}
