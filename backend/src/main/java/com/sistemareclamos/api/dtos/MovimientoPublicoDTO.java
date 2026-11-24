package com.sistemareclamos.api.dtos;

import java.time.LocalDateTime;

public record MovimientoPublicoDTO(
        LocalDateTime fecha,
        String estado,
        String respuesta
) {}