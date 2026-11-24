package com.sistemareclamos.api.services;

import com.sistemareclamos.api.entities.Usuario;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.expiration-minutes}") long minutos) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
        this.expiracionMs = minutos * 60_000;
    }

    /** Debe llamarse dentro de una transacción: rol y área son lazy. */
    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getNombreUsuario())
                .claim("idUsuario", usuario.getIdUsuario())
                .claim("rol", usuario.getRol().getIdRol())
                .claim("nombreRol", usuario.getRol().getNombre())
                .claim("nombres", usuario.getNombres())
                .claim("idArea", usuario.getArea() != null ? usuario.getArea().getIdArea() : null)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    /** Devuelve el nombre de usuario, o null si el token es inválido o expiró. */
    public String extraerUsername(String token) {
        try {
            return Jwts.parser().verifyWith(clave).build()
                    .parseSignedClaims(token).getPayload().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}