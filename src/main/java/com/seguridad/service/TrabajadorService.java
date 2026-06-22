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

    public Page<Trabajador> listar(int page, int size, Integer idSedeRestriccion) {
        Pageable pageable = PageRequest.of(page, size);
        return trabajadorRepo.filtrarTrabajadores(idSedeRestriccion, null, null, pageable);
    }

    public void guardar(Trabajador trabajador) {
        if (trabajador.getIdTrabajador() == null) {
            trabajador.setEstado(1);
            if (trabajador.getActivoCesado() == null ||
                trabajador.getActivoCesado().isEmpty()) {
                trabajador.setActivoCesado("ACTIVO");
            }
        } else {
            trabajadorRepo.findById(trabajador.getIdTrabajador())
                .ifPresent(original -> {
                    if (trabajador.getEstado() == null) {
                        trabajador.setEstado(original.getEstado());
                    }
                });
        }
        trabajadorRepo.save(trabajador);
    }

    public void cambiarEstado(Integer id) {
        Trabajador t = trabajadorRepo.findById(id).get();
        if ("ACTIVO".equalsIgnoreCase(t.getActivoCesado())) {
            t.setActivoCesado("CESADO");
        } else {
            t.setActivoCesado("ACTIVO");
        }
        trabajadorRepo.save(t);
    }

    public Trabajador obtenerPorId(Integer id) {
        return trabajadorRepo.findById(id).orElse(null);
    }

    public Page<Trabajador> buscar(String texto, int page, int size, Integer idSedeRestriccion) {
        Pageable pageable = PageRequest.of(page, size);
        return trabajadorRepo.filtrarTrabajadores(idSedeRestriccion, null, texto, pageable);
    }

    public Page<Trabajador> listarPorEstado(String estado, int page, int size,
                                             Integer idSedeRestriccion) {
        Pageable pageable = PageRequest.of(page, size);
        return trabajadorRepo.filtrarTrabajadores(idSedeRestriccion, estado, null, pageable);
    }

    public Page<Trabajador> buscarConFiltro(String texto, String estado, int page, int size,
                                             Integer idSedeRestriccion) {
        Pageable pageable = PageRequest.of(page, size);
        return trabajadorRepo.filtrarTrabajadores(idSedeRestriccion, estado, texto, pageable);
    }
}