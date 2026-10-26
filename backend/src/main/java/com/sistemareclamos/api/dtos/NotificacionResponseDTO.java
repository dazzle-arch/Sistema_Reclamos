package com.sistemareclamos.api.dtos;

import java.time.LocalDateTime;

public record NotificacionResponseDTO(
        Long idNotificacion,
        Long idReclamo,
        Long idUsuarioDestino,
        String asunto,
        String mensaje,
        LocalDateTime fechaEnvio,
        String estado
) {}
