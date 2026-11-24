package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.SeguimientoRequestDTO;
import com.sistemareclamos.api.dtos.SeguimientoResponseDTO;
import com.sistemareclamos.api.security.UsuarioPrincipal;

import java.util.List;

public interface SeguimientoService {
    List<SeguimientoResponseDTO> listar();
    SeguimientoResponseDTO obtenerPorId(Long id, UsuarioPrincipal usuario);
    List<SeguimientoResponseDTO> listarPorReclamo(Long idReclamo, UsuarioPrincipal usuario);
    SeguimientoResponseDTO crear(SeguimientoRequestDTO dto, UsuarioPrincipal usuario);
}
