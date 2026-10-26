package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.SeguimientoRequestDTO;
import com.sistemareclamos.api.dtos.SeguimientoResponseDTO;
import com.sistemareclamos.api.services.SeguimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seguimientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SeguimientoController {
    private final SeguimientoService service;

    @GetMapping
    public List<SeguimientoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeguimientoResponseDTO> obtenerPorId(@PathVariable Long id) {
        SeguimientoResponseDTO dto = service.obtenerPorId(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<SeguimientoResponseDTO> crear(@Valid @RequestBody SeguimientoRequestDTO dto) {
        SeguimientoResponseDTO creado = service.crear(dto);
        return creado == null
                ? ResponseEntity.badRequest().build() // reclamo o usuario no existen
                : ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
