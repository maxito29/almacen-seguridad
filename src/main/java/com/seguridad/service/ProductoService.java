package com.seguridad.service;

import com.seguridad.model.Producto;
import com.seguridad.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    @Autowired ProductoRepository productoRepo;

    public Page<Producto> listar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
            Sort.by("idProducto").ascending());
        return productoRepo.findAll(pageable);
    }

    public void guardar(Producto producto) {
        if (producto.getEstado() == null) {
            producto.setEstado(1);
        }
        if (producto.getStockTotal() == null) {
            producto.setStockTotal(0);
        }
        productoRepo.save(producto);
    }

    public void cambiarEstado(String id) {
        Producto p = productoRepo.findById(id).get();
        p.setEstado(p.getEstado() == 1 ? 2 : 1);
        productoRepo.save(p);
    }

    public Page<Producto> buscar(String texto, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepo
            .findByDescripcionContainingIgnoreCaseOrIdProductoContainingIgnoreCase(
                texto, texto, pageable);
    }

    public Page<Producto> buscarConFiltro(String texto, int estado,
                                           int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepo
            .findByEstadoAndDescripcionContainingIgnoreCase(
                estado, texto, pageable);
    }

    public Page<Producto> listarPorEstado(int estado, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepo.findByEstado(estado, pageable);
    }
}
