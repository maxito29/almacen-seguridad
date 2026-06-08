package com.seguridad.repository;

import com.seguridad.model.Salida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SalidaRepository extends JpaRepository<Salida, Integer> {
 List<Salida> findBySede_IdSede(int idSede);
}
