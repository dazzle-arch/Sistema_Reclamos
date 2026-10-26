package com.sistemareclamos.api.entities;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ciudadano")
@Data
@NoArgsConstructor

public class Ciudadano {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCiudadano;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(nullable = false, length = 120)
    private String nombres;

    @Column(nullable = false, length = 120)
    private String apellidos;

    @Column(nullable = false, length = 160)
    private String correo;

    @Column(nullable = false, length = 9)
    private String telefono;

    @Column(nullable = false, length = 1)
    private String sexo;  // 'F' o 'M'
}
