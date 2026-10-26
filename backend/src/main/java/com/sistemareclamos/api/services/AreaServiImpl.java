package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.AreaRequestDTO;
import com.sistemareclamos.api.dtos.AreaResponseDTO;
import com.sistemareclamos.api.entities.Area;
import com.sistemareclamos.api.repositories.AreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaServiImpl implements AreaService {

    private final AreaRepository repository;

    @Override
    public List<AreaResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public AreaResponseDTO obtenerPorId(Long id) {
        return repository.findById(id).map(this::toResponse).orElse(null);
    }

    @Override
    public AreaResponseDTO crear(AreaRequestDTO dto) {
        Area area = new Area();
        area.setNombre(dto.nombre());
        area.setDescripcion(dto.descripcion());

        return toResponse(repository.save(area));
    }

    @Override
    public AreaResponseDTO actualizar(Long id, AreaRequestDTO dto) {
        return repository.findById(id).map(area -> {
            area.setNombre(dto.nombre());
            area.setDescripcion(dto.descripcion());
            return toResponse(repository.save(area));
        }).orElse(null);
    }

    @Override
    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private AreaResponseDTO toResponse(Area a) {
        return new AreaResponseDTO(a.getIdArea(), a.getNombre(), a.getDescripcion());
    }
}
