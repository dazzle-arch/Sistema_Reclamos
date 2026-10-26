package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.AreaRequestDTO;
import com.sistemareclamos.api.dtos.AreaResponseDTO;
import java.util.List;

public interface AreaService {
    List<AreaResponseDTO> listar();
    AreaResponseDTO obtenerPorId(Long id);
    AreaResponseDTO crear(AreaRequestDTO dto);
    AreaResponseDTO actualizar(Long id, AreaRequestDTO dto);
    boolean eliminar(Long id);
}
