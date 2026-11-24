package com.sistemareclamos.api.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ConsultaPublicaDTO(
        Long codigo,
        String asunto,
        String categoria,
        String area,
        String estado,
        LocalDateTime fechaRegistro,
        List<MovimientoPublicoDTO> historial
) {}