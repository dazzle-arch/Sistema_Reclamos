package com.sistemareclamos.api.controllers;
import com.sistemareclamos.api.dtos.CiudadanoRequestDTO;
import com.sistemareclamos.api.dtos.CiudadanoResponseDTO;
import com.sistemareclamos.api.services.CiudadanoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("api/ciudadanos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CiudadanoController {
    private final CiudadanoService service;

    @GetMapping
    public List<CiudadanoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public CiudadanoResponseDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping("/dni/{dni}")
    public CiudadanoResponseDTO obtenerPorDni(@PathVariable String dni) {
        return service.obtenerPorDni(dni);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CiudadanoResponseDTO crear(@Valid @RequestBody CiudadanoRequestDTO dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public CiudadanoResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody CiudadanoRequestDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}