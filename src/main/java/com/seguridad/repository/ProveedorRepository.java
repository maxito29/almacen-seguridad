package com.seguridad.repository;

import com.seguridad.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {}