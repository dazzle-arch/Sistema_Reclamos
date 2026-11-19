package com.sistemareclamos.api.config;

import com.sistemareclamos.api.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Misma clave secreta que usaste en JwtService
    private static final String SECRET_KEY = "Q2xhdmVTZWNyZXRhTXV5U2VndXJhUGFyYVNpc3RlbWFSZWNsYW1vc011bmljaXBhbDIwMjY=";
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String nombreUsuario;

        // 1. Si no hay token o no empieza con "Bearer ", ignorar y seguir (Spring Security lo bloqueará después si la ruta lo exige)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (quitando "Bearer ")
        jwt = authHeader.substring(7);

        try {
            // 3. Extraer los datos del token usando la clave secreta
            Claims claims = Jwts.parser()
                    .verifyWith(obtenerFirma())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            nombreUsuario = claims.getSubject();
            Long idRol = claims.get("rol", Long.class);

            // 4. Si el usuario existe en el token y no está autenticado en este hilo
            if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Mapeamos el idRol a un formato que Spring Security entienda (ej: ROLE_ADMIN)
                String rolNombre = (idRol == 1) ? "ROLE_ADMIN" : "ROLE_FUNCIONARIO";

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        nombreUsuario,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(rolNombre))
                );

                // Registramos al usuario como "Autenticado"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Si el token expiró o es inválido, simplemente no lo autenticamos
            System.out.println("Error validando token: " + e.getMessage());
        }

        // 5. Continuar con la petición
        filterChain.doFilter(request, response);
    }

    private SecretKey obtenerFirma() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}