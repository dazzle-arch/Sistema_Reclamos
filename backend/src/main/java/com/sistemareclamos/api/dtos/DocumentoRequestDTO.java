package com.sistemareclamos.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DocumentoRequestDTO(
        @NotNull(message = "El reclamo es obligatorio")
        Long idReclamo,

        @NotBlank(message = "El nombre del archivo es obligatorio")
        @Size(max = 255)
        String nombreArchivo,

        @NotBlank(message = "El tipo de archivo es obligatorio")
        @Size(max = 100)
        String tipoArchivo,

        @Size(max = 255)
        String descripcion
) {}
