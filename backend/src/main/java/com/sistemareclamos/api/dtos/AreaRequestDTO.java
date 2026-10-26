package com.sistemareclamos.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AreaRequestDTO(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    String nombre,
    @Size(max = 255)
    String descripcion
    //NOTA: El area request no es obligatorio tenerlo por ahora pero hay que mantenerlo por si acaso
) {}
