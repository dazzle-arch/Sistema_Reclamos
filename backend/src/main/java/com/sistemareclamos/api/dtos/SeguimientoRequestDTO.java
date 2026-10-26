package com.sistemareclamos.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.*;

public record SeguimientoRequestDTO(
        @NotNull(message = "El reclamo es obligatorio")
        Long idReclamo,

        @NotNull(message = "El usuario es obligatorio")
        Long idUsuario,

        @NotBlank(message = "El estado es obligatorio")
        @Pattern(regexp = "REGISTRADO|EN_PROCESO|RESUELTO|CERRADO", message = "Estado no válido")
        String estado,

        @Size(max = 1000)
        String observacion,

        @Size(max = 1000)
        String respuesta,

        @Size(max = 350)
        String accionRealizada
) {}
