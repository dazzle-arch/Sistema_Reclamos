package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.NotificacionResponseDTO;
import com.sistemareclamos.api.entities.Notificacion;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.repositories.NotificacionRepository;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con id: " + id));
    }

    @Override
    public List<NotificacionResponseDTO> listarPorUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id: " + idUsuario);
        }
        return repository.findByUsuarioDestinoIdUsuarioOrderByFechaEnvioDesc(idUsuario)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<NotificacionResponseDTO> listarPorReclamo(Long idReclamo) {
        if (!reclamoRepository.existsById(idReclamo)) {
            throw new RecursoNoEncontradoException("Reclamo no encontrado con id: " + idReclamo);
        }
        return repository.findByReclamoIdReclamoOrderByFechaEnvioDesc(idReclamo)
                .stream().map(this::toResponse).toList();
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