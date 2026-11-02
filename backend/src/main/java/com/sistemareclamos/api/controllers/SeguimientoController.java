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
    public SeguimientoResponseDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/reclamo/{idReclamo}")
    public List<SeguimientoResponseDTO> listarPorReclamo(@PathVariable Long idReclamo) {
        return service.listarPorReclamo(idReclamo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeguimientoResponseDTO crear(@Valid @RequestBody SeguimientoRequestDTO dto) {
        return service.crear(dto);
    }
}
