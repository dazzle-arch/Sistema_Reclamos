package com.sistemareclamos.api.dtos;
import jakarta.validation.constraints.*;

public record CiudadanoRequestDTO(
        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
        String dni,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 120)
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 120)
        String apellidos,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Correo inválido")
        @Size(max = 160)
        String correo,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos")
        String telefono,

        @NotBlank(message = "El sexo es obligatorio")
        @Pattern(regexp = "[MF]", message = "El sexo debe ser 'M' o 'F'")
        String sexo

        //NOTA: El ciudadano request no es obligatorio tenerlo por ahora pero hay que mantenerlo por si acaso
){}


