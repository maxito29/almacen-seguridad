package com.seguridad.repository;

import com.seguridad.model.Salida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

public interface SalidaRepository extends JpaRepository<Salida, Integer> {

    List<Salida> findBySede_IdSede(int idSede);
    Page<Salida> findBySede_IdSede(Integer idSede, Pageable pageable);

    Page<Salida> findByEstado(int estado, Pageable pageable);
    Page<Salida> findByEstadoAndSede_IdSede(int estado, Integer idSede, Pageable pageable);

    @Query("SELECT s FROM Salida s WHERE " +
    	       "(:idSedeRestriccion IS NULL OR s.sede.idSede = :idSedeRestriccion) AND " +
    	       "(:estado IS NULL OR s.estado = :estado) AND " +
    	       "(:texto IS NULL OR (" +
    	       "   LOWER(s.producto.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
    	       "   LOWER(s.trabajador.nombreCompleto) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
    	       "   LOWER(s.sede.nombre) LIKE LOWER(CONCAT('%', :texto, '%')))" +
    	       ")")
    	Page<Salida> filtrarSalidas(@Param("idSedeRestriccion") Integer idSedeRestriccion,
    	                            @Param("estado") Integer estado,
    	                            @Param("texto") String texto,
    	                            Pageable pageable);
    long countBySede_IdSede(Integer idSede);

    @Query("SELECT MONTH(s.fecha) as mes, COUNT(s) as total " +
           "FROM Salida s " +
           "WHERE s.fecha >= :fechaInicio " +
           "GROUP BY MONTH(s.fecha) ORDER BY mes")
    List<Object[]> contarPorMes(@Param("fechaInicio") Date fechaInicio);

    @Query("SELECT MONTH(s.fecha) as mes, COUNT(s) as total " +
           "FROM Salida s " +
           "WHERE s.fecha >= :fechaInicio AND s.sede.idSede = :idSede " +
           "GROUP BY MONTH(s.fecha) ORDER BY mes")
    List<Object[]> contarPorMesYSede(@Param("fechaInicio") Date fechaInicio,
                                     @Param("idSede") Integer idSede);
}