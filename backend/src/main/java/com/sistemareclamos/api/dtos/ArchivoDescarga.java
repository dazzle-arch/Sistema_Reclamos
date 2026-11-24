package com.sistemareclamos.api.dtos;

import org.springframework.core.io.Resource;

public record ArchivoDescarga(
        Resource recurso,
        String nombre,
        String mime
) {}