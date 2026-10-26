package com.sistemareclamos.api.dtos;
import jakarta.validation.constraints.*;

public record ReclamoRequestDTO(

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 120)
        String apellidos,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String nombres,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe contener exactamente 8 dígitos")
        String dni,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener exactamente 9 dígitos")
        String telefono,

        @NotBlank(message = "El sexo es obligatorio")
        @Pattern(regexp = "^[FM]$", message = "El sexo debe ser F o M")
        String sexo,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 160)
        String correo,

        @NotBlank(message = "El asunto es obligatorio")
        @Size(max = 150, message = "El asunto no debe superar los 150 caracteres")
        String asunto,

        @NotNull(message = "El tipo de reclamo es obligatorio")
        Long tipoReclamo,  // este es el idCategoria

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
        String descripcion
) {}
