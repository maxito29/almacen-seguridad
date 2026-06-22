package com.seguridad.repository;
import com.seguridad.model.Sede;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedeRepository extends JpaRepository<Sede, Integer> {
    Page<Sede> findByEstado(int estado, Pageable pageable);
    Page<Sede> findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCase(
            String nombre, String codigo, Pageable pageable);
    Page<Sede> findByEstadoAndNombreContainingIgnoreCase(
            int estado, String nombre, Pageable pageable);
}