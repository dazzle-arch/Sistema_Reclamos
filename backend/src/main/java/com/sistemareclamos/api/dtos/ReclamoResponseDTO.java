package com.sistemareclamos.api.dtos;

import java.time.LocalDateTime;

public record ReclamoResponseDTO (
        Long idReclamo,
        Long idCiudadano,
        String nombreCiudadano,
        String dniCiudadano,
        Long idCategoria,
        String nombreCategoria,
        String asunto,
        String descripcion,
        String estado,
        String prioridad,
        LocalDateTime fechaRegistro
) {}
