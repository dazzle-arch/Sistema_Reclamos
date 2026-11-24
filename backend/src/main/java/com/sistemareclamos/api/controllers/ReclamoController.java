package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.*;
import com.sistemareclamos.api.security.UsuarioPrincipal;
import com.sistemareclamos.api.services.ReclamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamos")
@RequiredArgsConstructor
public class ReclamoController {

    private final ReclamoService service;

    @GetMapping
    public List<ReclamoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/consulta")
    public ConsultaPublicaDTO consultar(@RequestParam Long codigo, @RequestParam String dni) {
        return service.consultar(codigo, dni);
    }

    @GetMapping("/mis-casos")
    public List<ReclamoResponseDTO> listarMisCasos(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.listarMisCasos(usuario);
    }

    @GetMapping("/{id}")
    public ReclamoResponseDTO obtenerPorId(@PathVariable Long id,
                                           @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.obtenerPorId(id, usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReclamoCreadoDTO crear(@Valid @RequestBody ReclamoRequestDTO dto) {
        return service.crear(dto);
    }

    @PatchMapping("/{id}/estado")
    public ReclamoResponseDTO actualizarEstado(@PathVariable Long id,
                                               @RequestParam String estado,
                                               @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.actualizarEstado(id, estado, usuario);
    }

    @PatchMapping("/{id}/derivar")
    public ReclamoResponseDTO derivar(@PathVariable Long id,
                                      @Valid @RequestBody DerivarRequestDTO dto,
                                      @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.derivar(id, dto, usuario);
    }

    @PostMapping("/{id}/atenciones")
    public ReclamoResponseDTO registrarAtencion(@PathVariable Long id,
                                                @Valid @RequestBody AtencionRequestDTO dto,
                                                @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.registrarAtencion(id, dto, usuario);
    }
}
