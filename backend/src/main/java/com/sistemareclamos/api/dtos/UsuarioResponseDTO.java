package com.sistemareclamos.api.dtos;

public record UsuarioResponseDTO(
        Long idUsuario,
        Long idRol,
        Long idArea,
        String nombreUsuario,
        String nombres,
        String correo,
        Boolean estado,
        String nombreRol,
        String nombreArea
) {}
