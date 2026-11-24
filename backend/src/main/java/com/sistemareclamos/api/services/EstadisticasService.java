package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.ResumenPanelDTO;
import com.sistemareclamos.api.dtos.ResumenPanelDTO.Conteo;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import com.sistemareclamos.api.repositories.SeguimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstadisticasService {

    private static final String[] MESES =
            {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

    private final ReclamoRepository reclamoRepository;
    private final SeguimientoRepository seguimientoRepository;

    public ResumenPanelDTO resumen() {
        LocalDateTime ahora = LocalDateTime.now();

        // Por estado (siempre los cuatro, aunque haya 0)
        Map<String, Long> porEstado = new LinkedHashMap<>();
        for (String e : List.of("REGISTRADO", "EN_PROCESO", "RESUELTO", "CERRADO")) porEstado.put(e, 0L);
        long total = 0;
        for (Object[] f : reclamoRepository.contarPorEstado()) {
            long n = ((Number) f[1]).longValue();
            porEstado.merge((String) f[0], n, Long::sum);
            total += n;
        }

        // Por categoría
        List<Conteo> porCategoria = reclamoRepository.contarPorCategoria().stream()
                .map(f -> new Conteo((String) f[0], ((Number) f[1]).longValue()))
                .toList();

        // Por mes: últimos 6 meses, incluido el actual
        YearMonth actual = YearMonth.from(ahora);
        Map<YearMonth, Long> meses = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) meses.put(actual.minusMonths(i), 0L);
        LocalDateTime desdeMes = actual.minusMonths(5).atDay(1).atStartOfDay();
        for (LocalDateTime f : reclamoRepository.fechasDesde(desdeMes)) {
            meses.computeIfPresent(YearMonth.from(f), (k, v) -> v + 1);
        }
        List<Conteo> porMes = meses.entrySet().stream()
                .map(e -> new Conteo(MESES[e.getKey().getMonthValue() - 1] + " "
                        + String.valueOf(e.getKey().getYear()).substring(2), e.getValue()))
                .toList();

        // Plazos de los casos abiertos
        long vencidos = reclamoRepository.abiertosConPlazo().stream()
                .filter(f -> ((LocalDateTime) f[0]).plusDays(((Number) f[1]).longValue()).isBefore(ahora))
                .count();

        // Cumplimiento de los casos cerrados
        long aTiempo = 0;
        long tarde = 0;
        for (Object[] f : seguimientoRepository.cerradosConPlazo()) {
            LocalDateTime limite = ((LocalDateTime) f[0]).plusDays(((Number) f[1]).longValue());
            if (((LocalDateTime) f[2]).isAfter(limite)) tarde++; else aTiempo++;
        }

        // Resumen de los últimos 7 días
        LocalDateTime desde = ahora.minusDays(7);
        ResumenPanelDTO.Semana semana = new ResumenPanelDTO.Semana(
                reclamoRepository.countByFechaRegistroGreaterThanEqual(desde),
                seguimientoRepository.contarReclamosConEstadoDesde("EN_PROCESO", desde),
                seguimientoRepository.contarReclamosConEstadoDesde("RESUELTO", desde),
                seguimientoRepository.contarReclamosConEstadoDesde("CERRADO", desde),
                vencidos
        );

        return new ResumenPanelDTO(
                total,
                porEstado,
                porCategoria,
                porMes,
                semana,
                new ResumenPanelDTO.Plazos(aTiempo, tarde, vencidos),
                reclamoRepository.countByUsuarioAsignadoIsNullAndEstadoNot("CERRADO"),
                reclamoRepository.countByPrioridadAndEstadoNotIn("MUY ALTA", List.of("RESUELTO", "CERRADO"))
        );
    }
}