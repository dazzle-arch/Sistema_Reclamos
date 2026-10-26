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
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        UsuarioResponseDTO dto = service.obtenerPorId(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/nombre-usuario/{nombreUsuario}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorNombreUsuario(@PathVariable String nombreUsuario) {
        UsuarioResponseDTO dto = service.obtenerPorNombreUsuario(nombreUsuario);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO creado = service.crear(dto);
        return creado == null
                ? ResponseEntity.status(HttpStatus.CONFLICT).build() // 409: repetido o datos inválidos
                : ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO actualizado = service.actualizar(id, dto);
        return actualizado == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!service.eliminar(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
