package com.seguridad.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_producto")
public class Producto {

    @Id
    @Column(name = "id_producto")
    private String idProducto;

    private String eanInt;

    @Column(nullable = false)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_tipo")
    private TipoEquipo tipo;

    private Double costoUnitario;
    private Double precioVenta;
    private Integer stockTotal;
    private Integer estado;
}
