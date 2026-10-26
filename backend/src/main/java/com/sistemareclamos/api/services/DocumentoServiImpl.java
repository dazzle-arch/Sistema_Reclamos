package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.DocumentoRequestDTO;
import com.sistemareclamos.api.dtos.DocumentoResponseDTO;
import com.sistemareclamos.api.entities.Documento;
import com.sistemareclamos.api.entities.Reclamo;
import com.sistemareclamos.api.repositories.DocumentoRepository;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoServiImpl implements DocumentoService {
    private final DocumentoRepository repository;
    private final ReclamoRepository reclamoRepository;

    @Override
    public List<DocumentoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public DocumentoResponseDTO obtenerPorId(Long id) {
        return repository.findById(id).map(this::toResponse).orElse(null);
    }

    @Override
    public DocumentoResponseDTO crear(DocumentoRequestDTO dto) {
        Reclamo reclamo = reclamoRepository.findById(dto.idReclamo()).orElse(null);
        if (reclamo == null) {
            return null; // reclamo no existe
        }

        Documento d = new Documento();
        d.setReclamo(reclamo);
        d.setNombreArchivo(dto.nombreArchivo());
        d.setTipoArchivo(dto.tipoArchivo());
        d.setDescripcion(dto.descripcion());
        d.setFechaCarga(LocalDateTime.now());

        return toResponse(repository.save(d));
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
