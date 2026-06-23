package com.seguridad.controller;

import com.seguridad.dto.MensajeDTO;
import com.seguridad.model.Mensaje;
import com.seguridad.model.Usuario;
import com.seguridad.repository.MensajeRepository;
import com.seguridad.repository.UsuarioRepository;
import com.seguridad.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class ChatWebSocketController {

    @Autowired MensajeRepository mensajeRepo;
    @Autowired UsuarioRepository usuarioRepo;
    @Autowired SimpMessagingTemplate messagingTemplate;
    @Autowired NotificacionService notificacionService;

    @MessageMapping("/chat.grupal")
    public void enviarGrupal(@Payload Map<String, String> payload, Principal principal) {
        Usuario emisor = usuarioRepo.findByUsername(principal.getName()).orElseThrow();

        Mensaje m = new Mensaje();
        m.setEmisor(emisor);
        m.setDestinatario(null);
        m.setContenido(payload.get("contenido"));
        mensajeRepo.save(m);

        messagingTemplate.convertAndSend("/topic/chat.grupal", toDTO(m));
    }

    @MessageMapping("/chat.directo")
    public void enviarDirecto(@Payload Map<String, String> payload, Principal principal) {
        Usuario emisor = usuarioRepo.findByUsername(principal.getName()).orElseThrow();
        Integer idDestinatario = Integer.parseInt(payload.get("idDestinatario"));
        Usuario destinatario = usuarioRepo.findById(idDestinatario).orElseThrow();

        Mensaje m = new Mensaje();
        m.setEmisor(emisor);
        m.setDestinatario(destinatario);
        m.setContenido(payload.get("contenido"));
        mensajeRepo.save(m);

        MensajeDTO dto = toDTO(m);
        messagingTemplate.convertAndSendToUser(destinatario.getUsername(), "/queue/chat.directo", dto);
        messagingTemplate.convertAndSendToUser(emisor.getUsername(), "/queue/chat.directo", dto);
        notificacionService.crearNotificacion(destinatario,
                "Nuevo mensaje de " + emisor.getNombre(),
                m.getContenido(), "MENSAJE");
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