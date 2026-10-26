package com.sistemareclamos.api.repositories;

import com.sistemareclamos.api.entities.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    boolean existsByNombre(String nombre);
}
