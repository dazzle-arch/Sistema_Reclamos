package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.DocumentoRequestDTO;
import com.sistemareclamos.api.dtos.DocumentoResponseDTO;

import java.util.List;

public interface DocumentoService {
    List<DocumentoResponseDTO> listar();
    DocumentoResponseDTO obtenerPorId(Long id);
    List<DocumentoResponseDTO> listarPorReclamo(Long idReclamo);
    DocumentoResponseDTO crear(DocumentoRequestDTO dto);
}
