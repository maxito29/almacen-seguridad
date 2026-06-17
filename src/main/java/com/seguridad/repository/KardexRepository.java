package com.seguridad.repository;

import com.seguridad.model.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KardexRepository extends JpaRepository<Kardex, Integer> {
 /*~~(class org.openrewrite.java.tree.J$Erroneous cannot be cast to class org.openrewrite.java.tree.J$Assignment (org.openrewrite.java.tree.J$Erroneous and org.openrewrite.java.tree.J$Assignment are in unnamed module of loader 'app'))~~>*/@Query("SELECT k FROM Kardex k WHERE k.producto.idProducto = :idProducto ORDER BY k.fecha asc")
 List<Kardex> findByProducto_IdProductoOrderByFechaAsc(String idProducto);

 @Query("SELECT COALESCE(MAX(k.saldoCant), 0) FROM Kardex k WHERE k.producto.idProducto = :idProducto AND k.sede.idSede = :idSede")
 Optional<Integer> findUltimoSaldo(String idProducto, int idSede);
 
 // para buscar x sede
 List<Kardex> findBySede_IdSedeOrderByFechaAsc(int idSede);
 
 // para buscar x producto y sede
 List<Kardex> findByProducto_IdProductoAndSede_IdSedeOrderByFechaAsc(String idProducto, int idSede);
 
 // paginacion
 Page<Kardex> findByProducto_IdProducto(String idProducto, Pageable pageable);
 Page<Kardex> findBySede_IdSede(int idSede, Pageable pageable);
 Page<Kardex> findByProducto_IdProductoAndSede_IdSede(String idProducto, int idSede, Pageable pageable);
 
// para buscar x referencia o producto
@Query("SELECT k FROM Kardex k WHERE " +
     "LOWER(k.referencia) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
     "LOWER(k.producto.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))")
Page<Kardex> buscarPorTexto(@Param("texto") String texto, Pageable pageable);

// para buscar con filtros
@Query("SELECT k FROM Kardex k WHERE " +
     "(:idProducto IS NULL OR k.producto.idProducto = :idProducto) AND " +
     "(:idSede IS NULL OR k.sede.idSede = :idSede) AND " +
     "(:texto IS NULL OR LOWER(k.referencia) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
     "LOWER(k.producto.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')))")
Page<Kardex> filtrarKardex(@Param("idProducto") String idProducto,
                         @Param("idSede") Integer idSede,
                         @Param("texto") String texto,
                         Pageable pageable);
 
 
 
 
}