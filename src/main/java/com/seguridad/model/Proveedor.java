package com.seguridad.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @Column(nullable = false)
    private String nombre;

    private String ruc;
    private String direccion;
    private String telefono;
    private String tipo;

    @Column(name = "estado_sunat")
    private String estadoSunat;

    private Integer estado;
}