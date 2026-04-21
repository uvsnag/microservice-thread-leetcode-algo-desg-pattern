package com.example.authservice.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        String usrId,
        String coCd,
        String usrNm
) {
    public LoginResponse(String accessToken, String refreshToken, long expiresIn,
                         String usrId, String coCd, String usrNm) {
        this(accessToken, refreshToken, "Bearer", expiresIn, usrId, coCd, usrNm);
    }
}
