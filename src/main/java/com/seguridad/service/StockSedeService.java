package com.seguridad.service;

import com.seguridad.model.Producto;
import com.seguridad.model.Sede;
import com.seguridad.model.StockSede;
import com.seguridad.repository.StockSedeRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class StockSedeService {

    @Autowired
    private StockSedeRepository stockSedeRepo;

    public void sumar(Producto producto, Sede sede, int cantidad) {
        StockSede stock = obtenerOcrear(producto, sede);
        stock.setCantidad(stock.getCantidad() + cantidad);
        stockSedeRepo.save(stock);
    }

    public void restar(Producto producto, Sede sede, int cantidad) {
        StockSede stock = obtenerOcrear(producto, sede);
        if (stock.getCantidad() < cantidad) {
            throw new RuntimeException(
                "Stock insuficiente en " + sede.getNombre() +
                " para " + producto.getDescripcion() +
                " (disponible: " + stock.getCantidad() + ", solicitado: " + cantidad + ")");
        }
        stock.setCantidad(stock.getCantidad() - cantidad);
        stockSedeRepo.save(stock);
    }

    public int obtenerCantidad(String idProducto, Integer idSede) {
        return stockSedeRepo.findByProducto_IdProductoAndSede_IdSede(idProducto, idSede)
                .map(StockSede::getCantidad)
                .orElse(0);
    }

    private StockSede obtenerOcrear(Producto producto, Sede sede) {
        return stockSedeRepo
                .findByProducto_IdProductoAndSede_IdSede(producto.getIdProducto(), sede.getIdSede())
                .orElseGet(() -> {
                    StockSede nuevo = new StockSede();
                    nuevo.setProducto(producto);
                    nuevo.setSede(sede);
                    nuevo.setCantidad(0);
                    return nuevo;
                });
    }
    
    public List<Object[]> top5PorSede(Integer idSede) {
        return stockSedeRepo.top5PorSede(idSede, PageRequest.of(0, 5));
    }
}