package com.sistemareclamos.api.reportes;

import java.util.List;

/** Contenido ya calculado de un reporte. Lo consumen el PDF y el Excel por igual. */
public record DatosReporte(
        String clave,
        String titulo,
        String filtros,
        List<Col> columnas,
        List<Object[]> filas,
        String etiquetaTotal,
        long total,
        List<String> etiquetasGrafico,
        List<Double> valoresGrafico,
        String tituloGrafico
) {
    public enum Tipo { TEXTO, ENTERO, PORCENTAJE }

    /** ancho en puntos del PDF; las columnas de cada reporte suman 515. */
    public record Col(String titulo, int ancho, Tipo tipo) {}
}