package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.ArchivoDescarga;
import com.sistemareclamos.api.dtos.DocumentoResponseDTO;
import com.sistemareclamos.api.entities.Documento;
import com.sistemareclamos.api.entities.Reclamo;
import com.sistemareclamos.api.exception.ConflictoException;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.exception.SolicitudInvalidaException;
import com.sistemareclamos.api.repositories.DocumentoRepository;
import com.sistemareclamos.api.repositories.ReclamoRepository;
import com.sistemareclamos.api.security.AccesoReclamo;
import com.sistemareclamos.api.security.AlmacenArchivos;
import com.sistemareclamos.api.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentoServiImpl implements DocumentoService {

    private static final String ORIGEN_EVIDENCIA = "EVIDENCIA";

    private final DocumentoRepository repository;
    private final ReclamoRepository reclamoRepository;
    private final AccesoReclamo acceso;
    private final AlmacenArchivos almacen;

    @Override
    public List<DocumentoResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public DocumentoResponseDTO obtenerPorId(Long id, UsuarioPrincipal usuario) {
        Documento d = buscar(id);
        acceso.verificarLectura(d.getReclamo(), usuario);
        return toResponse(d);
    }

    @Override
    public List<DocumentoResponseDTO> listarPorReclamo(Long idReclamo, UsuarioPrincipal usuario) {
        Reclamo reclamo = reclamoRepository.findById(idReclamo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reclamo no encontrado con id: " + idReclamo));
        acceso.verificarLectura(reclamo, usuario);
        return repository.findByReclamoIdReclamo(idReclamo).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public DocumentoResponseDTO subirEvidencia(Long codigo, String dni, MultipartFile archivo) {
        // Mismo mensaje si falla el código o el DNI: no se revela cuál existe
        Reclamo reclamo = reclamoRepository.findByIdReclamoAndCiudadanoDni(codigo, dni.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró un reclamo con esos datos"));

        if ("CERRADO".equals(reclamo.getEstado())) {
            throw new SolicitudInvalidaException("El reclamo está cerrado");
        }
        if (repository.existsByReclamoIdReclamoAndOrigen(codigo, ORIGEN_EVIDENCIA)) {
            throw new ConflictoException("Este reclamo ya tiene evidencia adjunta");
        }

        AlmacenArchivos.Guardado g = almacen.guardar(archivo);

        // Si la transacción no se confirma, el archivo no queda huérfano en disco
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) almacen.eliminar(g.nombreGuardado());
            }
        });

        Documento d = new Documento();
        d.setReclamo(reclamo);
        d.setNombreArchivo(g.nombreOriginal());
        d.setTipoArchivo(g.mime());
        d.setRutaArchivo(g.nombreGuardado());
        d.setTamanio(g.tamanio());
        d.setOrigen(ORIGEN_EVIDENCIA);
        d.setDescripcion("Evidencia adjunta por el ciudadano");
        d.setFechaCarga(LocalDateTime.now());
        return toResponse(repository.save(d));
    }

    @Override
    public ArchivoDescarga descargar(Long id, UsuarioPrincipal usuario) {
        Documento d = buscar(id);
        acceso.verificarLectura(d.getReclamo(), usuario);
        return new ArchivoDescarga(almacen.cargar(d.getRutaArchivo()), d.getNombreArchivo(), d.getTipoArchivo());
    }

    private Documento buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con id: " + id));
    }

    private DocumentoResponseDTO toResponse(Documento d) {
        return new DocumentoResponseDTO(
                d.getIdDocumento(),
                d.getNombreArchivo(),
                d.getTipoArchivo(),
                d.getDescripcion(),
                d.getFechaCarga(),
                d.getReclamo().getIdReclamo(),
                d.getOrigen(),
                d.getTamanio()
        );
    }
}