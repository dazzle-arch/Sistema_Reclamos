package com.sistemareclamos.api.repositories;

import com.sistemareclamos.api.entities.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioDestinoIdUsuarioOrderByFechaEnvioDesc(Long idUsuario);

    List<Notificacion> findByReclamoIdReclamoOrderByFechaEnvioDesc(Long idReclamo);

    @Query("select count(n) from Notificacion n " +
            "where n.usuarioDestino.idUsuario = :idUsuario and (n.leida = false or n.leida is null)")
    long contarNoLeidas(@Param("idUsuario") Long idUsuario);

    @Modifying
    @Query("update Notificacion n set n.leida = true " +
            "where n.usuarioDestino.idUsuario = :idUsuario and (n.leida = false or n.leida is null)")
    int marcarTodasLeidas(@Param("idUsuario") Long idUsuario);
}
