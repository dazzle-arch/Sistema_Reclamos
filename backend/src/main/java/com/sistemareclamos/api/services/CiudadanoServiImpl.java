package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.CiudadanoRequestDTO;
import com.sistemareclamos.api.entities.Ciudadano;
import com.sistemareclamos.api.dtos.CiudadanoResponseDTO;
import com.sistemareclamos.api.repositories.CiudadanoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor

public class CiudadanoServiImpl implements CiudadanoService {

    private final CiudadanoRepository repository;

    @Override
    public List<CiudadanoResponseDTO> listar() {
        // Recupera todas las entidades de la base de datos y las transforma en una lista de DTOs
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public CiudadanoResponseDTO obtenerPorId(Long id) {
        // Busca por ID, lo transforma a DTO si existe, o devuelve null si no lo encuentra
        return repository.findById(id).map(this::toResponse).orElse(null);
    }

    @Override
    public CiudadanoResponseDTO obtenerPorDni(String dni) {
        // Busca por DNI, lo transforma a DTO si existe, o devuelve null si no lo encuentra
        return repository.findByDni(dni).map(this::toResponse).orElse(null);
    }

    @Override
    public CiudadanoResponseDTO crear(CiudadanoRequestDTO dto) {
        // Verifica si ya existe un ciudadano con el mismo DNI; si existe, interrumpe el flujo
        if (repository.existsByDni(dto.dni())) {
            return null;
        }
        Ciudadano ciudadano = new Ciudadano();
        ciudadano.setDni(dto.dni());
        ciudadano.setNombres(dto.nombres());
        ciudadano.setApellidos(dto.apellidos());
        ciudadano.setCorreo(dto.correo());
        ciudadano.setTelefono(dto.telefono());
        ciudadano.setSexo(dto.sexo());
        // Guarda la entidad en la base de datos y la convierte a DTO para retornarla
        return toResponse(repository.save(ciudadano));
    }

    @Override
    public CiudadanoResponseDTO actualizar(Long id, CiudadanoRequestDTO dto) {
        // Busca si el ciudadano a actualizar existe en la base de datos
        Optional<Ciudadano> opt = repository.findById(id);
        if (opt.isEmpty()){
            return null;  // no existe
        }
        // Verifica si el DNI ya está siendo utilizado por otro ciudadano distinto
        if(repository.existsByDniAndIdCiudadanoNot(dto.dni(), id)){
            return null; // Retorna null si el DNI pertenece a otro registro
        }
        Ciudadano c = opt.get();
        c.setDni(dto.dni());
        c.setNombres(dto.nombres());
        c.setApellidos(dto.apellidos());
        c.setCorreo(dto.correo());
        c.setTelefono(dto.telefono());
        c.setSexo(dto.sexo());
        // Guarda los cambios en la base de datos y retorna el DTO actualizado
        return toResponse(repository.save(c));
    }

    @Override
    public boolean eliminar(Long id) {
        if (!repository.existsById(id)){
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private CiudadanoResponseDTO toResponse(Ciudadano c){
        return new CiudadanoResponseDTO(
                c.getIdCiudadano(), c.getDni(), c.getNombres(),
                c.getApellidos(), c.getCorreo(), c.getTelefono(), c.getSexo());

    }
}
