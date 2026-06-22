package com.seguridad.service;

import com.seguridad.model.Usuario;
import com.seguridad.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired UsuarioRepository usuarioRepo;

    public Page<Usuario> listar(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("idUsuario").ascending());
        return usuarioRepo.findAll(pageable);
    }

    public void guardar(Usuario usuario) {
        boolean esNuevo = usuario.getIdUsuario() == null;

        if (esNuevo) {
            usuarioRepo.findByUsername(usuario.getUsername()).ifPresent(u -> {
                throw new RuntimeException("Ese nombre de usuario ya existe");
            });
            if (usuario.getEstado() == null) usuario.setEstado(1);
        } else {
            Usuario actual = usuarioRepo.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!actual.getUsername().equals(usuario.getUsername())) {
                usuarioRepo.findByUsername(usuario.getUsername()).ifPresent(u -> {
                    if (!u.getIdUsuario().equals(usuario.getIdUsuario())) {
                        throw new RuntimeException("Ese nombre de usuario ya existe");
                    }
                });
            }

            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword(actual.getPassword());
            }
            if (usuario.getEstado() == null) {
                usuario.setEstado(actual.getEstado());
            }
        }
        usuarioRepo.save(usuario);
    }

    public void cambiarEstado(Integer id) {
        Usuario u = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setEstado(u.getEstado() == 1 ? 2 : 1);
        usuarioRepo.save(u);
    }

    public Page<Usuario> buscar(String texto, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return usuarioRepo.findByUsernameContainingIgnoreCaseOrNombreContainingIgnoreCase(
                texto, texto, pageable);
    }

    public Page<Usuario> buscarConFiltro(String texto, int estado, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return usuarioRepo.findByEstadoAndUsernameContainingIgnoreCase(estado, texto, pageable);
    }

    public Page<Usuario> listarPorEstado(int estado, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return usuarioRepo.findByEstado(estado, pageable);
    }
}