package com.sistemareclamos.api.dtos;
import jakarta.validation.constraints.*;
public record CategoriaRequestDTO (

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede pasar de 100 caracteres")
    String nombre,

    @Size(max = 255, message = "La descripción no puede pasar de 255 caracteres")
    String descripcion
    //NOTA: La categoria request no es obligatorio tenerlo por ahora pero hay que mantenerlo por si acaso
)   {}

