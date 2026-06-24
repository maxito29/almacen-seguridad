package com.seguridad.controller;

import com.seguridad.model.Usuario;
import com.seguridad.security.CustomUserDetails;
import com.seguridad.service.ChatIAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatIAController {

    @Autowired ChatIAService chatIAService;

    @PostMapping("/preguntar")
    public Map<String, String> preguntar(@RequestBody Map<String, Object> payload,  
                                          Authentication auth) {
        String pregunta = (String) payload.get("pregunta");
        
        @SuppressWarnings("unchecked")
        List<Map<String, String>> historial = 
            (List<Map<String, String>>) payload.get("historial");
        
        Usuario usuario = ((CustomUserDetails) auth.getPrincipal()).getUsuario();
        String respuesta = chatIAService.preguntar(pregunta, usuario, historial);
        return Map.of("respuesta", respuesta);
    }
}