package com.seguridad.repository;
import com.seguridad.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    Page<Usuario> findByEstado(int estado, Pageable pageable);
    Page<Usuario> findByUsernameContainingIgnoreCaseOrNombreContainingIgnoreCase(
            String username, String nombre, Pageable pageable);
    Page<Usuario> findByEstadoAndUsernameContainingIgnoreCase(
            int estado, String username, Pageable pageable);
    
    List<Usuario> findByRolAndEstado(String rol, Integer estado);
    List<Usuario> findByRolAndSede_IdSedeAndEstado(String rol, Integer idSede, Integer estado);
}