package com.sistemareclamos.api.reportes;

import com.sistemareclamos.api.exception.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private static final long MAX_DIAS = 366;
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter SELLO = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");

    public record ArchivoReporte(byte[] bytes, String nombre, String mime) {}

    private final ReporteDatosService datosService;
    private final PdfReporte pdf;
    private final ExcelReporte excel;

    public ArchivoReporte generar(String tipo, String formato, LocalDate desde, LocalDate hasta,
                                  String estado, Long idArea, String generadoPor) {
        // Fechas: por defecto, los últimos 30 días
        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null ? desde : fin.minusDays(30);
        if (inicio.isAfter(fin)) {
            throw new SolicitudInvalidaException("La fecha inicial no puede ser posterior a la final");
        }
        if (ChronoUnit.DAYS.between(inicio, fin) > MAX_DIAS) {
            throw new SolicitudInvalidaException("El rango máximo es de un año");
        }

        String estadoOk = null;
        if (estado != null && !estado.isBlank()) {
            estadoOk = estado.trim().toUpperCase();
            if (!ReporteDatosService.ESTADOS.contains(estadoOk)) {
                throw new SolicitudInvalidaException("Estado no permitido. Valores válidos: " + ReporteDatosService.ESTADOS);
            }
        }

        // Lista fija: el tipo nunca se usa para construir rutas ni consultas
        DatosReporte datos = switch (tipo) {
            case "estado" -> datosService.porEstado(inicio, fin);
            case "area" -> datosService.porArea(inicio, fin, estadoOk);
            case "categoria" -> datosService.porCategoria(inicio, fin);
            case "funcionario" -> datosService.porFuncionario(inicio, fin, idArea);
            default -> throw new SolicitudInvalidaException("Tipo de reporte no válido");
        };

        String fechaHora = LocalDateTime.now().format(FECHA_HORA);
        String base = "reporte-" + tipo + "-" + LocalDateTime.now().format(SELLO);

        return switch (formato.toLowerCase()) {
            case "pdf" -> new ArchivoReporte(pdf.generar(datos, generadoPor, fechaHora),
                    base + ".pdf", "application/pdf");
            case "xlsx" -> new ArchivoReporte(excel.generar(datos, generadoPor, fechaHora),
                    base + ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default -> throw new SolicitudInvalidaException("Formato no válido. Use pdf o xlsx");
        };
    }
}