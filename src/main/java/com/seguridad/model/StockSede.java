package com.seguridad.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_stock_sede",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_stock_sede_producto_sede",
           columnNames = {"id_producto", "id_sede"}))
public class StockSede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idStockSede;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;

    @Column(nullable = false)
    private Integer cantidad;
}