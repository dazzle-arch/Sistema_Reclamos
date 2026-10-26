package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.CategoriaRequestDTO;
import com.sistemareclamos.api.dtos.CategoriaResponseDTO;
import com.sistemareclamos.api.entities.Categoria;
import com.sistemareclamos.api.repositories.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriaServiImpl implements CategoriaService {

    private final CategoriaRepository repository;

    @Override
    public List<CategoriaResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public CategoriaResponseDTO obtenerPorId(Long id) {
        return repository.findById(id).map(this::toResponse).orElse(null);
    }

    @Override
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        if (repository.existsByNombre(dto.nombre())) {
            return null; // nombre repetido
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.nombre());
        categoria.setDescripcion(dto.descripcion());

        return toResponse(repository.save(categoria));
    }

    @Override
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        Optional<Categoria> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return null; // no existe
        }
        Categoria c = opt.get();
        c.setNombre(dto.nombre());
        c.setDescripcion(dto.descripcion());

        return toResponse(repository.save(c));
    }

    @Override
    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private CategoriaResponseDTO toResponse(Categoria c) {
        return new CategoriaResponseDTO(
                c.getIdCategoria(), c.getNombre(), c.getDescripcion());
    }
}
