package com.equipo5.backend.service;

import com.equipo5.backend.model.UserEntity;
import io.jsonwebtoken.Claims;

import java.util.function.Function;

public interface JwtService {
    String generateToken(UserEntity user);
    String extractEmail(String token);
    Long extractUserId(String token);
    boolean isTokenValid(String token, UserEntity user);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}