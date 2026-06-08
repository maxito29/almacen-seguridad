package com.seguridad.repository;

import com.seguridad.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TrabajadorRepository extends JpaRepository<Trabajador, Integer> {
 List<Trabajador> findBySede_IdSede(int idSede);
 List<Trabajador> findByActivoCesado(String activoCesado);
}
