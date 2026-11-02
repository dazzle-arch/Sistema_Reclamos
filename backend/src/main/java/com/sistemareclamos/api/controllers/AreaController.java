package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.AreaRequestDTO;
import com.sistemareclamos.api.dtos.AreaResponseDTO;
import com.sistemareclamos.api.services.AreaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AreaController {

    private final AreaService service;

    @GetMapping
    public List<AreaResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public AreaResponseDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AreaResponseDTO crear(@Valid @RequestBody AreaRequestDTO dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public AreaResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody AreaRequestDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
