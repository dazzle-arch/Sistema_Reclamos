package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.DocumentoRequestDTO;
import com.sistemareclamos.api.dtos.DocumentoResponseDTO;
import com.sistemareclamos.api.services.DocumentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentoController {
    private final DocumentoService service;

    @GetMapping
    public List<DocumentoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponseDTO> obtenerPorId(@PathVariable Long id) {
        DocumentoResponseDTO dto = service.obtenerPorId(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<DocumentoResponseDTO> crear(@Valid @RequestBody DocumentoRequestDTO dto) {
        DocumentoResponseDTO creado = service.crear(dto);
        return creado == null
                ? ResponseEntity.badRequest().build() // el reclamo no existe
                : ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
