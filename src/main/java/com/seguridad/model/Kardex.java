package com.seguridad.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "tb_kardex")
public class Kardex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idKardex;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_sede")
    private Sede sede;

    @Column(nullable = false)
    private String tipoMov;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    private Integer cantidad;
    private Double costoUnit;
    private Double total;
    private Integer saldoCant;
    private Double saldoValor;
    private String referencia;
    private String observacion;
}
