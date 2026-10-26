package com.sistemareclamos.api.repositories;
import com.sistemareclamos.api.entities.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Long> {
    // Para "consultar-estado": buscar por DNI del ciudadano
    List<Reclamo> findByCiudadanoDni(String dni);

    // Para la bandeja del funcionario: filtrar por estado
    List<Reclamo> findByEstado(String estado);

    // Si quieres buscar por código de trámite + DNI juntos (consultar-estado)
    Optional<Reclamo> findByIdReclamoAndCiudadanoDni(Long idReclamo, String dni);
}
