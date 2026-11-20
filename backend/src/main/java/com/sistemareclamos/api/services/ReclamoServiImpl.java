package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.AtencionRequestDTO;
import com.sistemareclamos.api.dtos.DerivarRequestDTO;
import com.sistemareclamos.api.dtos.ReclamoRequestDTO;
import com.sistemareclamos.api.dtos.ReclamoResponseDTO;
import com.sistemareclamos.api.dtos.SeguimientoResponseDTO;
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

        Area areaInicial = areaRepository.findById(1L)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Área inicial (Atención al Ciudadano) no encontrada"));
    
        Reclamo reclamo = new Reclamo();
        reclamo.setCiudadano(ciudadano);
        reclamo.setCategoria(categoria);
        reclamo.setAsunto(dto.asunto());
        reclamo.setArea(areaInicial);
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
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + dto.idUsuarioAsignado()));
        Usuario quienDeriva = usuarioRepository.findById(dto.idUsuarioDeriva())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + dto.idUsuarioDeriva()));

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

    @Override
    @Transactional
    public ReclamoResponseDTO registrarAtencion(Long idReclamo, AtencionRequestDTO dto,
            String nombreUsuarioFuncionario) {
        
        // 1. Buscar el reclamo y el funcionario
        Reclamo reclamo = repository.findById(idReclamo)
                .orElseThrow(() -> new RuntimeException("Reclamo no encontrado"));

        Usuario funcionario = usuarioRepository.findByNombreUsuario(nombreUsuarioFuncionario)
                .orElseThrow(() -> new RuntimeException("Funcionario no encontrado"));
        if ("CERRADO".equalsIgnoreCase(reclamo.getEstado())) {
            throw new IllegalStateException("No se pueden registrar atenciones en un reclamo cerrado.");
        }
        // 2. Crear el registro de trazabilidad usando Seguimiento
        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setAccionRealizada(dto.accionRealizada());
        seguimiento.setObservacion(dto.observacion());
        seguimiento.setRespuesta(dto.respuestaCiudadano());

        // Seguimiento requiere que le pasemos el estado y la fecha obligatoriamente
        String estadoFinal = (dto.nuevoEstado() != null && !dto.nuevoEstado().isBlank())
                ? dto.nuevoEstado().toUpperCase()
                : reclamo.getEstado();

        seguimiento.setEstado(estadoFinal);
        seguimiento.setFechaHora(LocalDateTime.now());
        seguimiento.setReclamo(reclamo);
        seguimiento.setUsuario(funcionario);

        // Guardamos en la base de datos real
        seguimientoRepository.save(seguimiento);

        // 3. Actualizar el estado del Reclamo principal
        reclamo.setEstado(estadoFinal);
        if (dto.nuevaPrioridad() != null && !dto.nuevaPrioridad().isBlank()) {
            reclamo.setPrioridad(dto.nuevaPrioridad().toUpperCase());
        }

        // 4. (Opcional) Si se deriva a otra área
        if (dto.idAreaDerivada() != null) {
            Area area = areaRepository.findById(dto.idAreaDerivada())
                    .orElseThrow(() -> new RuntimeException("Área no encontrada"));
            reclamo.setArea(area);
            reclamo.setUsuarioAsignado(null);
        }

        Reclamo reclamoActualizado = repository.save(reclamo);
        return toResponse(reclamoActualizado);
    }

    @Override
    public List<ReclamoResponseDTO> listarMisCasos(String nombreUsuario) {
        // 1. Buscamos al usuario real en la base de datos usando el nombre que venía en
        // el token
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + nombreUsuario));

        // 2. Buscamos sus reclamos usando su ID real y el area correspondiente
        if (usuario.getArea() == null) {
            return List.of();
        }

        return repository.findByAreaIdArea(usuario.getArea().getIdArea())
                .stream()
                .map(this::toResponse)
                .toList();
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
        // 1. Buscamos el historial de este reclamo
        List<Seguimiento> seguimientos = seguimientoRepository.findByReclamoIdReclamo(r.getIdReclamo());
        
        // 2. Convertimos el historial a DTOs
        List<SeguimientoResponseDTO> historialDTO = seguimientos.stream()
            .map(s -> new SeguimientoResponseDTO(
                s.getIdSeguimiento(),
                s.getFechaHora(),
                s.getEstado(),
                s.getObservacion(),
                s.getRespuesta(),
                s.getAccionRealizada(),
                s.getReclamo().getIdReclamo(),
                s.getUsuario().getIdUsuario(),
                s.getUsuario().getNombres()
            )).toList();

        // 3. Devolvemos el Reclamo con su historial adjunto
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
                r.getFechaRegistro(),
                historialDTO // Pasamos la lista aquí
        );
    }
}
