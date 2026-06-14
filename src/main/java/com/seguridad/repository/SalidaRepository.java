package com.seguridad.repository;

import com.seguridad.model.Salida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
public interface SalidaRepository extends JpaRepository<Salida, Integer> {
 List<Salida> findBySede_IdSede(int idSede);
 
 @Query("SELECT MONTH(s.fecha) as mes, COUNT(s) as total " +
	       "FROM Salida s " +
	       "WHERE s.fecha >= :fechaInicio " +
	       "GROUP BY MONTH(s.fecha) ORDER BY mes")
	List<Object[]> contarPorMes(@Param("fechaInicio") Date fechaInicio);
}
