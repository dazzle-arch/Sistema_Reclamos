package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.RolRequestDTO;
import com.sistemareclamos.api.dtos.RolResponseDTO;
import com.sistemareclamos.api.entities.Rol;
import com.sistemareclamos.api.repositories.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RolServiImpl implements RolService {
    private final RolRepository repository;

    @Override
    public List<RolResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public RolResponseDTO obtenerPorId(Long id) {
        return repository.findById(id).map(this::toResponse).orElse(null);
    }

    @Override
    public RolResponseDTO crear(RolRequestDTO dto) {
        Rol rol = new Rol();
        rol.setNombre(dto.nombre());
        rol.setDescripcion(dto.descripcion());

        return toResponse(repository.save(rol));
    }

    @Override
    public RolResponseDTO actualizar(Long id, RolRequestDTO dto) {
        return repository.findById(id).map(rol -> {
            rol.setNombre(dto.nombre());
            rol.setDescripcion(dto.descripcion());
            return toResponse(repository.save(rol));
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

    private RolResponseDTO toResponse(Rol r) {
        return new RolResponseDTO(r.getIdRol(), r.getNombre(), r.getDescripcion());
    }
}
