package com.seguridad.controller;

import com.seguridad.model.Notificacion;
import com.seguridad.security.CustomUserDetails;
import com.seguridad.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    private Integer getIdUsuario(Authentication auth) {
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
        return ud.getUsuario().getIdUsuario(); 
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> count(Authentication auth) {
        long total = notificacionService.contarNoLeidas(getIdUsuario(auth));
        return ResponseEntity.ok(Map.of("noLeidas", total));
    }

    @GetMapping("/lista")
    public ResponseEntity<List<Map<String, Object>>> lista(Authentication auth) {
        List<Notificacion> notifs = notificacionService.obtenerTodas(getIdUsuario(auth));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Notificacion n : notifs) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id",      n.getId());
            item.put("titulo",  n.getTitulo());
            item.put("mensaje", n.getMensaje());
            item.put("leida",   n.isLeida());
            item.put("fecha",   n.getFechaCreacion().format(fmt));
            item.put("tipo",    n.getTipo());
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/marcar-leidas")
    public ResponseEntity<Void> marcarLeidas(Authentication auth) {
        notificacionService.marcarTodasLeidas(getIdUsuario(auth));
        return ResponseEntity.ok().build();
    }
}