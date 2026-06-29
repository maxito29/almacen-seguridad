package com.seguridad.service;

import com.seguridad.model.Notificacion;
import com.seguridad.model.Usuario;
import com.seguridad.repository.NotificacionRepository;
import com.seguridad.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepo;
    @Autowired 
    private UsuarioRepository usuarioRepo;

    public void crearNotificacion(Usuario usuario, String titulo, String mensaje, String tipo) {
        Notificacion n = new Notificacion();
        n.setUsuario(usuario);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setTipo(tipo);
        notificacionRepo.save(n);
    }

    public List<Notificacion> obtenerNoLeidas(Integer idUsuario) {
        return notificacionRepo
            .findByUsuario_IdUsuarioAndLeidaFalseOrderByFechaCreacionDesc(idUsuario);
    }

    public List<Notificacion> obtenerTodas(Integer idUsuario) {
        return notificacionRepo
            .findByUsuario_IdUsuarioOrderByFechaCreacionDesc(idUsuario);
    }

    public long contarNoLeidas(Integer idUsuario) {
        return notificacionRepo.countByUsuario_IdUsuarioAndLeidaFalse(idUsuario);
    }

    public void marcarTodasLeidas(Integer idUsuario) {
        notificacionRepo.marcarTodasComoLeidas(idUsuario);
    }
    

 
    public void notificarAdmins(String titulo, String mensaje, String tipo) {
        List<Usuario> admins = usuarioRepo.findByRol("ADMIN");
        for (Usuario admin : admins) {
            crearNotificacion(admin, titulo, mensaje, tipo);
        }
    }

    public void notificarAlmacenDeSede(Integer idSede, String titulo, String mensaje, String tipo) {
        List<Usuario> almacenistas = usuarioRepo.findByRolAndSede_IdSede("ALMACEN", idSede);
        for (Usuario u : almacenistas) {
            crearNotificacion(u, titulo, mensaje, tipo);
        }
    }
    
    public void notificarTodos(String titulo, String mensaje, String tipo) {
        List<Usuario> todos = usuarioRepo.findAll().stream()
            .filter(u -> u.getEstado() != null && u.getEstado() == 1)
            .collect(java.util.stream.Collectors.toList());
        for (Usuario u : todos) {
            crearNotificacion(u, titulo, mensaje, tipo);
        }
    }
}