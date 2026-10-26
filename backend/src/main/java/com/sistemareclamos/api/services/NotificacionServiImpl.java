package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.NotificacionResponseDTO;
import com.sistemareclamos.api.entities.Notificacion;
import com.sistemareclamos.api.repositories.NotificacionRepository;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServiImpl implements NotificacionService {
    private final NotificacionRepository repository;
    private final ReclamoRepository reclamoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<NotificacionResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public NotificacionResponseDTO obtenerPorId(Long id) {
        return repository.findById(id).map(this::toResponse).orElse(null);
    }

    private NotificacionResponseDTO toResponse(Notificacion n) {
        return new NotificacionResponseDTO(
                n.getIdNotificacion(),
                n.getReclamo().getIdReclamo(),
                n.getUsuarioDestino() != null ? n.getUsuarioDestino().getIdUsuario() : null,
                n.getAsunto(),
                n.getMensaje(),
                n.getFechaEnvio(),
                n.getEstado()
        );
    }
}