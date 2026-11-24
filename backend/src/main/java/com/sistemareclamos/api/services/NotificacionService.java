package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.NotificacionResponseDTO;
import com.sistemareclamos.api.security.UsuarioPrincipal;

import java.util.List;

public interface NotificacionService {
    List<NotificacionResponseDTO> listar();
    NotificacionResponseDTO obtenerPorId(Long id, UsuarioPrincipal usuario);
    List<NotificacionResponseDTO> listarPorUsuario(Long idUsuario, UsuarioPrincipal usuario);
    List<NotificacionResponseDTO> listarMisNotificaciones(UsuarioPrincipal usuario);
    List<NotificacionResponseDTO> listarPorReclamo(Long idReclamo, UsuarioPrincipal usuario);
    NotificacionResponseDTO marcarLeida(Long id, UsuarioPrincipal usuario);
    int marcarTodasLeidas(UsuarioPrincipal usuario);
    long contarNoLeidas(UsuarioPrincipal usuario);

}
