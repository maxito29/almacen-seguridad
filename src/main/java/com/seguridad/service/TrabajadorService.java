package com.seguridad.service;

import com.seguridad.model.Trabajador;
import com.seguridad.repository.TrabajadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class TrabajadorService {

    @Autowired
    TrabajadorRepository trabajadorRepo;

    // Listado 
    public Page<Trabajador> listar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return trabajadorRepo.findAll(pageable);
    }

    // Guardar 
    public void guardar(Trabajador trabajador) {
        if (trabajador.getIdTrabajador() == null) {
            // Solo al crear
            trabajador.setEstado(1);
            if (trabajador.getActivoCesado() == null ||
                trabajador.getActivoCesado().isEmpty()) {
                trabajador.setActivoCesado("ACTIVO");
            }
        } else {
            // Al editar — recuperar estado numérico del registro original
            trabajadorRepo.findById(trabajador.getIdTrabajador())
                .ifPresent(original -> {
                    if (trabajador.getEstado() == null) {
                        trabajador.setEstado(original.getEstado());
                    }
                });
        }
        trabajadorRepo.save(trabajador);
    }

    // Cambiar estado 
    public void cambiarEstado(Integer id) {

        Trabajador t = trabajadorRepo.findById(id).get();

        if ("ACTIVO".equalsIgnoreCase(t.getActivoCesado())) {
            t.setActivoCesado("CESADO");
        } else {
            t.setActivoCesado("ACTIVO");
        }

        trabajadorRepo.save(t);
    }

    // Buscar 
    public Page<Trabajador> buscar(
            String texto,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return trabajadorRepo
                .findByNombreCompletoContainingIgnoreCaseOrDocumentoIdentidadContainingIgnoreCase(
                        texto,
                        texto,
                        pageable);
    }
 // Filtrar estado
    public Page<Trabajador> listarPorEstado(
            String estado,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return trabajadorRepo.findByActivoCesado(
                estado,
                pageable);
    }

    // Busc
    public Page<Trabajador> buscarConFiltro(
            String texto,
            String estado,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        return trabajadorRepo
                .findByActivoCesadoAndNombreCompletoContainingIgnoreCase(
                        estado,
                        texto,
                        pageable);
    }
}