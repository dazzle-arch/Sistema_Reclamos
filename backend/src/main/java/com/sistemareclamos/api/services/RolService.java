package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.RolRequestDTO;
import com.sistemareclamos.api.dtos.RolResponseDTO;
import java.util.List;

public interface RolService {
    List<RolResponseDTO> listar();
    RolResponseDTO obtenerPorId(Long id);
    RolResponseDTO crear(RolRequestDTO dto);
    RolResponseDTO actualizar(Long id, RolRequestDTO dto);
    boolean eliminar(Long id);
}
