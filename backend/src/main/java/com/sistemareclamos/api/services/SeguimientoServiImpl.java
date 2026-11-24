package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.SeguimientoRequestDTO;
import com.sistemareclamos.api.dtos.SeguimientoResponseDTO;
import com.sistemareclamos.api.entities.Reclamo;
import com.sistemareclamos.api.entities.Seguimiento;
import com.sistemareclamos.api.entities.Usuario;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.exception.SolicitudInvalidaException;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import com.sistemareclamos.api.repositories.SeguimientoRepository;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import com.sistemareclamos.api.security.AccesoReclamo;
import com.sistemareclamos.api.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeguimientoServiImpl implements SeguimientoService {

    private static final List<String> ESTADOS_VALIDOS =
            List.of("REGISTRADO", "EN_PROCESO", "RESUELTO", "CERRADO");

    private final SeguimientoRepository repository;
    private final ReclamoRepository reclamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AccesoReclamo acceso;

    @Override
    public List<SeguimientoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public SeguimientoResponseDTO obtenerPorId(Long id, UsuarioPrincipal usuario) {
        Seguimiento s = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Seguimiento no encontrado con id: " + id));
        acceso.verificarLectura(s.getReclamo(), usuario);
        return toResponse(s);
    }

    @Override
    public List<SeguimientoResponseDTO> listarPorReclamo(Long idReclamo, UsuarioPrincipal usuario) {
        Reclamo reclamo = reclamoRepository.findById(idReclamo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reclamo no encontrado con id: " + idReclamo));
        acceso.verificarLectura(reclamo, usuario);
        return repository.findByReclamoIdReclamoOrderByFechaHoraAsc(idReclamo)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public SeguimientoResponseDTO crear(SeguimientoRequestDTO dto, UsuarioPrincipal principal) {
        String estado = dto.estado().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new SolicitudInvalidaException("Estado no permitido. Valores válidos: " + ESTADOS_VALIDOS);
        }

        Reclamo reclamo = reclamoRepository.findById(dto.idReclamo())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reclamo no encontrado con id: " + dto.idReclamo()));
        acceso.verificarGestion(reclamo, principal);

        if ("CERRADO".equals(reclamo.getEstado())) {
            throw new SolicitudInvalidaException("No se pueden registrar acciones en un reclamo cerrado");
        }

        // El responsable es el usuario del token, no el que diga el body
        Usuario usuario = usuarioRepository.findById(principal.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setReclamo(reclamo);
        seguimiento.setUsuario(usuario);
        seguimiento.setFechaHora(LocalDateTime.now());
        seguimiento.setEstado(estado);
        seguimiento.setObservacion(dto.observacion());
        seguimiento.setRespuesta(dto.respuesta());
        seguimiento.setAccionRealizada(dto.accionRealizada());

        reclamo.setEstado(estado);
        if (reclamo.getUsuarioAsignado() == null && !acceso.esAdmin(principal)) {
            reclamo.setUsuarioAsignado(usuario);
        }

        return toResponse(repository.save(seguimiento));
    }

    private SeguimientoResponseDTO toResponse(Seguimiento s) {
        return new SeguimientoResponseDTO(
                s.getIdSeguimiento(),
                s.getFechaHora(),
                s.getEstado(),
                s.getObservacion(),
                s.getRespuesta(),
                s.getAccionRealizada(),
                s.getReclamo().getIdReclamo(),
                s.getUsuario().getIdUsuario(),
                s.getUsuario().getNombres()
        );
    }
}