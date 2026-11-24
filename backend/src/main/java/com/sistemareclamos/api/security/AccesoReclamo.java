package com.sistemareclamos.api.security;

import com.sistemareclamos.api.entities.Reclamo;
import com.sistemareclamos.api.entities.Usuario;
import com.sistemareclamos.api.exception.AccesoDenegadoException;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccesoReclamo {

    private static final String ROLE_ADMIN = "ROLE_ADMINISTRADOR";

    private final UsuarioRepository usuarioRepository;

    public boolean esAdmin(UsuarioPrincipal p) {
        return p.getAuthorities().stream().anyMatch(a -> ROLE_ADMIN.equals(a.getAuthority()));
    }

    /** Llamar dentro de una transacción (área y asignado son lazy). */
    public void verificarLectura(Reclamo r, UsuarioPrincipal p) {
        if (esAdmin(p)) return;
        if (!mismaArea(r, p)) {
            throw new AccesoDenegadoException("No tienes acceso a este reclamo");
        }
    }

    public void verificarGestion(Reclamo r, UsuarioPrincipal p) {
        if (esAdmin(p)) return;
        if (!mismaArea(r, p)) {
            throw new AccesoDenegadoException("No tienes acceso a este reclamo");
        }
        Usuario asignado = r.getUsuarioAsignado();
        if (asignado != null && !asignado.getIdUsuario().equals(p.getIdUsuario())) {
            throw new AccesoDenegadoException("El reclamo está asignado a otro funcionario");
        }
    }

    private boolean mismaArea(Reclamo r, UsuarioPrincipal p) {
        if (r.getArea() == null) return false;
        Usuario u = usuarioRepository.findById(p.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return u.getArea().getIdArea().equals(r.getArea().getIdArea());
    }
}