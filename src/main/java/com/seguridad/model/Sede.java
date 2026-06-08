package com.seguridad.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_sede")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSede;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String direccion;
    private Integer estado;
}