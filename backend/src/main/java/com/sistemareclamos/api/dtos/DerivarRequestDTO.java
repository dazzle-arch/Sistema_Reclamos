package com.sistemareclamos.api.dtos;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DerivarRequestDTO (
        @NotNull Long idArea,
        @NotNull Long idUsuarioAsignado,
        @Size(max = 500) String observacion
) {}