package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.CategoriaRequestDTO;
import com.sistemareclamos.api.dtos.CategoriaResponseDTO;
import com.sistemareclamos.api.entities.Categoria;
import com.sistemareclamos.api.exception.ConflictoException;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.repositories.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaServiImpl implements CategoriaService {

    private final CategoriaRepository repository;

    @Override
    public List<CategoriaResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public CategoriaResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    @Transactional
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        if (repository.existsByNombre(dto.nombre())) {
            throw new ConflictoException("Ya existe una categoría con ese nombre");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.nombre());
        categoria.setDescripcion(dto.descripcion());

        return toResponse(repository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = buscar(id);
        if (repository.existsByNombreAndIdCategoriaNot(dto.nombre(), id)) {
            throw new ConflictoException("Ya existe una categoría con ese nombre");
        }
        categoria.setNombre(dto.nombre());
        categoria.setDescripcion(dto.descripcion());

        return toResponse(repository.save(categoria));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Categoría no encontrada con id: " + id);
        }
        repository.deleteById(id);
    }

    private Categoria buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
    }

    private CategoriaResponseDTO toResponse(Categoria c) {
        return new CategoriaResponseDTO(c.getIdCategoria(), c.getNombre(), c.getDescripcion());
    }
}
