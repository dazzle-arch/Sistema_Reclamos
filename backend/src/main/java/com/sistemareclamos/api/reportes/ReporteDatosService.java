package com.sistemareclamos.api.reportes;

import com.sistemareclamos.api.reportes.DatosReporte.Col;
import com.sistemareclamos.api.reportes.DatosReporte.Tipo;
import com.sistemareclamos.api.repositories.AreaRepository;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteDatosService {

    public static final List<String> ESTADOS = List.of("REGISTRADO", "EN_PROCESO", "RESUELTO", "CERRADO");
    private static final List<String> ABIERTOS = List.of("REGISTRADO", "EN_PROCESO");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ReclamoRepository reclamoRepository;
    private final AreaRepository areaRepository;

    // ---------- 1. Reclamos por estado ----------
    public DatosReporte porEstado(LocalDate desde, LocalDate hasta) {
        List<Object[]> filas = filas(desde, hasta);
        Map<String, Long> cont = new LinkedHashMap<>();
        ESTADOS.forEach(e -> cont.put(e, 0L));
        for (Object[] f : filas) cont.merge((String) f[0], 1L, Long::sum);
        long total = filas.size();

        List<Object[]> out = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        List<Double> valores = new ArrayList<>();
        cont.forEach((e, n) -> {
            out.add(new Object[]{nombreEstado(e), n, pct(n, total)});
            etiquetas.add(nombreEstado(e));
            valores.add((double) n);
        });

        return new DatosReporte("estado", "Reclamos por estado", periodo(desde, hasta),
                List.of(new Col("Estado", 295, Tipo.TEXTO),
                        new Col("Cantidad", 110, Tipo.ENTERO),
                        new Col("% del total", 110, Tipo.PORCENTAJE)),
                out, "Total de reclamos", total, etiquetas, valores, "Reclamos por estado");
    }

    // ---------- 2. Reclamos por área ----------
    public DatosReporte porArea(LocalDate desde, LocalDate hasta, String estado) {
        List<Object[]> filas = filas(desde, hasta).stream()
                .filter(f -> estado == null || estado.equals(f[0])).toList();

        Map<String, long[]> grupos = new HashMap<>();   // [reg, proc, res, cer, total]
        for (Object[] f : filas) {
            String area = f[2] == null ? "Sin área" : (String) f[2];
            long[] c = grupos.computeIfAbsent(area, k -> new long[5]);
            int i = ESTADOS.indexOf((String) f[0]);
            if (i >= 0) c[i]++;
            c[4]++;
        }

        List<Object[]> out = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        List<Double> valores = new ArrayList<>();
        grupos.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[4], a.getValue()[4]))
                .forEach(e -> {
                    long[] c = e.getValue();
                    out.add(new Object[]{e.getKey(), c[0], c[1], c[2], c[3], c[4]});
                    etiquetas.add(e.getKey());
                    valores.add((double) c[4]);
                });

        String filtros = periodo(desde, hasta) + (estado == null ? "" : "  ·  Estado: " + nombreEstado(estado));
        return new DatosReporte("area", "Reclamos por área", filtros,
                List.of(new Col("Área", 135, Tipo.TEXTO),
                        new Col("Registrado", 75, Tipo.ENTERO),
                        new Col("En proceso", 75, Tipo.ENTERO),
                        new Col("Resuelto", 75, Tipo.ENTERO),
                        new Col("Cerrado", 75, Tipo.ENTERO),
                        new Col("Total", 80, Tipo.ENTERO)),
                out, "Total de reclamos", filas.size(), etiquetas, valores, "Reclamos por área");
    }

    // ---------- 3. Reclamos por categoría ----------
    public DatosReporte porCategoria(LocalDate desde, LocalDate hasta) {
        List<Object[]> filas = filas(desde, hasta);
        LocalDateTime ahora = LocalDateTime.now();
        long total = filas.size();

        Map<String, long[]> grupos = new HashMap<>();   // [plazo, total, vencidos]
        for (Object[] f : filas) {
            long plazo = ((Number) f[4]).longValue();
            long[] c = grupos.computeIfAbsent((String) f[3], k -> new long[]{plazo, 0, 0});
            c[1]++;
            boolean abierto = ABIERTOS.contains((String) f[0]);
            if (abierto && ((LocalDateTime) f[6]).plusDays(plazo).isBefore(ahora)) c[2]++;
        }

        List<Object[]> out = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        List<Double> valores = new ArrayList<>();
        grupos.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[1], a.getValue()[1]))
                .forEach(e -> {
                    long[] c = e.getValue();
                    out.add(new Object[]{e.getKey(), c[0], c[1], c[2], pct(c[1], total)});
                    etiquetas.add(e.getKey());
                    valores.add((double) c[1]);
                });

        return new DatosReporte("categoria", "Reclamos por categoría", periodo(desde, hasta),
                List.of(new Col("Categoría", 215, Tipo.TEXTO),
                        new Col("Plazo (días)", 75, Tipo.ENTERO),
                        new Col("Total", 75, Tipo.ENTERO),
                        new Col("Vencidos", 75, Tipo.ENTERO),
                        new Col("% del total", 75, Tipo.PORCENTAJE)),
                out, "Total de reclamos", total, etiquetas, valores, "Reclamos por categoría");
    }

    // ---------- 4. Carga por funcionario ----------
    public DatosReporte porFuncionario(LocalDate desde, LocalDate hasta, Long idArea) {
        List<Object[]> filas = filas(desde, hasta).stream()
                .filter(f -> idArea == null || idArea.equals(f[1])).toList();

        Map<String, long[]> grupos = new HashMap<>();   // [abiertos, resueltos, cerrados, total]
        for (Object[] f : filas) {
            String nombre = f[5] == null ? "Sin asignar" : (String) f[5];
            long[] c = grupos.computeIfAbsent(nombre, k -> new long[4]);
            String estado = (String) f[0];
            if (ABIERTOS.contains(estado)) c[0]++;
            else if ("RESUELTO".equals(estado)) c[1]++;
            else if ("CERRADO".equals(estado)) c[2]++;
            c[3]++;
        }

        List<Object[]> out = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        List<Double> valores = new ArrayList<>();
        grupos.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[3], a.getValue()[3]))
                .forEach(e -> {
                    long[] c = e.getValue();
                    out.add(new Object[]{e.getKey(), c[0], c[1], c[2], c[3]});
                    etiquetas.add(e.getKey());
                    valores.add((double) c[3]);
                });

        String filtros = periodo(desde, hasta);
        if (idArea != null) {
            filtros += "  ·  Área: " + areaRepository.findById(idArea).map(a -> a.getNombre()).orElse("—");
        }
        return new DatosReporte("funcionario", "Carga por funcionario", filtros,
                List.of(new Col("Funcionario", 215, Tipo.TEXTO),
                        new Col("Abiertos", 75, Tipo.ENTERO),
                        new Col("Resueltos", 75, Tipo.ENTERO),
                        new Col("Cerrados", 75, Tipo.ENTERO),
                        new Col("Total", 75, Tipo.ENTERO)),
                out, "Total de reclamos", filas.size(), etiquetas, valores, "Reclamos por funcionario");
    }

    // ---------- utilidades ----------
    private List<Object[]> filas(LocalDate desde, LocalDate hasta) {
        return reclamoRepository.filasReporte(desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay());
    }

    private String periodo(LocalDate desde, LocalDate hasta) {
        return "Período: " + desde.format(FECHA) + " al " + hasta.format(FECHA);
    }

    private double pct(long parte, long total) {
        return total == 0 ? 0.0 : Math.round(parte * 1000.0 / total) / 10.0;
    }

    public static String nombreEstado(String e) {
        return switch (e) {
            case "REGISTRADO" -> "Registrado";
            case "EN_PROCESO" -> "En proceso";
            case "RESUELTO" -> "Resuelto";
            case "CERRADO" -> "Cerrado";
            default -> e;
        };
    }
}