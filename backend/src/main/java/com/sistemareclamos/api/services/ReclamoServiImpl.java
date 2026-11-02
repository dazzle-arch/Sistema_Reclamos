package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.ReclamoRequestDTO;
import com.sistemareclamos.api.dtos.ReclamoResponseDTO;
import com.sistemareclamos.api.entities.Categoria;
import com.sistemareclamos.api.entities.Ciudadano;
import com.sistemareclamos.api.entities.Reclamo;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.exception.SolicitudInvalidaException;
import com.sistemareclamos.api.repositories.CategoriaRepository;
import com.sistemareclamos.api.repositories.CiudadanoRepository;
import com.sistemareclamos.api.repositories.ReclamoRepository;
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
                r.getAsunto(),
                r.getDescripcion(),
                r.getEstado(),
                r.getPrioridad(),
                r.getFechaRegistro()
        );
    }
}
