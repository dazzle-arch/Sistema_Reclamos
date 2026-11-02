package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.CiudadanoRequestDTO;
import com.sistemareclamos.api.entities.Ciudadano;
import com.sistemareclamos.api.dtos.CiudadanoResponseDTO;
import com.sistemareclamos.api.exception.ConflictoException;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.repositories.CiudadanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CiudadanoServiImpl implements CiudadanoService {

    private final CiudadanoRepository repository;

    @Override
    public List<CiudadanoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public CiudadanoResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public CiudadanoResponseDTO obtenerPorDni(String dni) {
        return repository.findByDni(dni)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado con DNI: " + dni));
    }

    @Override
    @Transactional
    public CiudadanoResponseDTO crear(CiudadanoRequestDTO dto) {
        if (repository.existsByDni(dto.dni())) {
            throw new ConflictoException("Ya existe un ciudadano con ese DNI");
        }
        Ciudadano ciudadano = new Ciudadano();
        mapearDatos(ciudadano, dto);

        return toResponse(repository.save(ciudadano));
    }

    @Override
    @Transactional
    public CiudadanoResponseDTO actualizar(Long id, CiudadanoRequestDTO dto) {
        Ciudadano ciudadano = buscar(id);
        if (repository.existsByDniAndIdCiudadanoNot(dto.dni(), id)) {
            throw new ConflictoException("Ese DNI pertenece a otro ciudadano");
        }
        mapearDatos(ciudadano, dto);

        return toResponse(repository.save(ciudadano));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Ciudadano no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private Ciudadano buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ciudadano no encontrado con id: " + id));
    }

    private void mapearDatos(Ciudadano ciudadano, CiudadanoRequestDTO dto) {
        ciudadano.setDni(dto.dni());
        ciudadano.setNombres(dto.nombres());
        ciudadano.setApellidos(dto.apellidos());
        ciudadano.setCorreo(dto.correo());
        ciudadano.setTelefono(dto.telefono());
        ciudadano.setSexo(dto.sexo());
    }

    private CiudadanoResponseDTO toResponse(Ciudadano c) {
        return new CiudadanoResponseDTO(
                c.getIdCiudadano(), c.getDni(), c.getNombres(),
                c.getApellidos(), c.getCorreo(), c.getTelefono(), c.getSexo());
    }
}
