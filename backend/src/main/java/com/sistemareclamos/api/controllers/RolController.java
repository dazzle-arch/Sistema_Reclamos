package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.RolRequestDTO;
import com.sistemareclamos.api.dtos.RolResponseDTO;
import com.sistemareclamos.api.services.RolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RolController {
    private final RolService service;

    @GetMapping
    public List<RolResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public RolResponseDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolResponseDTO crear(@Valid @RequestBody RolRequestDTO dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public RolResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody RolRequestDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
