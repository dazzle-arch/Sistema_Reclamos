package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.reportes.ReporteService;
import com.sistemareclamos.api.reportes.ReporteService.ArchivoReporte;
import com.sistemareclamos.api.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    @GetMapping("/{tipo}")
    public ResponseEntity<byte[]> generar(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "pdf") String formato,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long idArea,
            @AuthenticationPrincipal UsuarioPrincipal usuario) {

        ArchivoReporte archivo = service.generar(tipo, formato, desde, hasta, estado, idArea, usuario.getUsername());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.mime()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(archivo.nombre()).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .cacheControl(CacheControl.noStore())
                .body(archivo.bytes());
    }
}