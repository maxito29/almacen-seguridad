package com.seguridad.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class ApiPeruService {

    @Value("${apisperu.token}")
    private String token;

    @Value("${apisperu.url.ruc}")
    private String urlRuc;

    @Value("${apisperu.url.dni}")
    private String urlDni;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> consultarRuc(String ruc) {
        String url = urlRuc + ruc + "?token=" + token;
        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            return Map.of("error", "No se pudo consultar el RUC: " + e.getMessage());
        }
    }

    public Map<String, Object> consultarDni(String dni) {
        String url = urlDni + dni + "?token=" + token;
        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            return Map.of("error", "No se pudo consultar el DNI: " + e.getMessage());
        }
    }
}