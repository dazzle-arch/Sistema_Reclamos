package com.sistemareclamos.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RolRequestDTO(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    String nombre,
    @Size(max = 255)
    String descripcion,
    @Size(max = 1000)
    String permisos
) {}
// posible eliminacion