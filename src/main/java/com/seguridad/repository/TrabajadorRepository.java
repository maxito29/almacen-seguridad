package com.seguridad.repository;

import com.seguridad.model.Trabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Integer> {

    List<Trabajador> findBySede_IdSede(int idSede);

    List<Trabajador> findByActivoCesado(String activoCesado);

    Page<Trabajador> findByActivoCesado(
            String estado,
            Pageable pageable);

    Page<Trabajador>
    findByNombreCompletoContainingIgnoreCaseOrDocumentoIdentidadContainingIgnoreCase(
            String nombre,
            String dni,
            Pageable pageable);

    Page<Trabajador>
    findByActivoCesadoAndNombreCompletoContainingIgnoreCase(
            String estado,
            String texto,
            Pageable pageable);
}