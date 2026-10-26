package com.sistemareclamos.api.repositories;
import com.sistemareclamos.api.entities.Ciudadano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CiudadanoRepository extends JpaRepository<Ciudadano, Long> {

    Optional<Ciudadano> findByDni(String dni);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdCiudadanoNot(String dni, Long idCiudadano);
}
