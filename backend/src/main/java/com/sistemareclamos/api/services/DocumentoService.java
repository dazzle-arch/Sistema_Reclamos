package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.ArchivoDescarga;
import com.sistemareclamos.api.dtos.DocumentoRequestDTO;
import com.sistemareclamos.api.dtos.DocumentoResponseDTO;
import com.sistemareclamos.api.security.UsuarioPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentoService {
    List<DocumentoResponseDTO> listar();
    DocumentoResponseDTO obtenerPorId(Long id, UsuarioPrincipal usuario);
    List<DocumentoResponseDTO> listarPorReclamo(Long idReclamo, UsuarioPrincipal usuario);
    DocumentoResponseDTO subirEvidencia(Long codigo, String dni, MultipartFile archivo);
    ArchivoDescarga descargar(Long id, UsuarioPrincipal usuario);
}