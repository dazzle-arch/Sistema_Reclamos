package com.sistemareclamos.api.dtos;
import java.time.LocalDateTime;

public record ErrorResponse (LocalDateTime fecha, int status, String mensaje){}
