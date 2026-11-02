package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.SeguimientoRequestDTO;
import com.sistemareclamos.api.dtos.SeguimientoResponseDTO;
import java.util.List;

public interface SeguimientoService {
    List<SeguimientoResponseDTO> listar();
    SeguimientoResponseDTO obtenerPorId(Long id);
    List<SeguimientoResponseDTO> listarPorReclamo(Long idReclamo);
    SeguimientoResponseDTO crear(SeguimientoRequestDTO dto);
}
