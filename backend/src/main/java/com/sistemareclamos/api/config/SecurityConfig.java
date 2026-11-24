package com.sistemareclamos.api.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = "ADMINISTRADOR";

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) ->
                                escribirError(res, HttpServletResponse.SC_UNAUTHORIZED, "No autenticado o sesión expirada"))
                        .accessDeniedHandler((req, res, ex) ->
                                escribirError(res, HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para esta acción")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Públicas
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reclamos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reclamos/consulta").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/documentos/evidencia").permitAll()

                        // Solo administrador
                        .requestMatchers("/api/usuarios/**", "/api/roles/**", "/api/ciudadanos/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/reclamos").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/reclamos/*/derivar").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/seguimientos").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/notificaciones").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/documentos").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/categorias").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/areas").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/areas/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/areas/**").hasRole(ADMIN)
                        .requestMatchers("/api/panel/**").hasRole(ADMIN)
                        .requestMatchers("/api/reportes/**").hasRole(ADMIN)

                        // El resto: autenticado, y cada servicio valida el permiso por caso
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:4200"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }

    private void escribirError(HttpServletResponse res, int status, String mensaje) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"fecha\":\"" + LocalDateTime.now() + "\",\"status\":" + status
                + ",\"mensaje\":\"" + mensaje + "\"}");
    }
}