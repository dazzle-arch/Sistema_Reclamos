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

    @Override
    public List<SeguimientoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public SeguimientoResponseDTO obtenerPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("Seguimiento no encontrado con id: " + id));
    }

    @Override
    public List<SeguimientoResponseDTO> listarPorReclamo(Long idReclamo) {
        if (!reclamoRepository.existsById(idReclamo)) {
            throw new RecursoNoEncontradoException("Reclamo no encontrado con id: " + idReclamo);
        }
        return repository.findByReclamoIdReclamoOrderByFechaHoraAsc(idReclamo)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public SeguimientoResponseDTO crear(SeguimientoRequestDTO dto) {
        String estado = dto.estado().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new SolicitudInvalidaException("Estado no permitido. Valores válidos: " + ESTADOS_VALIDOS);
        }

        Reclamo reclamo = reclamoRepository.findById(dto.idReclamo())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reclamo no encontrado con id: " + dto.idReclamo()));
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + dto.idUsuario()));

        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setReclamo(reclamo);
        seguimiento.setUsuario(usuario);
        seguimiento.setFechaHora(LocalDateTime.now());
        seguimiento.setEstado(estado);
        seguimiento.setObservacion(dto.observacion());
        seguimiento.setRespuesta(dto.respuesta());
        seguimiento.setAccionRealizada(dto.accionRealizada());

        // El reclamo ya está gestionado por la transacción: el cambio se guarda solo
        reclamo.setEstado(estado);

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
