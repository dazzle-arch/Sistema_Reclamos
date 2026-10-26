package com.sistemareclamos.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(max = 60)
        String nombreUsuario,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 120)
        String nombres,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 160)
        String correo,

        // Opcional: obligatoria al crear (se valida en el servicio), opcional al actualizar
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String contrasena,

        @NotNull(message = "El rol es obligatorio")
        Long idRol,

        @NotNull(message = "El área es obligatoria")
        Long idArea
) {}
