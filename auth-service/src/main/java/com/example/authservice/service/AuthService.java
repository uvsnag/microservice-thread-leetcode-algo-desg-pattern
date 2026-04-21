package com.example.authservice.service;

import java.util.Map;

import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import com.example.authservice.dto.RefreshRequest;
import com.example.authservice.dto.TokenValidationResponse;
import com.example.authservice.event.AuthEventPublisher;
import com.example.authservice.mapper.AuthUserMapper;
import com.example.authservice.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AuthUserMapper authUserMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthEventPublisher authEventPublisher;

    public AuthService(AuthUserMapper authUserMapper, JwtTokenProvider jwtTokenProvider, AuthEventPublisher authEventPublisher) {
        this.authUserMapper = authUserMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authEventPublisher = authEventPublisher;
    }

    public LoginResponse login(LoginRequest request) {
        Map<String, Object> credentials = authUserMapper.findCredentials(request.coCd(), request.usrId());

        if (credentials == null) {
            authEventPublisher.publishLoginFailure(request.coCd(), request.usrId(), "User not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String actFlg = (String) credentials.get("actFlg");
        if ("N".equalsIgnoreCase(actFlg)) {
            authEventPublisher.publishLoginFailure(request.coCd(), request.usrId(), "Account inactive");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is inactive");
        }

        String storedPassword = (String) credentials.get("usrPwd");
        if (!verifyPassword(request.password(), storedPassword)) {
            authEventPublisher.publishLoginFailure(request.coCd(), request.usrId(), "Wrong password");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String usrId = (String) credentials.get("usrId");
        String coCd = (String) credentials.get("coCd");
        String usrNm = (String) credentials.get("usrNm");
        if (usrNm == null) {
            usrNm = (String) credentials.get("fullNm");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(usrId, coCd, usrNm);
        String refreshToken = jwtTokenProvider.generateRefreshToken(usrId, coCd);

        authEventPublisher.publishLoginSuccess(coCd, usrId, usrNm);

        return new LoginResponse(accessToken, refreshToken,
                jwtTokenProvider.getAccessTokenExpirationMs() / 1000,
                usrId, coCd, usrNm);
    }

    public LoginResponse refresh(RefreshRequest request) {
        if (!jwtTokenProvider.validateToken(request.refreshToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        Claims claims = jwtTokenProvider.parseToken(request.refreshToken());
        String type = claims.get("type", String.class);
        if (!"refresh".equals(type)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is not a refresh token");
        }

        String usrId = claims.getSubject();
        String coCd = claims.get("coCd", String.class);

        Map<String, Object> credentials = authUserMapper.findCredentials(coCd, usrId);
        if (credentials == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User no longer exists");
        }

        String usrNm = (String) credentials.get("usrNm");
        String accessToken = jwtTokenProvider.generateAccessToken(usrId, coCd, usrNm);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(usrId, coCd);

        authEventPublisher.publishTokenRefresh(coCd, usrId);

        return new LoginResponse(accessToken, newRefreshToken,
                jwtTokenProvider.getAccessTokenExpirationMs() / 1000,
                usrId, coCd, usrNm);
    }

    public TokenValidationResponse validate(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return new TokenValidationResponse(false, null, null, null);
        }
        Claims claims = jwtTokenProvider.parseToken(token);
        return new TokenValidationResponse(
                true,
                claims.getSubject(),
                claims.get("coCd", String.class),
                claims.get("usrNm", String.class)
        );
    }

    private boolean verifyPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$1$")) {
            // MD5 crypt hash: $1$salt$hash
            String computed = Md5Crypt.md5Crypt(rawPassword.getBytes(), storedPassword);
            return storedPassword.equals(computed);
        }
        // Fallback: plain-text comparison
        return storedPassword.equals(rawPassword);
    }
}
