package com.seguridad.service;

import com.seguridad.model.*;
import com.seguridad.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class SalidaService {
    @Autowired SalidaRepository salidaRepo;
    @Autowired ProductoRepository productoRepo;
    @Autowired KardexRepository kardexRepo;
    @Autowired StockSedeService stockSedeService;
    @Autowired NotificacionService notificacionService; 

    public Page<Salida> listar(int page, int size, Integer idSedeRestriccion) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("idSalida").descending());
        return salidaRepo.filtrarSalidas(idSedeRestriccion, null, null, pageable);
    }

    @Transactional
    public void guardar(Salida salida) {
        salida.setFecha(new Date());
        boolean esNueva = (salida.getIdSalida() == null);
        if (esNueva) {
            salida.setEstado(1);
        }
        salidaRepo.save(salida);

        if (esNueva) {
            notificacionService.notificarAdmins(
                "📤 Nueva Salida",
                "Salida registrada en " + salida.getSede().getNombre()
                    + " — " + salida.getProducto().getDescripcion(),
                "SALIDA"
            );

            Producto p = productoRepo.findById(
                salida.getProducto().getIdProducto()).get();
            p.setStockTotal(p.getStockTotal() - salida.getCantidad());
            productoRepo.save(p);
            stockSedeService.restar(p, salida.getSede(), salida.getCantidad());
            if (p.getStockTotal() <= 0) {
                String titulo  = "🚨 Stock Crítico";
                String mensaje = "CRÍTICO: " + p.getDescripcion()
                    + " sin stock en " + salida.getSede().getNombre();
                notificacionService.notificarAlmacenDeSede(
                    salida.getSede().getIdSede(), titulo, mensaje, "STOCK_CRITICO");
                notificacionService.notificarAdmins(titulo, mensaje, "STOCK_CRITICO");
            }

            int saldoAnterior = kardexRepo
                .findUltimoSaldo(p.getIdProducto(), salida.getSede().getIdSede())
                .orElse(0);
            Kardex k = new Kardex();
            k.setProducto(p);
            k.setSede(salida.getSede());
            k.setTipoMov("S");
            k.setFecha(new Date());
            k.setCantidad(salida.getCantidad());
            k.setCostoUnit(p.getCostoUnitario());
            k.setTotal(salida.getCantidad() * p.getCostoUnitario());
            k.setSaldoCant(saldoAnterior - salida.getCantidad());
            k.setSaldoValor((saldoAnterior - salida.getCantidad()) * p.getCostoUnitario());
            k.setReferencia("SAL-" + salida.getIdSalida());
            k.setObservacion(salida.getObservacion());
            kardexRepo.save(k);
        }
    }

    public void cambiarEstado(Integer id) {
        Salida s = salidaRepo.findById(id).get();
        s.setEstado(s.getEstado() == 1 ? 2 : 1);
        salidaRepo.save(s);
    }

    public Page<Salida> buscar(String texto, int page, int size, Integer idSedeRestriccion) {
        Pageable pageable = PageRequest.of(page, size);
        return salidaRepo.filtrarSalidas(idSedeRestriccion, null, texto, pageable);
    }

    public Page<Salida> buscarConFiltro(String texto, int estado, int page, int size,
                                         Integer idSedeRestriccion) {
        Pageable pageable = PageRequest.of(page, size);
        return salidaRepo.filtrarSalidas(idSedeRestriccion, estado, texto, pageable);
    }
}