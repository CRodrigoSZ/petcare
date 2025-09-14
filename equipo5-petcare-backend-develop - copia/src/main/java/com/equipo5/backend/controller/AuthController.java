package com.equipo5.backend.controller;

import com.equipo5.backend.model.UserEntity;
import com.equipo5.backend.model.dtos.request.UserRequestDTO;
import com.equipo5.backend.model.dtos.response.UserResponseDTO;
import com.equipo5.backend.model.enums.Role;
import com.equipo5.backend.service.UserService;
import com.equipo5.backend.serviceImpl.JwtService;
import com.equipo5.backend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    // Registro de owners - retorna token JWT
    @PostMapping("/owners/register")
    public ResponseEntity<TokenResponse> registerOwner(@Valid @RequestBody UserRequestDTO request) {
        Long userId = userService.createUser(request);

        UserEntity user = userRepository.findById(userId).orElseThrow();
        user.setRol(Role.OWNER);
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(token));
    }

    // Registro de sitters - retorna token JWT
    @PostMapping("/sitters/register")
    public ResponseEntity<TokenResponse> registerSitter(@Valid @RequestBody UserRequestDTO request) {
        Long userId = userService.createUser(request);

        UserEntity user = userRepository.findById(userId).orElseThrow();
        user.setRol(Role.SITTER);
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(token));
    }

    // Login de owners - retorna token JWT
    @PostMapping("/owners/login")
    public ResponseEntity<TokenResponse> loginOwner(@RequestBody LoginRequest loginRequest) {
        if (userRepository.existsByEmail(loginRequest.email())) {
            UserEntity user = userRepository.findByEmail(loginRequest.email()).orElse(null);
            if (user != null && user.getRol() == Role.OWNER) {
                String token = jwtService.generateToken(user);
                return ResponseEntity.ok(new TokenResponse(token));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Login de sitters - retorna token JWT
    @PostMapping("/sitters/login")
    public ResponseEntity<TokenResponse> loginSitter(@RequestBody LoginRequest loginRequest) {
        if (userRepository.existsByEmail(loginRequest.email())) {
            UserEntity user = userRepository.findByEmail(loginRequest.email()).orElse(null);
            if (user != null && user.getRol() == Role.SITTER) {
                String token = jwtService.generateToken(user);
                return ResponseEntity.ok(new TokenResponse(token));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Verificación de token para owners
    @PostMapping("/owners/verify")
    public ResponseEntity<UserResponseDTO> verifyOwnerToken(@RequestBody TokenRequest tokenRequest) {
        try {
            String email = jwtService.extractEmail(tokenRequest.token());
            UserEntity user = userRepository.findByEmail(email).orElse(null);

            if (user != null && user.getRol() == Role.OWNER && jwtService.isTokenValid(tokenRequest.token(), user)) {
                UserResponseDTO userResponse = userService.readUser(user.getId());
                return ResponseEntity.ok(userResponse);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Verificación de token para sitters
    @PostMapping("/sitters/verify")
    public ResponseEntity<UserResponseDTO> verifySitterToken(@RequestBody TokenRequest tokenRequest) {
        try {
            String email = jwtService.extractEmail(tokenRequest.token());
            UserEntity user = userRepository.findByEmail(email).orElse(null);

            if (user != null && user.getRol() == Role.SITTER && jwtService.isTokenValid(tokenRequest.token(), user)) {
                UserResponseDTO userResponse = userService.readUser(user.getId());
                return ResponseEntity.ok(userResponse);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Método auxiliar para obtener ID por email
    private Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::getId)
                .orElse(null);
    }

    // DTOs para requests y responses
    public record LoginRequest(String email, String password) {}
    public record TokenRequest(String token) {}
    public record TokenResponse(String token) {}
}