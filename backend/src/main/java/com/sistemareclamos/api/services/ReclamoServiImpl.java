package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.ReclamoRequestDTO;
import com.sistemareclamos.api.dtos.ReclamoResponseDTO;
import com.sistemareclamos.api.entities.Categoria;
import com.sistemareclamos.api.entities.Ciudadano;
import com.sistemareclamos.api.entities.Reclamo;
import com.sistemareclamos.api.repositories.CategoriaRepository;
import com.sistemareclamos.api.repositories.CiudadanoRepository;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
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
        return repository.findById(id).map(this::toResponse).orElse(null);
    }

    @Override
    @Transactional
    public ReclamoResponseDTO crear(ReclamoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.tipoReclamo()).orElse(null);
        if (categoria == null) {
            return null; // categoría no encontrada
        }

        Ciudadano ciudadano = ciudadanoRepository.findByDni(dto.dni()).orElse(null);
        if (ciudadano == null) {
            ciudadano = new Ciudadano();
            ciudadano.setDni(dto.dni());
            ciudadano.setNombres(dto.nombres());
            ciudadano.setApellidos(dto.apellidos());
            ciudadano.setCorreo(dto.correo());
            ciudadano.setTelefono(dto.telefono());
            ciudadano.setSexo(dto.sexo());
            ciudadano = ciudadanoRepository.save(ciudadano);
        }

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
    public ReclamoResponseDTO actualizarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado.toUpperCase())) {
            return null; // estado no permitido
        }
        Reclamo reclamo = repository.findById(id).orElse(null);
        if (reclamo == null) {
            return null; // no existe
        }
        reclamo.setEstado(nuevoEstado.toUpperCase());
        return toResponse(repository.save(reclamo));
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
