package com.sistemareclamos.api.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // Clave secreta (En producción debería ir en el application.properties)
    // Es una cadena Base64 de al menos 256 bits
    private static final String SECRET_KEY = "Q2xhdmVTZWNyZXRhTXV5U2VndXJhUGFyYVNpc3RlbWFSZWNsYW1vc011bmljaXBhbDIwMjY=";

    public String generarToken(String nombreUsuario, Long idRol) {
        return Jwts.builder()
                .subject(nombreUsuario)
                .claim("rol", idRol) // Guardamos el rol dentro del token para que Angular lo lea
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expira en 10 horas
                .signWith(obtenerFirma(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey obtenerFirma() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}