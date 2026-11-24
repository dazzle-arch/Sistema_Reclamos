package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.NotificacionResponseDTO;
import com.sistemareclamos.api.security.UsuarioPrincipal;
import com.sistemareclamos.api.services.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService service;

    @GetMapping
    public List<NotificacionResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/mis-notificaciones")
    public List<NotificacionResponseDTO> misNotificaciones(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.listarMisNotificaciones(usuario);
    }

    @GetMapping("/{id}")
    public NotificacionResponseDTO obtenerPorId(@PathVariable Long id,
                                                @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.obtenerPorId(id, usuario);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<NotificacionResponseDTO> listarPorUsuario(@PathVariable Long idUsuario,
                                                          @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.listarPorUsuario(idUsuario, usuario);
    }

    @GetMapping("/reclamo/{idReclamo}")
    public List<NotificacionResponseDTO> listarPorReclamo(@PathVariable Long idReclamo,
                                                          @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.listarPorReclamo(idReclamo, usuario);
    }
    @GetMapping("/mis-notificaciones/no-leidas/cantidad")
    public Map<String, Long> cantidadNoLeidas(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return Map.of("cantidad", service.contarNoLeidas(usuario));
    }

    @PatchMapping("/leidas")
    public Map<String, Integer> marcarTodasLeidas(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return Map.of("actualizadas", service.marcarTodasLeidas(usuario));
    }

    @PatchMapping("/{id}/leida")
    public NotificacionResponseDTO marcarLeida(@PathVariable Long id,
                                               @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.marcarLeida(id, usuario);
    }
}