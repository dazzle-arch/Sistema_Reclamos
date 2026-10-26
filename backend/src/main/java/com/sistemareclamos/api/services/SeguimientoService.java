package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.SeguimientoRequestDTO;
import com.sistemareclamos.api.dtos.SeguimientoResponseDTO;
import java.util.List;

public interface SeguimientoService {
    List<SeguimientoResponseDTO> listar();
    SeguimientoResponseDTO obtenerPorId(Long id);
    SeguimientoResponseDTO crear(SeguimientoRequestDTO dto);
}
