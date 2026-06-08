package com.seguridad.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "tb_ingreso")
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idIngreso;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "id_sede")
    private Sede sede;

    private Integer cantidad;
    private Double costoUnitario;
    private Double total;

    @Temporal(TemporalType.DATE)
    private Date fecha;

    private String nroFactura;
    private String observacion;
    private Integer estado;
}
