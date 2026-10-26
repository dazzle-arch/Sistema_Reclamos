package com.sistemareclamos.api.dtos;

import java.time.LocalDateTime;

public record DocumentoResponseDTO(
        Long idDocumento,
        String nombreArchivo,
        String tipoArchivo,
        String descripcion,
        LocalDateTime fechaCarga,
        Long idReclamo
) {}
