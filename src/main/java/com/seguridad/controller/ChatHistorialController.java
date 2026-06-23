package com.seguridad.controller;

import com.seguridad.dto.MensajeDTO;
import com.seguridad.model.Mensaje;
import com.seguridad.model.Usuario;
import com.seguridad.repository.MensajeRepository;
import com.seguridad.repository.UsuarioRepository;
import com.seguridad.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatHistorialController {

    @Autowired MensajeRepository mensajeRepo;
    @Autowired UsuarioRepository usuarioRepo;

    @GetMapping("/grupal")
    public List<MensajeDTO> historialGrupal() {
        List<Mensaje> mensajes = mensajeRepo.findUltimosGrupales(PageRequest.of(0, 50));
        Collections.reverse(mensajes);
        return mensajes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/directo/{idUsuario}")
    @Transactional
    public List<MensajeDTO> historialDirecto(@PathVariable Integer idUsuario, Authentication auth) {
        Usuario yo = ((CustomUserDetails) auth.getPrincipal()).getUsuario();
        List<Mensaje> mensajes = mensajeRepo.findConversacion(yo.getIdUsuario(), idUsuario);
        mensajeRepo.marcarConversacionLeida(yo.getIdUsuario(), idUsuario);
        return mensajes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/contactos")
    public List<Map<String, Object>> listarContactos(Authentication auth) {
        Usuario yo = ((CustomUserDetails) auth.getPrincipal()).getUsuario();

        return usuarioRepo.findAll().stream()
                .filter(u -> !u.getIdUsuario().equals(yo.getIdUsuario()))
                .filter(u -> u.getEstado() != null && u.getEstado() == 1)
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idUsuario", u.getIdUsuario());
                    map.put("nombre", u.getNombre());
                    map.put("rol", u.getRol());
                    map.put("sede", u.getSede() != null ? u.getSede().getNombre() : null);
                    map.put("noLeidos", mensajeRepo.contarNoLeidosDe(yo.getIdUsuario(), u.getIdUsuario()));
                    return map;
                })
                .collect(Collectors.toList());
    }

    private MensajeDTO toDTO(Mensaje m) {
        MensajeDTO dto = new MensajeDTO();
        dto.setIdMensaje(m.getIdMensaje());
        dto.setIdEmisor(m.getEmisor().getIdUsuario());
        dto.setNombreEmisor(m.getEmisor().getNombre());
        dto.setIdDestinatario(m.getDestinatario() != null ? m.getDestinatario().getIdUsuario() : null);
        dto.setContenido(m.getContenido());
        dto.setFecha(m.getFecha());
        return dto;
    }
}