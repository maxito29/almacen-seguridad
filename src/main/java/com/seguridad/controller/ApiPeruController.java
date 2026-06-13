package com.seguridad.controller;

import com.seguridad.service.ApiPeruService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiPeruController {

    @Autowired
    ApiPeruService apiPeruService;
    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<Map<String, Object>> consultarRuc(
            @PathVariable String ruc) {

        if (ruc == null || !ruc.matches("\\d{11}")) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "El RUC debe tener 11 dígitos"));
        }

        Map<String, Object> resultado = apiPeruService.consultarRuc(ruc);
        return ResponseEntity.ok(resultado);
    }
    @GetMapping("/dni/{dni}")
    public ResponseEntity<Map<String, Object>> consultarDni(
            @PathVariable String dni) {
        if (dni == null || !dni.matches("\\d{8}")) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "El DNI debe tener 8 dígitos"));
        }

        Map<String, Object> resultado = apiPeruService.consultarDni(dni);
        return ResponseEntity.ok(resultado);
    }
}