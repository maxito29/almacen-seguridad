package com.seguridad.repository;

import com.seguridad.model.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
public interface KardexRepository extends JpaRepository<Kardex, Integer> {
 List<Kardex> findByProducto_IdProductoOrderByFechaAsc(String idProducto);

 @Query("SELECT COALESCE(MAX(k.saldoCant), 0) FROM Kardex k WHERE k.producto.idProducto = :idProducto AND k.sede.idSede = :idSede")
 Optional<Integer> findUltimoSaldo(String idProducto, int idSede);
}