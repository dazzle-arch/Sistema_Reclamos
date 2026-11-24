package com.sistemareclamos.api.controllers;

import com.sistemareclamos.api.dtos.ResumenPanelDTO;
import com.sistemareclamos.api.services.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/panel")
@RequiredArgsConstructor
public class PanelController {

    private final EstadisticasService service;

    @GetMapping("/resumen")
    public ResumenPanelDTO resumen() {
        return service.resumen();
    }
}