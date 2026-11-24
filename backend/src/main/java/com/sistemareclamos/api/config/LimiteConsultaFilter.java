package com.sistemareclamos.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LimiteConsultaFilter extends OncePerRequestFilter {

    private static final int MAX_POR_VENTANA = 10;
    private static final long VENTANA_MS = 60_000;

    // ip -> [inicio de ventana, cantidad]
    private final Map<String, long[]> contadores = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean consulta = "GET".equals(request.getMethod()) && "/api/reclamos/consulta".equals(uri);
        boolean subida = "POST".equals(request.getMethod()) && "/api/documentos/evidencia".equals(uri);
        return !(consulta || subida);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        long ahora = System.currentTimeMillis();
        String ip = request.getRemoteAddr() + request.getRequestURI();   // contador separado por ruta

        long[] c = contadores.compute(ip, (k, v) -> {
            if (v == null || ahora - v[0] > VENTANA_MS) return new long[]{ahora, 1};
            v[1]++;
            return v;
        });

        if (contadores.size() > 10_000) {
            contadores.entrySet().removeIf(e -> ahora - e.getValue()[0] > VENTANA_MS);
        }

        if (c[1] > MAX_POR_VENTANA) {
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"fecha\":\"" + LocalDateTime.now() + "\",\"status\":429,"
                    + "\"mensaje\":\"Demasiadas consultas. Intenta nuevamente en un minuto\"}");
            return;
        }
        chain.doFilter(request, response);
    }
}