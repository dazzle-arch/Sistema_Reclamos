package com.sistemareclamos.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento")
@Getter
@Setter
@NoArgsConstructor
public class Seguimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSeguimiento;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(length = 1000)
    private String observacion;

    @Column(length = 1000)
    private String respuesta;

    @Column(name = "accion_realizada", length = 350)
    private String accionRealizada;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reclamo", nullable = false)
    private Reclamo reclamo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
