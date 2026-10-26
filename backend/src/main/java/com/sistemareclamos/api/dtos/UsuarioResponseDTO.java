package com.sistemareclamos.api.dtos;

public record UsuarioResponseDTO(
        Long idUsuario,
        String nombreUsuario,
        String nombres,
        String correo,
        String estado,
        Long idRol,
        String nombreRol,
        Long idArea,
        String nombreArea
) {}
