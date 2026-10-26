package com.sistemareclamos.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NotificacionRequestDTO(
        @NotNull(message = "El reclamo es obligatorio")
        Long idReclamo,

        Long idUsuario, // null = notificación para el ciudadano; con valor = para un funcionario

        @NotBlank(message = "El asunto es obligatorio")
        @Size(max = 150)
        String asunto,

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 500)
        String mensaje
) {}
