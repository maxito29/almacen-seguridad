package com.seguridad.service;


import com.seguridad.model.*;
import com.seguridad.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class IngresoService {

    @Autowired IngresoRepository ingresoRepo;
    @Autowired ProductoRepository productoRepo;
    @Autowired KardexRepository kardexRepo;

    // Listado con paginacion
    public Page<Ingreso> listar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ingresoRepo.findAll(pageable);
    }

    // Guardar ingreso + actualizar stock + registrar Kardex
    @Transactional
    public void guardar(Ingreso ingreso) {
        ingreso.setTotal(ingreso.getCantidad() * ingreso.getCostoUnitario());
        ingreso.setFecha(new Date());

        if (ingreso.getIdIngreso() == null) {
            ingreso.setEstado(1);
        }

        ingresoRepo.save(ingreso);

        Producto p = productoRepo.findById(
            ingreso.getProducto().getIdProducto()).get();
        p.setCostoUnitario(ingreso.getCostoUnitario());
        p.setStockTotal(p.getStockTotal() + ingreso.getCantidad());
        productoRepo.save(p);

        // Registro en Kardex
        int saldoAnterior = kardexRepo
            .findUltimoSaldo(p.getIdProducto(), ingreso.getSede().getIdSede())
            .orElse(0);

        Kardex k = new Kardex();
        k.setProducto(p);
        k.setSede(ingreso.getSede());
        k.setTipoMov("E");
        k.setFecha(new Date());
        k.setCantidad(ingreso.getCantidad());
        k.setCostoUnit(ingreso.getCostoUnitario());
        k.setTotal(ingreso.getTotal());
        k.setSaldoCant(saldoAnterior + ingreso.getCantidad());
        k.setSaldoValor((saldoAnterior + ingreso.getCantidad())
            * ingreso.getCostoUnitario());
        k.setReferencia("ING-" + ingreso.getIdIngreso());
        k.setObservacion(ingreso.getNroFactura());
        kardexRepo.save(k);
    }

    public void cambiarEstado(Integer id) {
        Ingreso i = ingresoRepo.findById(id).get();
        i.setEstado(i.getEstado() == 1 ? 2 : 1);
        ingresoRepo.save(i);
    }
    
    public Page<Ingreso> buscar(String texto, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ingresoRepo
                .findByProductoDescripcionContainingIgnoreCaseOrProveedorNombreContainingIgnoreCaseOrSedeNombreContainingIgnoreCase(
                        texto,
                        texto,
                        texto,
                        pageable);
    }
    
    public Page<Ingreso> buscarConFiltro(String texto, int estado, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ingresoRepo
            .findByEstadoAndProductoDescripcionContainingIgnoreCaseOrEstadoAndProveedorNombreContainingIgnoreCaseOrEstadoAndSedeNombreContainingIgnoreCase(
                estado, texto,
                estado, texto,
                estado, texto,
                pageable);
    }
}
