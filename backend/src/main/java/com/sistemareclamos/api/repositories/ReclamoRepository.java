package com.sistemareclamos.api.repositories;
import com.sistemareclamos.api.entities.Reclamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
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

    List<Reclamo> findByUsuarioAsignadoIdUsuario(Long idUsuario);

    List<Reclamo> findByAreaIdArea(Long idArea);

    @Query("select r.estado, count(r) from Reclamo r group by r.estado")
    List<Object[]> contarPorEstado();

    @Query("select c.nombre, count(r) from Reclamo r join r.categoria c " +
            "group by c.nombre order by count(r) desc")
    List<Object[]> contarPorCategoria();

    @Query("select r.fechaRegistro from Reclamo r where r.fechaRegistro >= :desde")
    List<LocalDateTime> fechasDesde(@org.springframework.data.repository.query.Param("desde") LocalDateTime desde);

    long countByFechaRegistroGreaterThanEqual(LocalDateTime desde);

    long countByUsuarioAsignadoIsNullAndEstadoNot(String estado);

    long countByPrioridadAndEstadoNotIn(String prioridad, Collection<String> estados);

    /** Casos abiertos con su plazo en días: [fechaRegistro, plazoDias]. */
    @Query("select r.fechaRegistro, coalesce(c.plazoDias, 15) from Reclamo r join r.categoria c " +
            "where r.estado in ('REGISTRADO', 'EN_PROCESO')")
    List<Object[]> abiertosConPlazo();

    /** Una fila por reclamo: [estado, idArea, nombreArea, categoría, plazoDias, funcionario, fechaRegistro]. */
    @Query("select r.estado, a.idArea, a.nombre, c.nombre, coalesce(c.plazoDias, 15), u.nombres, r.fechaRegistro " +
            "from Reclamo r join r.categoria c left join r.area a left join r.usuarioAsignado u " +
            "where r.fechaRegistro >= :desde and r.fechaRegistro < :hasta")
    List<Object[]> filasReporte(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
