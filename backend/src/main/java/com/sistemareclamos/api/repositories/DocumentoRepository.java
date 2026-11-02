package com.sistemareclamos.api.repositories;

import com.sistemareclamos.api.entities.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByReclamoIdReclamo(Long idReclamo);
}
