package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.UsuarioRequestDTO;
import com.sistemareclamos.api.dtos.UsuarioResponseDTO;
import com.sistemareclamos.api.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {
    private final UsuarioService service;

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/nombre-usuario/{nombreUsuario}")
    public UsuarioResponseDTO obtenerPorNombreUsuario(@PathVariable String nombreUsuario) {
        return service.obtenerPorNombreUsuario(nombreUsuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return service.actualizar(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public UsuarioResponseDTO cambiarEstado(@PathVariable Long id, @RequestParam boolean estado) {
        return service.cambiarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
