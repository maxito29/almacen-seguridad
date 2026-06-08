package com.seguridad.repository;

import com.seguridad.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductoRepository extends JpaRepository<Producto, String> {
 List<Producto> findByEstado(int estado);
 List<Producto> findByTipo_IdTipo(int idTipo);
}
