package com.sistemareclamos.api.security;

import com.sistemareclamos.api.entities.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class UsuarioPrincipal implements UserDetails {

    private final Long idUsuario;
    private final String nombreUsuario;
    private final String contrasena;
    private final boolean activo;
    private final boolean noBloqueado;
    private final String rol;
    private final List<GrantedAuthority> authorities;

    private UsuarioPrincipal(Usuario u) {
        this.idUsuario = u.getIdUsuario();
        this.nombreUsuario = u.getNombreUsuario();
        this.contrasena = u.getContrasena();
        this.activo = Boolean.TRUE.equals(u.getEstado());
        this.noBloqueado = u.getBloqueadoHasta() == null
                || u.getBloqueadoHasta().isBefore(LocalDateTime.now());
        this.rol = u.getRol().getNombre();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + normalizar(this.rol)));
    }

    /** Debe llamarse dentro de una transacción: el rol es lazy. */
    public static UsuarioPrincipal desde(Usuario u) {
        return new UsuarioPrincipal(u);
    }

    private static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
    }

    public Long getIdUsuario() { return idUsuario; }
    public String getRol() { return rol; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return contrasena; }
    @Override public String getUsername() { return nombreUsuario; }
    @Override public boolean isEnabled() { return activo; }
    @Override public boolean isAccountNonLocked() { return noBloqueado; }
}