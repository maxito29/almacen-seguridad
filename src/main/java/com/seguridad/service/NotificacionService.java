package com.seguridad.service;

import com.seguridad.model.Notificacion;
import com.seguridad.model.Usuario;
import com.seguridad.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepo;

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
}