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
    public List<ReclamoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ReclamoResponseDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReclamoResponseDTO crear(@Valid @RequestBody ReclamoRequestDTO dto) {
        return service.crear(dto);
    }

    @PatchMapping("/{id}/estado")
    public ReclamoResponseDTO actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        return service.actualizarEstado(id, estado);
    }
}
