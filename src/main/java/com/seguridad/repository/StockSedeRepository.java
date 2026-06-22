package com.seguridad.repository;

import com.seguridad.model.StockSede;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StockSedeRepository extends JpaRepository<StockSede, Integer> {

	Optional<StockSede> findByProducto_IdProductoAndSede_IdSede(String idProducto, Integer idSede);

	List<StockSede> findBySede_IdSede(Integer idSede);

	@Query("SELECT ss FROM StockSede ss WHERE ss.sede.idSede = :idSede " + "ORDER BY ss.cantidad ASC")
	List<StockSede> listarPorSedeOrdenado(@Param("idSede") Integer idSede);

	@Query("SELECT ss FROM StockSede ss WHERE ss.sede.idSede = :idSede " + "ORDER BY ss.cantidad ASC")
	Page<StockSede> listarPorSedeOrdenadoPaginado(@Param("idSede") Integer idSede, Pageable pageable);

	@Query("SELECT ss.producto.descripcion, ss.cantidad FROM StockSede ss "
			+ "WHERE ss.sede.idSede = :idSede ORDER BY ss.cantidad DESC")
	List<Object[]> top5PorSede(@Param("idSede") Integer idSede, Pageable pageable);

	@Query("SELECT ss FROM StockSede ss WHERE ss.sede.idSede = :idSede AND ss.cantidad <= :umbral ORDER BY ss.cantidad ASC")
	List<StockSede> findStockBajoPorSede(@Param("idSede") Integer idSede, @Param("umbral") int umbral);

	@Query("SELECT ss FROM StockSede ss WHERE ss.cantidad <= :umbral ORDER BY ss.sede.nombre ASC, ss.cantidad ASC")
	List<StockSede> findStockBajoGlobal(@Param("umbral") int umbral);
}