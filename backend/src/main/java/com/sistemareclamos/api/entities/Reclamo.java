package com.sistemareclamos.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "reclamo")
@Getter       // Genera solo los getters
@Setter       // Genera solo los setters
@NoArgsConstructor
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReclamo;

    @Column(nullable = false, length = 150)
    private String asunto;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, length = 20)
    private String estado = "REGISTRADO";

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(nullable = false, length = 10)
    private String prioridad = "MEDIA";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciudadano", nullable = false)
    private Ciudadano ciudadano;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

}