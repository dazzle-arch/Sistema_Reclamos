package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.NotificacionResponseDTO;
import com.sistemareclamos.api.services.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class NotificacionController {
    private final NotificacionService service;

    @GetMapping
    public List<NotificacionResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        NotificacionResponseDTO dto = service.obtenerPorId(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

}
