package com.example.userservice.controller;

import java.util.List;

import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "Read-only user operations (MyBatis + PostgreSQL)")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "List all users")
    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get user by composite key (coCd + usrId)")
    @GetMapping("/{coCd}/{usrId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable String coCd,
            @PathVariable String usrId) {
        return userService.getUserByKey(coCd, usrId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "List users by company code")
    @GetMapping("/company/{coCd}")
    public List<UserResponse> getUsersByCompany(@PathVariable String coCd) {
        return userService.getUsersByCompany(coCd);
    }

    @Operation(summary = "Search users by keyword (name or email)")
    @GetMapping("/search")
    public List<UserResponse> searchUsers(@RequestParam String keyword) {
        return userService.searchUsers(keyword);
    }

    @Operation(summary = "List users by age")
    @GetMapping("/by-age/{age}")
    public List<UserResponse> getUsersByAge(@PathVariable Integer age) {
        return userService.getUsersByAge(age);
    }

    @Operation(summary = "Count total users")
    @GetMapping("/count")
    public long countUsers() {
        return userService.countUsers();
    }
}
