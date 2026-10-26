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
    public List<CiudadanoResponseDTO>listar(){
        return this.service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CiudadanoResponseDTO> obtenerPorId(@PathVariable Long id){
        CiudadanoResponseDTO dto = service.obtenerPorId(id);
        if(dto == null){
            return ResponseEntity.notFound().build(); //404
        }
        return ResponseEntity.ok(dto); //200
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<CiudadanoResponseDTO> obtenerPorDni(@PathVariable String dni) {
        CiudadanoResponseDTO dto = service.obtenerPorDni(dni);
        if (dto == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(dto); // 200
    }

    @PostMapping
    public ResponseEntity<CiudadanoResponseDTO> crear(@Valid @RequestBody CiudadanoRequestDTO dto){
        CiudadanoResponseDTO creado = service.crear(dto);
        if (creado == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409: DNI repetido
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(creado); //201
    }

    @PutMapping("/{id}")
    public ResponseEntity<CiudadanoResponseDTO> actualizar(@PathVariable Long id, @Valid  @RequestBody CiudadanoRequestDTO dto){
        CiudadanoResponseDTO actualizado = service.actualizar(id, dto);
        if(actualizado == null){
            return ResponseEntity.badRequest().build(); //400 no existe o DNI repetido
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        if (!service.eliminar(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}