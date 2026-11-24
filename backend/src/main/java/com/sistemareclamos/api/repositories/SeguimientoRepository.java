package com.sistemareclamos.api.repositories;

import com.sistemareclamos.api.entities.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {
    List<Seguimiento> findByReclamoIdReclamoOrderByFechaHoraAsc(Long idReclamo);
    
    List<Seguimiento> findByReclamoIdReclamo(Long idReclamo);

    @Query("select count(distinct r.idReclamo) from Seguimiento s join s.reclamo r " +
            "where s.estado = :estado and s.fechaHora >= :desde")
    long contarReclamosConEstadoDesde(@Param("estado") String estado, @Param("desde") LocalDateTime desde);

    /** Casos cerrados: [fechaRegistro, plazoDias, fechaDelCierre]. */
    @Query("select r.fechaRegistro, coalesce(c.plazoDias, 15), min(s.fechaHora) " +
            "from Seguimiento s join s.reclamo r join r.categoria c " +
            "where s.estado = 'CERRADO' and r.estado = 'CERRADO' " +
            "group by r.idReclamo, r.fechaRegistro, c.plazoDias")
    List<Object[]> cerradosConPlazo();
}