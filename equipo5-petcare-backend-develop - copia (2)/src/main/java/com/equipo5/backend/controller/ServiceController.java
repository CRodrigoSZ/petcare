package com.equipo5.backend.controller;

import com.equipo5.backend.model.dtos.request.ServiceEntityRequestDTO;
import com.equipo5.backend.model.dtos.response.services.ServiceEntityResponseDTO;
import com.equipo5.backend.service.SitterService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/services")
@AllArgsConstructor
public class ServiceController {

    private final SitterService sitterService;

    @GetMapping
    public ResponseEntity<Page<ServiceEntityResponseDTO>> getAllServices(Pageable pageable) {
        Page<ServiceEntityResponseDTO> services = sitterService.getSittersDTO(pageable);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntityResponseDTO> getServiceById(@PathVariable Long id) {
        Optional<ServiceEntityResponseDTO> service = sitterService.getSitterById(id);
        return service.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sitter/{sitterId}")
    public ResponseEntity<ServiceEntityResponseDTO> createService(@PathVariable Long sitterId,
                                                                  @Valid @RequestBody ServiceEntityRequestDTO request) {
        ServiceEntityResponseDTO service = sitterService.createSitter(sitterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(service);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceEntityResponseDTO> updateService(@PathVariable Long id,
                                                                  @Valid @RequestBody ServiceEntityRequestDTO request) {
        Optional<ServiceEntityResponseDTO> service = sitterService.updateSitter(id, request);
        return service.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        sitterService.deleteSitter(id);
        return ResponseEntity.noContent().build();
    }
}