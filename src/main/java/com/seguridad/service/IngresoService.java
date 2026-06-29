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
    @Autowired StockSedeService stockSedeService;
    @Autowired NotificacionService notificacionService; 
    @Autowired UsuarioRepository usuarioRepo;  

    public Page<Ingreso> listar(int page, int size, Integer idSedeRestriccion) {
        Pageable pageable = PageRequest.of(page, size);
        return ingresoRepo.filtrarIngresos(idSedeRestriccion, null, null, pageable);
    }

    @Transactional
    public void guardar(Ingreso ingreso) {
        ingreso.setTotal(ingreso.getCantidad() * ingreso.getCostoUnitario());
        ingreso.setFecha(new Date());
          
        boolean esNuevo = ingreso.getIdIngreso() == null; 
        if (esNuevo) ingreso.setEstado(1);
            
        ingresoRepo.save(ingreso);

        Producto p = productoRepo.findById(ingreso.getProducto().getIdProducto()).get();
        p.setCostoUnitario(ingreso.getCostoUnitario());
        p.setStockTotal(p.getStockTotal() + ingreso.getCantidad());
        productoRepo.save(p);

        if (esNuevo) {
            notificacionService.notificarAdmins(
                "📦 Nuevo Ingreso",
                "Nuevo ingreso en " + ingreso.getSede().getNombre()
                    + " — " + ingreso.getCantidad()
                    + " unidades de " + p.getDescripcion(), 
                "INGRESO"
            );
        }

        stockSedeService.sumar(p, ingreso.getSede(), ingreso.getCantidad());
        int saldoAnterior = kardexRepo
            .findUltimoSaldo(p.getIdProducto(), ingreso.getSede().getIdSede())
            .orElse(0);
        Kardex k = new Kardex();
        k.setProducto(p); k.setSede(ingreso.getSede());
        k.setTipoMov("E"); k.setFecha(new Date());
        k.setCantidad(ingreso.getCantidad());
        k.setCostoUnit(ingreso.getCostoUnitario());
        k.setTotal(ingreso.getTotal());
        k.setSaldoCant(saldoAnterior + ingreso.getCantidad());
        k.setSaldoValor((saldoAnterior + ingreso.getCantidad()) * ingreso.getCostoUnitario());
        k.setReferencia("ING-" + ingreso.getIdIngreso());
        k.setObservacion(ingreso.getNroFactura());
        kardexRepo.save(k);
    }


    public void cambiarEstado(Integer id) {
        Ingreso i = ingresoRepo.findById(id).get();
        i.setEstado(i.getEstado() == 1 ? 2 : 1);
        ingresoRepo.save(i);
    }

    public Page<Ingreso> buscar(String texto, int page, int size, Integer idSedeRestriccion) {
        Pageable pageable = PageRequest.of(page, size);
        return ingresoRepo.filtrarIngresos(idSedeRestriccion, null, texto, pageable);
    }

    public Page<Ingreso> buscarConFiltro(String texto, int estado, int page, int size,
                                          Integer idSedeRestriccion) {
        Pageable pageable = PageRequest.of(page, size);
        return ingresoRepo.filtrarIngresos(idSedeRestriccion, estado, texto, pageable);
    }
}