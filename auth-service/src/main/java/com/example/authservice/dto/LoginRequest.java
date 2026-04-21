package com.example.authservice.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String coCd,
        @NotBlank String usrId,
        @NotBlank String password
) {
}
