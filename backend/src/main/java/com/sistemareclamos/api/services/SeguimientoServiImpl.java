package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.SeguimientoRequestDTO;
import com.sistemareclamos.api.dtos.SeguimientoResponseDTO;
import com.sistemareclamos.api.entities.Reclamo;
import com.sistemareclamos.api.entities.Seguimiento;
import com.sistemareclamos.api.entities.Usuario;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import com.sistemareclamos.api.repositories.SeguimientoRepository;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeguimientoServiImpl implements SeguimientoService {
    private final SeguimientoRepository repository;
    private final ReclamoRepository reclamoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<SeguimientoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public SeguimientoResponseDTO obtenerPorId(Long id) {
        return repository.findById(id).map(this::toResponse).orElse(null);
    }

    @Override
    @Transactional
    public SeguimientoResponseDTO crear(SeguimientoRequestDTO dto) {
        Reclamo reclamo = reclamoRepository.findById(dto.idReclamo()).orElse(null);
        Usuario usuario = usuarioRepository.findById(dto.idUsuario()).orElse(null);
        if (reclamo == null || usuario == null) {
            return null;
        }

        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setReclamo(reclamo);
        seguimiento.setUsuario(usuario);
        seguimiento.setFechaHora(LocalDateTime.now());
        seguimiento.setEstado(dto.estado());
        seguimiento.setObservacion(dto.observacion());
        seguimiento.setRespuesta(dto.respuesta());
        seguimiento.setAccionRealizada(dto.accionRealizada());

        // Mantener sincronizado el estado del reclamo
        reclamo.setEstado(dto.estado());
        reclamoRepository.save(reclamo);

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
