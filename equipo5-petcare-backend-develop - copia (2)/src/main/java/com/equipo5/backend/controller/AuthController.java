package com.equipo5.backend.controller;

import com.equipo5.backend.model.UserEntity;
import com.equipo5.backend.model.dtos.request.UserRequestDTO;
import com.equipo5.backend.model.dtos.response.UserResponseDTO;
import com.equipo5.backend.model.enums.Role;
import com.equipo5.backend.service.UserService;
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

    // Registro de dueños
    @PostMapping("/owners/register")
    public ResponseEntity<String> registerOwner(@Valid @RequestBody UserRequestDTO request) {
        Long userId = userService.createUser(request);

        // Actualizar rol después de la creación
        UserEntity user = userRepository.findById(userId).orElseThrow();
        user.setRol(Role.OWNER);
        userRepository.save(user);

        // Retornar token simulado para testing
        String mockToken = "mock-token-" + userId;
        return ResponseEntity.status(HttpStatus.CREATED).body(mockToken);
    }

    // Registro de cuidadores
    @PostMapping("/sitters/register")
    public ResponseEntity<Long> registerSitter(@Valid @RequestBody UserRequestDTO request) {
        Long userId = userService.createUser(request);

        // Actualizar rol después de la creación
        UserEntity user = userRepository.findById(userId).orElseThrow();
        user.setRol(Role.SITTER);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    // Login básico para dueños
    @PostMapping("/owners/login")
    public ResponseEntity<UserResponseDTO> loginOwner(@RequestBody LoginRequest loginRequest) {
        if (userRepository.existsByEmail(loginRequest.email())) {
            UserResponseDTO user = userService.readUser(getUserIdByEmail(loginRequest.email()));
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Login básico para cuidadores
    @PostMapping("/sitters/login")
    public ResponseEntity<UserResponseDTO> loginSitter(@RequestBody LoginRequest loginRequest) {
        if (userRepository.existsByEmail(loginRequest.email())) {
            UserResponseDTO user = userService.readUser(getUserIdByEmail(loginRequest.email()));
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Método auxiliar para obtener ID por email
    private Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::getId)
                .orElse(null);
    }

    // DTO simple para login
    public record LoginRequest(String email, String password) {}
}