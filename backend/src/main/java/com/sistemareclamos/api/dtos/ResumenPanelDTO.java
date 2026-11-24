package com.sistemareclamos.api.dtos;

import java.util.List;
import java.util.Map;

public record ResumenPanelDTO(
        long total,
        Map<String, Long> porEstado,
        List<Conteo> porCategoria,
        List<Conteo> porMes,
        Semana semana,
        Plazos plazos,
        long sinAsignar,
        long muyAltaAbiertos
) {
    public record Conteo(String etiqueta, long cantidad) {}

    public record Semana(long nuevos, long enProceso, long resueltos, long cerrados, long fueraDePlazo) {}

    /** cerradosATiempo y cerradosTarde: casos ya cerrados. fueraDePlazoAbiertos: casos abiertos vencidos. */
    public record Plazos(long cerradosATiempo, long cerradosTarde, long fueraDePlazoAbiertos) {}
}