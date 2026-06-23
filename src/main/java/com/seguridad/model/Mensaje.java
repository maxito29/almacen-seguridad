package com.seguridad.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_mensaje")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMensaje;

    @ManyToOne
    @JoinColumn(name = "id_emisor", nullable = false)
    private Usuario emisor;

    @ManyToOne
    @JoinColumn(name = "id_destinatario")
    private Usuario destinatario;

    @Column(nullable = false, length = 1000)
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Boolean leido = false;

    @PrePersist
    public void prePersist() {
        if (fecha == null) fecha = LocalDateTime.now();
        if (leido == null) leido = false;
    }
}