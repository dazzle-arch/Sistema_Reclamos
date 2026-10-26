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
    public ResponseEntity<AreaResponseDTO> obtenerPorId(@PathVariable Long id) {
        AreaResponseDTO dto = service.obtenerPorId(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<AreaResponseDTO> crear(@Valid @RequestBody AreaRequestDTO dto) {
        AreaResponseDTO creado = service.crear(dto);
        return creado == null
                ? ResponseEntity.badRequest().build()
                : ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AreaRequestDTO dto) {
        AreaResponseDTO actualizado = service.actualizar(id, dto);
        return actualizado == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!service.eliminar(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
