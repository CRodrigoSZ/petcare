package com.equipo5.backend.controller;

import com.equipo5.backend.model.dtos.request.UserRequestDTO;
import com.equipo5.backend.model.dtos.response.UserResponseDTO;
import com.equipo5.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Long> createUser(@Valid @RequestBody UserRequestDTO request) {
        Long userId = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> users = userService.readAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.readUser(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO request) {
        userService.updateUser(request, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}