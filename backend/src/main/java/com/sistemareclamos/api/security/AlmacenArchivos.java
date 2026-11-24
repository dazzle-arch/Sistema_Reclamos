package com.sistemareclamos.api.security;

import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.exception.SolicitudInvalidaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class AlmacenArchivos {

    public static final long MAX_BYTES = 5L * 1024 * 1024;

    public record Guardado(String nombreGuardado, String nombreOriginal, String mime, long tamanio) {}

    private final Path base;

    public AlmacenArchivos(@Value("${app.uploads.dir}") String dir) {
        this.base = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(base);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear la carpeta de subidas", e);
        }
    }

    public Guardado guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new SolicitudInvalidaException("Debes adjuntar un archivo");
        }
        if (archivo.getSize() > MAX_BYTES) {
            throw new SolicitudInvalidaException("El archivo supera el máximo de 5 MB");
        }

        byte[] cab = leerCabecera(archivo);
        String mime;
        String ext;
        if (cab.length >= 5 && cab[0] == '%' && cab[1] == 'P' && cab[2] == 'D' && cab[3] == 'F' && cab[4] == '-') {
            mime = "application/pdf";
            ext = ".pdf";
        } else if (cab.length >= 3 && (cab[0] & 0xFF) == 0xFF && (cab[1] & 0xFF) == 0xD8 && (cab[2] & 0xFF) == 0xFF) {
            mime = "image/jpeg";
            ext = ".jpg";
        } else if (cab.length >= 8 && (cab[0] & 0xFF) == 0x89 && cab[1] == 'P' && cab[2] == 'N' && cab[3] == 'G'
                && cab[4] == 0x0D && cab[5] == 0x0A && cab[6] == 0x1A && cab[7] == 0x0A) {
            mime = "image/png";
            ext = ".png";
        } else {
            throw new SolicitudInvalidaException("Solo se permiten archivos PDF, JPG o PNG");
        }

        String nombreGuardado = UUID.randomUUID() + ext;
        Path destino = base.resolve(nombreGuardado).normalize();
        if (!destino.startsWith(base)) {
            throw new SolicitudInvalidaException("Nombre de archivo no válido");
        }
        try (InputStream in = archivo.getInputStream()) {
            Files.copy(in, destino);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo", e);
        }
        return new Guardado(nombreGuardado, limpiarNombre(archivo.getOriginalFilename(), ext), mime, archivo.getSize());
    }

    public Resource cargar(String nombreGuardado) {
        Path p = base.resolve(nombreGuardado).normalize();
        if (!p.startsWith(base) || !Files.isRegularFile(p)) {
            throw new RecursoNoEncontradoException("El archivo ya no está disponible");
        }
        return new FileSystemResource(p);
    }

    public void eliminar(String nombreGuardado) {
        try {
            Path p = base.resolve(nombreGuardado).normalize();
            if (p.startsWith(base)) Files.deleteIfExists(p);
        } catch (IOException ignorado) {
            // limpieza de mejor esfuerzo
        }
    }

    private byte[] leerCabecera(MultipartFile archivo) {
        try (InputStream in = archivo.getInputStream()) {
            return in.readNBytes(8);
        } catch (IOException e) {
            throw new SolicitudInvalidaException("No se pudo leer el archivo");
        }
    }

    private String limpiarNombre(String original, String ext) {
        String n = StringUtils.cleanPath(original == null ? "" : original);
        n = n.substring(n.lastIndexOf('/') + 1).replaceAll("[^\\p{L}\\p{N}._\\- ]", "_").trim();
        if (n.length() > 200) n = n.substring(n.length() - 200);
        return n.isBlank() ? "evidencia" + ext : n;
    }
}