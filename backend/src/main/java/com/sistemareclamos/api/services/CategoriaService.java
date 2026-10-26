package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.CategoriaRequestDTO;
import com.sistemareclamos.api.dtos.CategoriaResponseDTO;
import java.util.List;
public interface CategoriaService {

    List<CategoriaResponseDTO> listar();
    CategoriaResponseDTO obtenerPorId(Long id);
    CategoriaResponseDTO crear(CategoriaRequestDTO dto);
    CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto);
    boolean eliminar(Long id);
}
