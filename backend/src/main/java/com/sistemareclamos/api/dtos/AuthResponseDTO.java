package com.sistemareclamos.api.dtos;

public record AuthResponseDTO(
        String token,
        String nombreUsuario,
        Long idRol,
        Long idArea,
        Long idUsuario
) {}