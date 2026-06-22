package com.seguridad.repository;

import com.seguridad.model.Trabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Integer> {

    List<Trabajador> findBySede_IdSede(int idSede);
    List<Trabajador> findByActivoCesado(String activoCesado);

    @Query("SELECT t FROM Trabajador t WHERE " +
         "(:idSedeRestriccion IS NULL OR t.sede.idSede = :idSedeRestriccion) AND " +
         "(:activoCesado IS NULL OR t.activoCesado = :activoCesado) AND " +
         "(:texto IS NULL OR " +
         "   LOWER(t.nombreCompleto) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
         "   LOWER(t.documentoIdentidad) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Trabajador> filtrarTrabajadores(@Param("idSedeRestriccion") Integer idSedeRestriccion,
                                          @Param("activoCesado") String activoCesado,
                                          @Param("texto") String texto,
                                          Pageable pageable);
    
    long countBySede_IdSede(int idSede);
}