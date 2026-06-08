package com.seguridad.model;


import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "tb_trabajador")
public class Trabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTrabajador;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String documentoIdentidad;

    private String puesto;
    private String cliente;

    @ManyToOne
    @JoinColumn(name = "id_sede")
    private Sede sede;

    private String activoCesado;

    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;

    private Integer estado;
}
