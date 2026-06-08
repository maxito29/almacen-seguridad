package com.seguridad.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nombre;

    @Column(nullable = false)
    private String rol;

    @ManyToOne
    @JoinColumn(name = "id_sede")
    private Sede sede;

    private Integer estado;
}
