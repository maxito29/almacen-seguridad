package com.seguridad.service;

import com.seguridad.model.Proveedor;
import com.seguridad.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class ProveedorService {

    @Autowired ProveedorRepository proveedorRepo;

    public Page<Proveedor> listar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
            Sort.by("idProveedor").descending());
        return proveedorRepo.findAll(pageable);
    }

    public void guardar(Proveedor proveedor) {
        if (proveedor.getEstado() == null) {
            proveedor.setEstado(1);
        }

        if (proveedor.getTipo() == null || proveedor.getTipo().isBlank()) {
            if (proveedor.getRuc() != null && proveedor.getRuc().startsWith("20")) {
                proveedor.setTipo("JURIDICA");
            } else if (proveedor.getRuc() != null && proveedor.getRuc().startsWith("10")) {
                proveedor.setTipo("NATURAL");
            }
        }

        proveedorRepo.save(proveedor);
    }

    public void cambiarEstado(Integer id) {
        Proveedor p = proveedorRepo.findById(id).get();
        p.setEstado(p.getEstado() == 1 ? 2 : 1);
        proveedorRepo.save(p);
    }

    public Page<Proveedor> buscar(String texto, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return proveedorRepo
            .findByNombreContainingIgnoreCaseOrRucContaining(
                texto, texto, pageable);
    }

    public Page<Proveedor> buscarConFiltro(String texto, int estado,
                                            int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return proveedorRepo
            .findByEstadoAndNombreContainingIgnoreCase(
                estado, texto, pageable);
    }

    public Page<Proveedor> listarPorEstado(int estado, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return proveedorRepo.findByEstado(estado, pageable);
    }
}