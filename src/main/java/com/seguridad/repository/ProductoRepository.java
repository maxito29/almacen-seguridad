package com.seguridad.repository;

import com.seguridad.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, String> {

    List<Producto> findByTipo_IdTipo(int idTipo);

    // Paginado por estado
    Page<Producto> findByEstado(int estado, Pageable pageable);

    // Busqueda solo por texto
    Page<Producto> findByDescripcionContainingIgnoreCaseOrIdProductoContainingIgnoreCase(
            String descripcion, String codigo, Pageable pageable);

    // Busqueda combinada texto + estado
    Page<Producto> findByEstadoAndDescripcionContainingIgnoreCase(
            int estado, String descripcion, Pageable pageable);
    
    @Query("SELECT t.nombre, COUNT(p) FROM Producto p " +
           "JOIN p.tipo t GROUP BY t.nombre")
    List<Object[]> contarPorTipo();

    @Query("SELECT p.descripcion, p.stockTotal FROM Producto p " +
           "ORDER BY p.stockTotal DESC")
    List<Object[]> top5Productos(Pageable pageable);
}