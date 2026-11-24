package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.SeguimientoRequestDTO;
import com.sistemareclamos.api.dtos.SeguimientoResponseDTO;
import com.sistemareclamos.api.security.UsuarioPrincipal;
import com.sistemareclamos.api.services.SeguimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seguimientos")
@RequiredArgsConstructor
public class SeguimientoController {

    private final SeguimientoService service;

    @GetMapping
    public List<SeguimientoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public SeguimientoResponseDTO obtenerPorId(@PathVariable Long id,
                                               @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.obtenerPorId(id, usuario);
    }

    @GetMapping("/reclamo/{idReclamo}")
    public List<SeguimientoResponseDTO> listarPorReclamo(@PathVariable Long idReclamo,
                                                         @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.listarPorReclamo(idReclamo, usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeguimientoResponseDTO crear(@Valid @RequestBody SeguimientoRequestDTO dto,
                                        @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.crear(dto, usuario);
    }
}
