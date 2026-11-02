package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.DocumentoRequestDTO;
import com.sistemareclamos.api.dtos.DocumentoResponseDTO;
import com.sistemareclamos.api.entities.Documento;
import com.sistemareclamos.api.entities.Reclamo;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.repositories.DocumentoRepository;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentoServiImpl implements DocumentoService {
    private final DocumentoRepository repository;
    private final ReclamoRepository reclamoRepository;

    @Override
    public List<DocumentoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public DocumentoResponseDTO obtenerPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con id: " + id));
    }

    @Override
    public List<DocumentoResponseDTO> listarPorReclamo(Long idReclamo) {
        if (!reclamoRepository.existsById(idReclamo)) {
            throw new RecursoNoEncontradoException("Reclamo no encontrado con id: " + idReclamo);
        }
        return repository.findByReclamoIdReclamo(idReclamo).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public DocumentoResponseDTO crear(DocumentoRequestDTO dto) {
        Reclamo reclamo = reclamoRepository.findById(dto.idReclamo())
                .orElseThrow(() -> new RecursoNoEncontradoException("Reclamo no encontrado con id: " + dto.idReclamo()));

        Documento documento = new Documento();
        documento.setReclamo(reclamo);
        documento.setNombreArchivo(dto.nombreArchivo());
        documento.setTipoArchivo(dto.tipoArchivo());
        documento.setDescripcion(dto.descripcion());
        documento.setFechaCarga(LocalDateTime.now());

        return toResponse(repository.save(documento));
    }

    private DocumentoResponseDTO toResponse(Documento d) {
        return new DocumentoResponseDTO(
                d.getIdDocumento(),
                d.getNombreArchivo(),
                d.getTipoArchivo(),
                d.getDescripcion(),
                d.getFechaCarga(),
                d.getReclamo().getIdReclamo()
        );
    }


}
