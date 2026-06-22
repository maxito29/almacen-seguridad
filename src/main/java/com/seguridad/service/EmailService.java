package com.seguridad.service;

import com.seguridad.model.StockSede;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Autowired private ITemplateEngine templateEngine;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${brevo.api.key}")
    private String apiKey;
    @Value("${brevo.api.url}")
    private String apiUrl;
    @Value("${app.mail.remitente}")
    private String remitente;

    @Async
    public void enviarBienvenida(String destino, String username, String nombre, String passwordTemporal) {
        try {
            Context context = new Context();
            context.setVariable("nombre", nombre);
            context.setVariable("username", username);
            context.setVariable("password", passwordTemporal);
            String html = templateEngine.process("emails/bienvenida", context);
            enviarCorreo(destino, nombre, "Bienvenido al Sistema Almacén", html);
        } catch (Exception e) {
            System.err.println("Error enviando correo de bienvenida: " + e.getMessage());
        }
    }

    @Async
    public void enviarAlertaStockBajo(String destino, String nombre, List<StockSede> items) {
        try {
            Context context = new Context();
            context.setVariable("nombre", nombre);
            context.setVariable("items", items);
            String html = templateEngine.process("emails/stock-bajo", context);
            enviarCorreo(destino, nombre, "⚠ Alerta de Stock Bajo - Sistema Almacén", html);
        } catch (Exception e) {
            System.err.println("Error enviando alerta de stock bajo: " + e.getMessage());
        }
    }

    private void enviarCorreo(String destino, String nombreDestino, String asunto, String html) {
    	
        System.out.println("=== ENVIANDO CORREO ===");
        System.out.println("Para: " + destino);
        System.out.println("Asunto: " + asunto);
        System.out.println("API Key (primeros 10): " + apiKey.substring(0, 10));
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, Object> sender = new HashMap<>();
        sender.put("name", "Sistema Almacén");
        sender.put("email", remitente);

        Map<String, Object> to = new HashMap<>();
        to.put("email", destino);
        to.put("name", nombreDestino);

        Map<String, Object> body = new HashMap<>();
        body.put("sender", sender);
        body.put("to", List.of(to));
        body.put("subject", asunto);
        body.put("htmlContent", html);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(apiUrl, request, String.class);
        
        try {
            ResponseEntity<String> respuesta = restTemplate.postForEntity(apiUrl, request, String.class);
            System.out.println("✅ Brevo respondió: " + respuesta.getStatusCode());
        } catch (Exception e) {
            System.err.println("❌ Error Brevo: " + e.getMessage()); 
        }
    }
}