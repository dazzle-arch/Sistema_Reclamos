package com.sistemareclamos.api.controllers;


import com.sistemareclamos.api.dtos.ArchivoDescarga;
import com.sistemareclamos.api.dtos.DocumentoResponseDTO;
import com.sistemareclamos.api.security.UsuarioPrincipal;
import com.sistemareclamos.api.services.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService service;

    @GetMapping
    public List<DocumentoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public DocumentoResponseDTO obtenerPorId(@PathVariable Long id,
                                             @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.obtenerPorId(id, usuario);
    }

    @GetMapping("/reclamo/{idReclamo}")
    public List<DocumentoResponseDTO> listarPorReclamo(@PathVariable Long idReclamo,
                                                       @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return service.listarPorReclamo(idReclamo, usuario);
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<Resource> descargar(@PathVariable Long id,
                                              @AuthenticationPrincipal UsuarioPrincipal usuario) {
        ArchivoDescarga a = service.descargar(id, usuario);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(a.mime()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(a.nombre(), StandardCharsets.UTF_8).build().toString())
                .body(a.recurso());
    }

    /** Pública: el ciudadano adjunta evidencia con su código y DNI. */
    @PostMapping(value = "/evidencia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoResponseDTO subirEvidencia(@RequestParam Long codigo,
                                               @RequestParam String dni,
                                               @RequestParam("archivo") MultipartFile archivo) {
        return service.subirEvidencia(codigo, dni, archivo);
    }
}