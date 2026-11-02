package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.RolRequestDTO;
import com.sistemareclamos.api.dtos.RolResponseDTO;
import com.sistemareclamos.api.entities.Rol;
import com.sistemareclamos.api.exception.ConflictoException;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.repositories.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolServiImpl implements RolService {
    private final RolRepository repository;

    @Override
    public List<RolResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public RolResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    @Transactional
    public RolResponseDTO crear(RolRequestDTO dto) {
        if (repository.existsByNombre(dto.nombre())) {
            throw new ConflictoException("Ya existe un rol con ese nombre");
        }
        Rol rol = new Rol();
        rol.setNombre(dto.nombre());
        rol.setDescripcion(dto.descripcion());

        return toResponse(repository.save(rol));
    }

    @Override
    @Transactional
    public RolResponseDTO actualizar(Long id, RolRequestDTO dto) {
        Rol rol = buscar(id);
        if (repository.existsByNombreAndIdRolNot(dto.nombre(), id)) {
            throw new ConflictoException("Ya existe un rol con ese nombre");
        }
        rol.setNombre(dto.nombre());
        rol.setDescripcion(dto.descripcion());

        return toResponse(repository.save(rol));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Rol no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private Rol buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con id: " + id));
    }

    private RolResponseDTO toResponse(Rol r) {
        return new RolResponseDTO(r.getIdRol(), r.getNombre(), r.getDescripcion());
    }
}
