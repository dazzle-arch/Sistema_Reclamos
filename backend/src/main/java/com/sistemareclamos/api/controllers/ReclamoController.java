 package com.sistemareclamos.api.controllers;
import com.sistemareclamos.api.dtos.ReclamoRequestDTO;
import com.sistemareclamos.api.dtos.ReclamoResponseDTO;
import com.sistemareclamos.api.services.ReclamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamos")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ReclamoController {

    private final ReclamoService service;

    @GetMapping
    public ResponseEntity<List<ReclamoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReclamoResponseDTO> obtenerPorId(@PathVariable Long id) {
        ReclamoResponseDTO reclamo = service.obtenerPorId(id);
        return reclamo != null ? ResponseEntity.ok(reclamo) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ReclamoResponseDTO> crear(@Valid @RequestBody ReclamoRequestDTO dto) {
        ReclamoResponseDTO nuevo = service.crear(dto);
        return nuevo != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(nuevo)
                : ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ReclamoResponseDTO> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        ReclamoResponseDTO actualizado = service.actualizarEstado(id, estado);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }
}
