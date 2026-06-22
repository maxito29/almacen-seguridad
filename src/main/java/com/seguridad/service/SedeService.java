package com.seguridad.service;

import com.seguridad.model.Sede;
import com.seguridad.repository.SedeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class SedeService {

    @Autowired SedeRepository sedeRepo;

    public Page<Sede> listar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("idSede").ascending());
        return sedeRepo.findAll(pageable);
    }

    public void guardar(Sede sede) {
        if (sede.getEstado() == null) {
            sede.setEstado(1);
        }
        sedeRepo.save(sede);
    }

    public void cambiarEstado(Integer id) {
        Sede s = sedeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        s.setEstado(s.getEstado() == 1 ? 2 : 1);
        sedeRepo.save(s);
    }

    public Page<Sede> buscar(String texto, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sedeRepo.findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCase(
                texto, texto, pageable);
    }

    public Page<Sede> buscarConFiltro(String texto, int estado, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sedeRepo.findByEstadoAndNombreContainingIgnoreCase(estado, texto, pageable);
    }

    public Page<Sede> listarPorEstado(int estado, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sedeRepo.findByEstado(estado, pageable);
    }
}