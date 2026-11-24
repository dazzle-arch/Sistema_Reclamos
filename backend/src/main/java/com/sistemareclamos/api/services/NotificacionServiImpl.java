package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.NotificacionResponseDTO;
import com.sistemareclamos.api.entities.Notificacion;
import com.sistemareclamos.api.entities.Reclamo;
import com.sistemareclamos.api.exception.AccesoDenegadoException;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.repositories.NotificacionRepository;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import com.sistemareclamos.api.security.AccesoReclamo;
import com.sistemareclamos.api.security.UsuarioPrincipal;
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
    private final AccesoReclamo acceso;

    @Override
    public List<NotificacionResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public NotificacionResponseDTO obtenerPorId(Long id, UsuarioPrincipal usuario) {
        Notificacion n = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con id: " + id));
        boolean esDueno = n.getUsuarioDestino() != null
                && n.getUsuarioDestino().getIdUsuario().equals(usuario.getIdUsuario());
        if (!esDueno && !acceso.esAdmin(usuario)) {
            throw new AccesoDenegadoException("No tienes acceso a esta notificación");
        }
        return toResponse(n);
    }

    @Override
    public List<NotificacionResponseDTO> listarPorUsuario(Long idUsuario, UsuarioPrincipal usuario) {
        if (!idUsuario.equals(usuario.getIdUsuario()) && !acceso.esAdmin(usuario)) {
            throw new AccesoDenegadoException("No puedes ver las notificaciones de otro usuario");
        }
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id: " + idUsuario);
        }
        return repository.findByUsuarioDestinoIdUsuarioOrderByFechaEnvioDesc(idUsuario)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<NotificacionResponseDTO> listarMisNotificaciones(UsuarioPrincipal usuario) {
        return repository.findByUsuarioDestinoIdUsuarioOrderByFechaEnvioDesc(usuario.getIdUsuario())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<NotificacionResponseDTO> listarPorReclamo(Long idReclamo, UsuarioPrincipal usuario) {
        Reclamo reclamo = reclamoRepository.findById(idReclamo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reclamo no encontrado con id: " + idReclamo));
        acceso.verificarLectura(reclamo, usuario);
        return repository.findByReclamoIdReclamoOrderByFechaEnvioDesc(idReclamo)
                .stream().map(this::toResponse).toList();
    }
    @Override
    @Transactional
    public NotificacionResponseDTO marcarLeida(Long id, UsuarioPrincipal usuario) {
        Notificacion n = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con id: " + id));
        // Solo el dueño: ni siquiera el admin marca las de otro
        if (n.getUsuarioDestino() == null
                || !n.getUsuarioDestino().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new AccesoDenegadoException("No puedes modificar esta notificación");
        }
        n.setLeida(true);
        return toResponse(repository.save(n));
    }

    @Override
    @Transactional
    public int marcarTodasLeidas(UsuarioPrincipal usuario) {
        return repository.marcarTodasLeidas(usuario.getIdUsuario());
    }

    @Override
    public long contarNoLeidas(UsuarioPrincipal usuario) {
        return repository.contarNoLeidas(usuario.getIdUsuario());
    }

    private NotificacionResponseDTO toResponse(Notificacion n) {
        return new NotificacionResponseDTO(
                n.getIdNotificacion(),
                n.getReclamo().getIdReclamo(),
                n.getUsuarioDestino() != null ? n.getUsuarioDestino().getIdUsuario() : null,
                n.getAsunto(),
                n.getMensaje(),
                n.getFechaEnvio(),
                n.getEstado(),
                Boolean.TRUE.equals(n.getLeida())
        );
    }
}