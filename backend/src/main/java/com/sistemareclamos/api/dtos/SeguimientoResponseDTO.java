package com.sistemareclamos.api.dtos;

import java.time.LocalDateTime;

public record SeguimientoResponseDTO(
        Long idSeguimiento,
        LocalDateTime fechaHora,
        String estado,
        String observacion,
        String respuesta,
        String accionRealizada,
        Long idReclamo,
        Long idUsuario,
        String nombreUsuario
) {}
