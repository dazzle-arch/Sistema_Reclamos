package com.sistemareclamos.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtencionRequestDTO(
        @NotBlank(message = "El nuevo estado es obligatorio")
        String nuevoEstado,

        @NotBlank(message = "La acción realizada es obligatoria")
        @Size(max = 350)
        String accionRealizada,

        @Size(max = 1000)
        String observacion,

        String nuevaPrioridad,

        @Size(max = 1000)
        String respuestaCiudadano,

        Long idAreaDerivada
) {}