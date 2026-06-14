package com.seguridad.repository;

import com.seguridad.model.Ingreso;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IngresoRepository extends JpaRepository<Ingreso, Integer> {
 List<Ingreso> findBySede_IdSede(int idSede);
 Page<Ingreso> findByEstado(int estado, Pageable pageable);
 
 Page<Ingreso> findByProductoDescripcionContainingIgnoreCaseOrProveedorNombreContainingIgnoreCaseOrSedeNombreContainingIgnoreCase(
	        String descripcion,
	        String proveedor,
	        String sede,
	        Pageable pageable);
 
 Page<Ingreso> findByEstadoAndProductoDescripcionContainingIgnoreCaseOrEstadoAndProveedorNombreContainingIgnoreCaseOrEstadoAndSedeNombreContainingIgnoreCase(
         int estado1, String descripcion,
         int estado2, String proveedor,
         int estado3, String sede,
         Pageable pageable);
 
@Query("SELECT MONTH(i.fecha) as mes, COUNT(i) as total " +
     "FROM Ingreso i " +
     "WHERE i.fecha >= :fechaInicio " +
     "GROUP BY MONTH(i.fecha) ORDER BY mes")
List<Object[]> contarPorMes(@Param("fechaInicio") Date fechaInicio);
}

