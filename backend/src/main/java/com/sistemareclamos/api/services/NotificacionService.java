package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.NotificacionResponseDTO;
import java.util.List;

public interface NotificacionService {
    List<NotificacionResponseDTO> listar();
    NotificacionResponseDTO obtenerPorId(Long id);
    List<NotificacionResponseDTO> listarPorUsuario(Long idUsuario);
    List<NotificacionResponseDTO> listarPorReclamo(Long idReclamo);
}
