package com.sistemareclamos.api.dtos;

import jakarta.validation.constraints.NotBlank;

public record AtencionRequestDTO(
        @NotBlank(message = "El nuevo estado es obligatorio")
        String nuevoEstado,
        
        @NotBlank(message = "La acción realizada es obligatoria")
        String accionRealizada,
        
        String observacion,
        String nuevaPrioridad,
        String respuestaCiudadano,
        Long idAreaDerivada // Opcional, si se deriva
) {}