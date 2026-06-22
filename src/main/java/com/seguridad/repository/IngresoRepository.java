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

    Page<Ingreso> findByEstadoAndSede_IdSede(int estado, Integer idSede, Pageable pageable);

    Page<Ingreso> findBySede_IdSede(Integer idSede, Pageable pageable);

    @Query("SELECT MONTH(i.fecha) as mes, COUNT(i) as total " +
         "FROM Ingreso i " +
         "WHERE i.fecha >= :fechaInicio " +
         "GROUP BY MONTH(i.fecha) ORDER BY mes")
    List<Object[]> contarPorMes(@Param("fechaInicio") Date fechaInicio);


    @Query("SELECT i FROM Ingreso i WHERE " +
         "(:idSedeRestriccion IS NULL OR i.sede.idSede = :idSedeRestriccion) AND " +
         "(:estado IS NULL OR i.estado = :estado) AND " +
         "(:texto IS NULL OR " +
         "   LOWER(i.producto.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
         "   (i.proveedor IS NOT NULL AND LOWER(i.proveedor.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))) OR " +
         "   LOWER(i.sede.nombre) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Ingreso> filtrarIngresos(@Param("idSedeRestriccion") Integer idSedeRestriccion,
                                   @Param("estado") Integer estado,
                                   @Param("texto") String texto,
                                   Pageable pageable);
    

 long countBySede_IdSede(Integer idSede);

 @Query("SELECT MONTH(i.fecha) as mes, COUNT(i) as total " +
      "FROM Ingreso i " +
      "WHERE i.fecha >= :fechaInicio AND i.sede.idSede = :idSede " +
      "GROUP BY MONTH(i.fecha) ORDER BY mes")
 List<Object[]> contarPorMesYSede(@Param("fechaInicio") Date fechaInicio,
                                   @Param("idSede") Integer idSede);
}