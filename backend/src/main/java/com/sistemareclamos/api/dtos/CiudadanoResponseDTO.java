package com.sistemareclamos.api.dtos;

public record CiudadanoResponseDTO(
   Long idCiudadano,
   String dni,
   String nombres,
   String apellidos,
   String correo,
   String telefono,
   String sexo
){}
