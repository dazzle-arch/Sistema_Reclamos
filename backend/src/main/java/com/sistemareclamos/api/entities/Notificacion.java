 package com.sistemareclamos.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Getter
@Setter
@NoArgsConstructor
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    @Column(nullable = false, length = 150)
    private String asunto;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    // Solo se llena cuando es para un funcionario/admin; null = notificación para el ciudadano
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_destino")
    private Usuario usuarioDestino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reclamo", nullable = false)
    private Reclamo reclamo;

}
