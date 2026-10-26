package com.sistemareclamos.api.repositories;

import com.sistemareclamos.api.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByCorreo(String correo);
    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByCorreoAndIdUsuarioNot(String correo, Long idUsuario);
    boolean existsByNombreUsuarioAndIdUsuarioNot(String nombreUsuario, Long idUsuario);
}
