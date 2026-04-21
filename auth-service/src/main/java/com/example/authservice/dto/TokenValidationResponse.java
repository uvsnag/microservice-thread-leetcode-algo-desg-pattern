package com.example.authservice.dto;

public record TokenValidationResponse(
        boolean valid,
        String usrId,
        String coCd,
        String usrNm
) {
}
