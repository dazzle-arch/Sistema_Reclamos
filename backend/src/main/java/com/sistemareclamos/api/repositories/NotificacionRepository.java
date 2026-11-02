package com.sistemareclamos.api.repositories;

import com.sistemareclamos.api.entities.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioDestinoIdUsuarioOrderByFechaEnvioDesc(Long idUsuario);

    List<Notificacion> findByReclamoIdReclamoOrderByFechaEnvioDesc(Long idReclamo);
}
