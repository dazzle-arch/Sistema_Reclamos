package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.DerivarRequestDTO;
import com.sistemareclamos.api.dtos.ReclamoRequestDTO;
import com.sistemareclamos.api.dtos.ReclamoResponseDTO;
import com.sistemareclamos.api.entities.*;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.exception.SolicitudInvalidaException;
import com.sistemareclamos.api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReclamoServiImpl implements ReclamoService {

    private static final List<String> ESTADOS_VALIDOS =
            List.of("REGISTRADO", "EN_PROCESO", "RESUELTO", "CERRADO");

    private final ReclamoRepository repository;
    private final CiudadanoRepository ciudadanoRepository;
    private final CategoriaRepository categoriaRepository;
    private final AreaRepository areaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SeguimientoRepository seguimientoRepository;
    private final NotificacionRepository notificacionRepository;

    @Override
    public List<ReclamoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ReclamoResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    @Transactional
    public ReclamoResponseDTO crear(ReclamoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.tipoReclamo())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría no encontrada con id: " + dto.tipoReclamo()));

        Ciudadano ciudadano = ciudadanoRepository.findByDni(dto.dni())
                .orElseGet(() -> crearCiudadano(dto));

        Reclamo reclamo = new Reclamo();
        reclamo.setCiudadano(ciudadano);
        reclamo.setCategoria(categoria);
        reclamo.setAsunto(dto.asunto());
        reclamo.setDescripcion(dto.descripcion());
        reclamo.setEstado("REGISTRADO");
        reclamo.setPrioridad("MEDIA");
        reclamo.setFechaRegistro(LocalDateTime.now());

        return toResponse(repository.save(reclamo));
    }

    @Override
    @Transactional
    public ReclamoResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado.toUpperCase())) {
            throw new SolicitudInvalidaException("Estado no permitido. Valores válidos: " + ESTADOS_VALIDOS);
        }
        Reclamo reclamo = buscar(id);
        reclamo.setEstado(nuevoEstado.toUpperCase());

        return toResponse(repository.save(reclamo));
    }
    @Override
    public List<ReclamoResponseDTO> listarPorFuncionario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id: " + idUsuario);
        }
        return repository.findByUsuarioAsignadoIdUsuario(idUsuario)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ReclamoResponseDTO derivar(Long id, DerivarRequestDTO dto) {
        Reclamo reclamo = buscar(id);
        if ("CERRADO".equals(reclamo.getEstado())) {
            throw new SolicitudInvalidaException("No se puede derivar un reclamo cerrado");
        }

        Area area = areaRepository.findById(dto.idArea())
                .orElseThrow(() -> new RecursoNoEncontradoException("Área no encontrada con id: " + dto.idArea()));
        Usuario funcionario = usuarioRepository.findById(dto.idUsuarioAsignado())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + dto.idUsuarioAsignado()));
        Usuario quienDeriva = usuarioRepository.findById(dto.idUsuarioDeriva())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + dto.idUsuarioDeriva()));

        if (!Boolean.TRUE.equals(funcionario.getEstado())) {
            throw new SolicitudInvalidaException("El funcionario está inactivo");
        }
        if (!funcionario.getArea().getIdArea().equals(area.getIdArea())) {
            throw new SolicitudInvalidaException("El funcionario no pertenece al área seleccionada");
        }

        reclamo.setArea(area);
        reclamo.setUsuarioAsignado(funcionario);
        reclamo.setEstado("EN_PROCESO");

        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setReclamo(reclamo);
        seguimiento.setUsuario(quienDeriva);
        seguimiento.setFechaHora(LocalDateTime.now());
        seguimiento.setEstado("EN_PROCESO");
        seguimiento.setObservacion(dto.observacion());
        seguimiento.setAccionRealizada("Derivado a " + area.getNombre() + ", asignado a " + funcionario.getNombres());
        seguimientoRepository.save(seguimiento);

        Notificacion notificacion = new Notificacion();
        notificacion.setReclamo(reclamo);
        notificacion.setUsuarioDestino(funcionario);
        notificacion.setAsunto("Nuevo caso asignado");
        notificacion.setMensaje("Se te asignó el reclamo N° " + reclamo.getIdReclamo() + ": " + reclamo.getAsunto());
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacionRepository.save(notificacion);

        return toResponse(reclamo);
    }

    private Reclamo buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reclamo no encontrado con id: " + id));
    }

    private Ciudadano crearCiudadano(ReclamoRequestDTO dto) {
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setDni(dto.dni());
        ciudadano.setNombres(dto.nombres());
        ciudadano.setApellidos(dto.apellidos());
        ciudadano.setCorreo(dto.correo());
        ciudadano.setTelefono(dto.telefono());
        ciudadano.setSexo(dto.sexo());
        return ciudadanoRepository.save(ciudadano);
    }

    private ReclamoResponseDTO toResponse(Reclamo r) {
        return new ReclamoResponseDTO(
                r.getIdReclamo(),
                r.getCiudadano().getIdCiudadano(),
                r.getCiudadano().getNombres() + " " + r.getCiudadano().getApellidos(),
                r.getCiudadano().getDni(),
                r.getCategoria().getIdCategoria(),
                r.getCategoria().getNombre(),
                r.getArea() != null ? r.getArea().getIdArea() : null,
                r.getArea() != null ? r.getArea().getNombre() : null,
                r.getUsuarioAsignado() != null ? r.getUsuarioAsignado().getIdUsuario() : null,
                r.getUsuarioAsignado() != null ? r.getUsuarioAsignado().getNombres() : null,
                r.getAsunto(),
                r.getDescripcion(),
                r.getEstado(),
                r.getPrioridad(),
                r.getFechaRegistro()
        );
    }
}
