package com.seguridad.repository;

import com.seguridad.model.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    Page<Proveedor> findByEstado(int estado, Pageable pageable);

    Page<Proveedor> findByNombreContainingIgnoreCaseOrRucContaining(
            String nombre, String ruc, Pageable pageable);

    Page<Proveedor> findByEstadoAndNombreContainingIgnoreCase(
            int estado, String nombre, Pageable pageable);
}