package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.CiudadanoRequestDTO;
import com.sistemareclamos.api.dtos.CiudadanoResponseDTO;

import java.util.List;

public interface CiudadanoService {
    List<CiudadanoResponseDTO> listar();
    CiudadanoResponseDTO obtenerPorId(Long id);
    CiudadanoResponseDTO obtenerPorDni(String dni);
    CiudadanoResponseDTO crear(CiudadanoRequestDTO dto);
    CiudadanoResponseDTO actualizar(Long id, CiudadanoRequestDTO dto);
    boolean eliminar(Long id);
}
