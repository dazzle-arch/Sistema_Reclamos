package com.sistemareclamos.api.dtos;

import java.time.LocalDateTime;

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
        LocalDateTime fechaRegistro
) {}
