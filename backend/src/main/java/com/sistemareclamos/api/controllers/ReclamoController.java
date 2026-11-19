package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.AtencionRequestDTO;
import com.sistemareclamos.api.dtos.DerivarRequestDTO;
import com.sistemareclamos.api.dtos.ReclamoRequestDTO;
import com.sistemareclamos.api.dtos.ReclamoResponseDTO;
import com.sistemareclamos.api.services.ReclamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

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

    @PatchMapping("/{id}/derivar")
    public ReclamoResponseDTO derivar(@PathVariable Long id, @Valid @RequestBody DerivarRequestDTO dto) {
        return service.derivar(id, dto);
    }

    @GetMapping("/mis-casos")
    public List<ReclamoResponseDTO> listarMisCasos(Authentication authentication) {
        // Spring Security lee el token y nos da el nombre de usuario automáticamente
        String username = authentication.getName();
        return service.listarMisCasos(username);
    }

    @PostMapping("/{id}/atenciones")
    public ResponseEntity<ReclamoResponseDTO> registrarAtencion(
            @PathVariable Long id,
            @Valid @RequestBody AtencionRequestDTO dto,
            Authentication authentication) { // Spring inyecta esto mágicamente gracias a tu JWT

        // Obtenemos el nombre del funcionario directamente del Token de seguridad
        String nombreUsuarioFuncionario = authentication.getName();

        ReclamoResponseDTO reclamoActualizado = service.registrarAtencion(id, dto, nombreUsuarioFuncionario);

        return ResponseEntity.ok(reclamoActualizado);
    }
}
