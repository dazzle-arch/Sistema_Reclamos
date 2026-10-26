package com.sistemareclamos.api.dtos;

public record RolResponseDTO(
        Long idRol,
        String nombre,
        String descripcion,
        String permisos
) {}
