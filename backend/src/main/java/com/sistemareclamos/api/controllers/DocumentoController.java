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
    public DocumentoResponseDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/reclamo/{idReclamo}")
    public List<DocumentoResponseDTO> listarPorReclamo(@PathVariable Long idReclamo) {
        return service.listarPorReclamo(idReclamo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoResponseDTO crear(@Valid @RequestBody DocumentoRequestDTO dto) {
        return service.crear(dto);
    }
}
