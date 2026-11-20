package com.sistemareclamos.api.services;

import com.sistemareclamos.api.entities.Usuario; // Verifica si tu paquete es entities o models
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "Q2xhdmVTZWNyZXRhTXV5U2VndXJhUGFyYVNpc3RlbWFSZWNsYW1vc011bmljaXBhbDIwMjY=";

    public String generarToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getNombreUsuario())
                .claim("idUsuario", usuario.getIdUsuario())
                .claim("rol", usuario.getRol().getIdRol())
                .claim("nombres", usuario.getNombres())
                .claim("idArea", usuario.getArea() != null ? usuario.getArea().getIdArea() : null)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(obtenerFirma(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey obtenerFirma() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}