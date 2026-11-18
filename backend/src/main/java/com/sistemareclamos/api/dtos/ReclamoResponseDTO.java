package com.sistemareclamos.api.dtos;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat; // Añadir esta importación

public record ReclamoResponseDTO (
        Long idReclamo,
        Long idCiudadano,
        String nombreCiudadano,
        String dniCiudadano,
        Long idCategoria,
        String nombreCategoria,
        Long idArea,
        String nombreArea,
        Long idUsuarioAsignado,
        String nombreUsuarioAsignado,
        String asunto,
        String descripcion,
        String estado,
        String prioridad,
        
        // Añadir esta línea exacta sobre tu fecha:
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fechaRegistro,

        List<SeguimientoResponseDTO> historial
) {}
