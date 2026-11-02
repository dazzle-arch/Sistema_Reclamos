package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.AreaRequestDTO;
import com.sistemareclamos.api.dtos.AreaResponseDTO;
import com.sistemareclamos.api.entities.Area;
import com.sistemareclamos.api.exception.ConflictoException;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.repositories.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AreaServiImpl implements AreaService {

    private final AreaRepository repository;

    @Override
    public List<AreaResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public AreaResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    @Transactional
    public AreaResponseDTO crear(AreaRequestDTO dto) {
        if (repository.existsByNombre(dto.nombre())) {
            throw new ConflictoException(("Ya existe un area con ese nombre"));
        }
        Area area = new Area();
        area.setNombre(dto.nombre());
        area.setDescripcion(dto.descripcion());

        return toResponse(repository.save(area));
    }

    @Override
    @Transactional
    public AreaResponseDTO actualizar(Long id, AreaRequestDTO dto) {
        Area area = buscar(id);
        if (repository.existsByNombreAndIdAreaNot(dto.nombre(), id)){
            throw new ConflictoException("Ya existe un area con ese nombre");
        }
        area.setNombre(dto.nombre());
        area.setDescripcion(dto.descripcion());

        return toResponse(repository.save(area));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Area no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }
    private Area buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Área no encontrada con id: " + id));
    }

    private AreaResponseDTO toResponse(Area a) {
        return new AreaResponseDTO(a.getIdArea(), a.getNombre(), a.getDescripcion());
    }
}
