package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.*;
import com.sistemareclamos.api.entities.*;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.exception.SolicitudInvalidaException;
import com.sistemareclamos.api.repositories.*;
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
public class ReclamoServiImpl implements ReclamoService {

    private static final List<String> ESTADOS_VALIDOS =
            List.of("REGISTRADO", "EN_PROCESO", "RESUELTO", "CERRADO");
    private static final List<String> PRIORIDADES_VALIDAS = List.of("ALTA", "MEDIA", "BAJA");
    private static final String ROL_ADMIN = "Administrador";

    private final ReclamoRepository repository;
    private final CiudadanoRepository ciudadanoRepository;
    private final CategoriaRepository categoriaRepository;
    private final AreaRepository areaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SeguimientoRepository seguimientoRepository;
    private final NotificacionRepository notificacionRepository;
    private final AccesoReclamo acceso;

    @Override
    public List<ReclamoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ReclamoResponseDTO obtenerPorId(Long id, UsuarioPrincipal usuario) {
        Reclamo reclamo = buscar(id);
        acceso.verificarLectura(reclamo, usuario);
        return toResponse(reclamo);
    }

    @Override
    @Transactional
    public ReclamoCreadoDTO crear(ReclamoRequestDTO dto) {
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

        Reclamo guardado = repository.save(reclamo);
        return new ReclamoCreadoDTO(guardado.getIdReclamo(), guardado.getEstado(), guardado.getFechaRegistro());
    }

    @Override
    @Transactional
    public ReclamoResponseDTO actualizarEstado(Long id, String nuevoEstado, UsuarioPrincipal usuario) {
        String estado = validarEstado(nuevoEstado);
        Reclamo reclamo = buscar(id);
        acceso.verificarGestion(reclamo, usuario);
        if ("CERRADO".equals(reclamo.getEstado())) {
            throw new SolicitudInvalidaException("El reclamo ya está cerrado");
        }
        reclamo.setEstado(estado);

        if ("CERRADO".equals(estado)) {
            Usuario quien = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
            notificarAdmins(reclamo, "Caso cerrado",
                    "El reclamo N° " + reclamo.getIdReclamo() + " fue cerrado por " + quien.getNombres(),
                    quien.getIdUsuario());
        }
        return toResponse(repository.save(reclamo));
    }

    @Override
    @Transactional
    public ReclamoResponseDTO derivar(Long id, DerivarRequestDTO dto, UsuarioPrincipal usuario) {
        Reclamo reclamo = buscar(id);
        if ("CERRADO".equals(reclamo.getEstado())) {
            throw new SolicitudInvalidaException("No se puede derivar un reclamo cerrado");
        }

        Area area = areaRepository.findById(dto.idArea())
                .orElseThrow(() -> new RecursoNoEncontradoException("Área no encontrada con id: " + dto.idArea()));
        Usuario funcionario = usuarioRepository.findById(dto.idUsuarioAsignado())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado con id: " + dto.idUsuarioAsignado()));
        // Quien deriva sale del token, no del body
        Usuario quienDeriva = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

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
    public ReclamoResponseDTO registrarAtencion(Long id, AtencionRequestDTO dto, UsuarioPrincipal usuario) {
        Reclamo reclamo = buscar(id);
        acceso.verificarGestion(reclamo, usuario);

        if ("CERRADO".equalsIgnoreCase(reclamo.getEstado())) {
            throw new SolicitudInvalidaException("No se pueden registrar atenciones en un reclamo cerrado");
        }

        Usuario funcionario = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        String estadoFinal = validarEstado(dto.nuevoEstado());

        String prioridad = null;
        if (dto.nuevaPrioridad() != null && !dto.nuevaPrioridad().isBlank()) {
            prioridad = dto.nuevaPrioridad().toUpperCase();
            if (!PRIORIDADES_VALIDAS.contains(prioridad)) {
                throw new SolicitudInvalidaException("Prioridad no permitida. Valores válidos: " + PRIORIDADES_VALIDAS);
            }
        }

        Area areaDestino = null;
        if (dto.idAreaDerivada() != null) {
            if ("CERRADO".equals(estadoFinal)) {
                throw new SolicitudInvalidaException("No se puede derivar un reclamo que se está cerrando");
            }
            areaDestino = areaRepository.findById(dto.idAreaDerivada())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Área no encontrada con id: " + dto.idAreaDerivada()));
        }

        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setReclamo(reclamo);
        seguimiento.setUsuario(funcionario);
        seguimiento.setFechaHora(LocalDateTime.now());
        seguimiento.setEstado(estadoFinal);
        seguimiento.setAccionRealizada(dto.accionRealizada());
        seguimiento.setObservacion(dto.observacion());
        seguimiento.setRespuesta(dto.respuestaCiudadano());
        seguimientoRepository.save(seguimiento);

        reclamo.setEstado(estadoFinal);
        if (prioridad != null) {
            reclamo.setPrioridad(prioridad);
        }

        // Quien toma un caso sin asignar queda como responsable (el admin no)
        if (reclamo.getUsuarioAsignado() == null && !acceso.esAdmin(usuario)) {
            reclamo.setUsuarioAsignado(funcionario);
        }

        // Derivación a otra área: queda en la cola del área, sin asignado
        if (areaDestino != null) {
            reclamo.setArea(areaDestino);
            reclamo.setUsuarioAsignado(null);

            for (Usuario destino : usuarioRepository.findByAreaIdAreaAndEstadoTrue(areaDestino.getIdArea())) {
                if (destino.getIdUsuario().equals(funcionario.getIdUsuario())) continue;
                if (ROL_ADMIN.equals(destino.getRol().getNombre())) continue; // el admin se avisa aparte
                Notificacion n = new Notificacion();
                n.setReclamo(reclamo);
                n.setUsuarioDestino(destino);
                n.setAsunto("Nuevo caso en tu área");
                n.setMensaje("Se derivó a " + areaDestino.getNombre() + " el reclamo N° "
                        + reclamo.getIdReclamo() + ": " + reclamo.getAsunto());
                n.setFechaEnvio(LocalDateTime.now());
                notificacionRepository.save(n);
            }

            notificarAdmins(reclamo, "Caso derivado",
                    funcionario.getNombres() + " derivó el reclamo N° " + reclamo.getIdReclamo()
                            + " a " + areaDestino.getNombre(),
                    funcionario.getIdUsuario());
        }

        if ("CERRADO".equals(estadoFinal)) {
            notificarAdmins(reclamo, "Caso cerrado",
                    "El reclamo N° " + reclamo.getIdReclamo() + " fue cerrado por " + funcionario.getNombres(),
                    funcionario.getIdUsuario());
        }
        return toResponse(repository.save(reclamo));

    }

    @Override
    public List<ReclamoResponseDTO> listarMisCasos(UsuarioPrincipal usuario) {
        Usuario u = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        if (u.getArea() == null) {
            return List.of();
        }
        return repository.findByAreaIdArea(u.getArea().getIdArea())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public ConsultaPublicaDTO consultar(Long codigo, String dni) {
        // Mismo mensaje si falla el código o el DNI: no se revela cuál de los dos existe
        Reclamo r = repository.findByIdReclamoAndCiudadanoDni(codigo, dni.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró un reclamo con esos datos"));

        List<MovimientoPublicoDTO> historial = seguimientoRepository
                .findByReclamoIdReclamoOrderByFechaHoraAsc(r.getIdReclamo())
                .stream()
                .map(s -> new MovimientoPublicoDTO(s.getFechaHora(), s.getEstado(), s.getRespuesta()))
                .toList();

        return new ConsultaPublicaDTO(
                r.getIdReclamo(),
                r.getAsunto(),
                r.getCategoria().getNombre(),
                r.getArea() != null ? r.getArea().getNombre() : null,
                r.getEstado(),
                r.getFechaRegistro(),
                historial
        );
    }

    private String validarEstado(String estado) {
        if (estado == null || !ESTADOS_VALIDOS.contains(estado.toUpperCase())) {
            throw new SolicitudInvalidaException("Estado no permitido. Valores válidos: " + ESTADOS_VALIDOS);
        }
        return estado.toUpperCase();
    }

    private void notificarAdmins(Reclamo reclamo, String asunto, String mensaje, Long excluirId) {
        for (Usuario admin : usuarioRepository.findByRolNombreAndEstadoTrue(ROL_ADMIN)) {
            if (admin.getIdUsuario().equals(excluirId)) continue;
            Notificacion n = new Notificacion();
            n.setReclamo(reclamo);
            n.setUsuarioDestino(admin);
            n.setAsunto(asunto);
            n.setMensaje(mensaje);
            n.setFechaEnvio(LocalDateTime.now());
            notificacionRepository.save(n);
        }
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
        List<SeguimientoResponseDTO> historialDTO = seguimientoRepository
                .findByReclamoIdReclamo(r.getIdReclamo()).stream()
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

        return new ReclamoResponseDTO(
                r.getIdReclamo(),
                r.getCiudadano().getIdCiudadano(),
                r.getCiudadano().getNombres() + " " + r.getCiudadano().getApellidos(),
                r.getCiudadano().getDni(),
                r.getCiudadano().getTelefono(),
                r.getCiudadano().getCorreo(),
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
                historialDTO
        );
    }
}
