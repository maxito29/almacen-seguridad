package com.seguridad.controller;

import com.seguridad.service.ChatIAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatIAController {

    @Autowired ChatIAService chatIAService;

    @PostMapping("/preguntar")
    public ResponseEntity<Map<String, Object>> preguntar(
            @RequestBody Map<String, String> body) {

        String pregunta = body.get("pregunta");
        Map<String, Object> response = new HashMap<>();

        if (pregunta == null || pregunta.trim().isEmpty()) {
            response.put("error", "La pregunta no puede estar vacía");
            return ResponseEntity.badRequest().body(response);
        }

        String respuesta = chatIAService.preguntar(pregunta);
        response.put("respuesta", respuesta);
        return ResponseEntity.ok(response);
    }
}