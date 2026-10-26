package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.UsuarioRequestDTO;
import com.sistemareclamos.api.dtos.UsuarioResponseDTO;
import java.util.List;

public interface UsuarioService {
    List<UsuarioResponseDTO> listar();
    UsuarioResponseDTO obtenerPorId(Long id);
    UsuarioResponseDTO obtenerPorNombreUsuario(String nombreUsuario);
    UsuarioResponseDTO crear(UsuarioRequestDTO dto);
    UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto);
    boolean eliminar(Long id);
}
