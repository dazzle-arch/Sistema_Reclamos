package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.ReclamoRequestDTO;
import com.sistemareclamos.api.dtos.ReclamoResponseDTO;
import java.util.List;
public interface ReclamoService {
    List<ReclamoResponseDTO> listar();
    ReclamoResponseDTO obtenerPorId(Long id);
    ReclamoResponseDTO crear(ReclamoRequestDTO dto);
    ReclamoResponseDTO actualizarEstado(Long id, String nuevoEstado);
}
