package com.sistemareclamos.api.dtos;

import java.time.LocalDateTime;

public record ReclamoCreadoDTO(
        Long idReclamo,
        String estado,
        LocalDateTime fechaRegistro
) {}