package com.sistemareclamos.api.controllers;
import com.sistemareclamos.api.dtos.CategoriaRequestDTO;
import com.sistemareclamos.api.dtos.CategoriaResponseDTO;
import com.sistemareclamos.api.services.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaController {
    private final CategoriaService service;

    @GetMapping
    public List<CategoriaResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerPorId(@PathVariable Long id) {
        CategoriaResponseDTO dto = service.obtenerPorId(id);
        if (dto == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(dto); // 200
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO dto) {
        CategoriaResponseDTO creado = service.crear(dto);
        if (creado == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409: nombre repetido
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(creado); // 201
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) {
        CategoriaResponseDTO actualizado = service.actualizar(id, dto);
        if (actualizado == null) {
            return ResponseEntity.badRequest().build(); // no existe o nombre repetido
        }
        return ResponseEntity.ok(actualizado); // 200
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!service.eliminar(id)) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.noContent().build(); // 204
    }
}
