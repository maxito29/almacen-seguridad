package com.seguridad.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String titulo;

    @Column(length = 1000)
    private String mensaje;

    private boolean leida = false;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private String tipo; 
}