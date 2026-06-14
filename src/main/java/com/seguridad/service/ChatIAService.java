package com.seguridad.service;

import com.seguridad.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatIAService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Autowired ProductoRepository   productoRepo;
    @Autowired IngresoRepository    ingresoRepo;
    @Autowired SalidaRepository     salidaRepo;
    @Autowired TrabajadorRepository trabajadorRepo;
    @Autowired SedeRepository       sedeRepo;
    @Autowired ProveedorRepository proveedorRepo;

    private final RestTemplate restTemplate = new RestTemplate();

    public String preguntar(String pregunta) {

        String contexto = construirContexto();

        String systemPrompt =
            "Eres un asistente experto del Sistema de Gestión de " +
            "Almacén de una empresa de seguridad privada en Perú. " +
            "Responde de forma breve, clara y profesional en español. " +
            "Usa los datos reales del sistema para responder. " +
            "No inventes datos que no estén en el contexto.\n\n" +
            "=== DATOS ACTUALES DEL SISTEMA ===\n" + contexto;
        
        Map<String, Object> mensajeSistema = new HashMap<>();
        mensajeSistema.put("role", "system");
        mensajeSistema.put("content", systemPrompt);

        Map<String, Object> mensajeUsuario = new HashMap<>();
        mensajeUsuario.put("role", "user");
        mensajeUsuario.put("content", pregunta);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.3-70b-versatile"); 
        body.put("messages", List.of(mensajeSistema, mensajeUsuario));
        body.put("max_tokens", 1024);
        body.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); 

        HttpEntity<Map<String, Object>> request =
            new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                apiUrl, request, Map.class);

            return extraerTexto(response);

        } catch (Exception e) {
            return "Error al conectar con el asistente: " + e.getMessage();
        }
    }

    @SuppressWarnings("unchecked")
    private String extraerTexto(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices =
                (List<Map<String, Object>>) response.get("choices");

            Map<String, Object> message =
                (Map<String, Object>) choices.get(0).get("message");

            return (String) message.get("content");

        } catch (Exception e) {
            return "No se pudo procesar la respuesta.";
        }
    }

    private String construirContexto() {
        StringBuilder ctx = new StringBuilder();

        ctx.append("RESUMEN GENERAL:\n");
        ctx.append("- Total productos: ").append(productoRepo.count()).append("\n");
        ctx.append("- Total ingresos: ").append(ingresoRepo.count()).append("\n");
        ctx.append("- Total salidas: ").append(salidaRepo.count()).append("\n");
        ctx.append("- Total trabajadores: ").append(trabajadorRepo.count()).append("\n\n");
        
        ctx.append("INGRESOS RECIENTES (últimos 20):\n");
        ingresoRepo.findAll().stream()
            .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
            .limit(20)
            .forEach(i -> ctx.append("- ID: ").append(i.getIdIngreso())
                .append(" | Fecha: ").append(i.getFecha())
                .append(" | Producto: ").append(i.getProducto().getDescripcion())
                .append(" | Cantidad: ").append(i.getCantidad())
                .append(" | Total: S/ ").append(i.getTotal())
                .append(" | Sede: ").append(i.getSede().getNombre())
                .append("\n"));
        ctx.append("\n");

        ctx.append("INGRESOS POR MES (resumen):\n");
        ingresoRepo.findAll().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                i -> {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(i.getFecha());
                    return cal.get(java.util.Calendar.YEAR) + "-" +
                           String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1);
                },
                java.util.stream.Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": ").append(e.getValue()).append(" ingresos\n"));
        ctx.append("\n");
        
        ctx.append("SALIDAS POR MES (resumen):\n");
        salidaRepo.findAll().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                s -> {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(s.getFecha());
                    return cal.get(java.util.Calendar.YEAR) + "-" +
                           String.format("%02d", cal.get(java.util.Calendar.MONTH) + 1);
                },
                java.util.stream.Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": ").append(e.getValue()).append(" salidas\n"));
        ctx.append("\n");
        
        ctx.append("INGRESOS POR PROVEEDOR:\n");
        ingresoRepo.findAll().stream()
            .filter(i -> i.getProveedor() != null)
            .collect(java.util.stream.Collectors.groupingBy(
                i -> i.getProveedor().getNombre(),
                java.util.stream.Collectors.counting()
            ))
            .entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": ").append(e.getValue()).append(" ingresos\n"));
        ctx.append("\n");
        
        ctx.append("SALIDAS RECIENTES (últimas 20):\n");
        salidaRepo.findAll().stream()
            .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
            .limit(20)
            .forEach(s -> ctx.append("- ID: ").append(s.getIdSalida())
                .append(" | Fecha: ").append(s.getFecha())
                .append(" | Producto: ").append(s.getProducto().getDescripcion())
                .append(" | Cantidad: ").append(s.getCantidad())
                .append(" | Sede: ").append(s.getSede().getNombre())
                .append(" | Trabajador: ").append(
                    s.getTrabajador() != null ? s.getTrabajador().getNombreCompleto() : "-")
                .append("\n"));
        ctx.append("\n");
        
        ctx.append("PRODUCTOS MÁS SOLICITADOS (por salidas):\n");
        salidaRepo.findAll().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                s -> s.getProducto().getDescripcion(),
                java.util.stream.Collectors.summingInt(s -> s.getCantidad())
            ))
            .entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(10)
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": ").append(e.getValue()).append(" unidades entregadas\n"));
        ctx.append("\n");
        
        ctx.append("PROVEEDORES REGISTRADOS:\n");
        ctx.append("- Total proveedores activos: ")
           .append(proveedorRepo.findAll().stream()
               .filter(p -> p.getEstado() == 1).count()).append("\n");
        proveedorRepo.findAll().stream()
            .filter(p -> p.getEstado() == 1)
            .forEach(p -> ctx.append("- ").append(p.getNombre())
                .append(" | RUC: ").append(p.getRuc() != null ? p.getRuc() : "-")
                .append(" | Tipo: ").append(p.getTipo() != null ? p.getTipo() : "-")
                .append(" | SUNAT: ").append(p.getEstadoSunat() != null
                    ? p.getEstadoSunat() : "-")
                .append("\n"));
        ctx.append("\n");

        ctx.append("TIPOS DE PRODUCTOS Y CANTIDAD:\n");
        productoRepo.findAll().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                p -> p.getTipo() != null ? p.getTipo().getNombre() : "Sin tipo",
                java.util.stream.Collectors.counting()
            ))
            .entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": ").append(e.getValue()).append(" productos\n"));
        ctx.append("\n");

        ctx.append("STOCK TOTAL POR TIPO DE PRODUCTO:\n");
        productoRepo.findAll().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                p -> p.getTipo() != null ? p.getTipo().getNombre() : "Sin tipo",
                java.util.stream.Collectors.summingInt(
                    p -> p.getStockTotal() != null ? p.getStockTotal() : 0)
            ))
            .entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": ").append(e.getValue()).append(" unidades en total\n"));
        ctx.append("\n");

        ctx.append("TRABAJADORES CON MÁS SALIDAS:\n");
        salidaRepo.findAll().stream()
            .filter(s -> s.getTrabajador() != null)
            .collect(java.util.stream.Collectors.groupingBy(
                s -> s.getTrabajador().getNombreCompleto(),
                java.util.stream.Collectors.counting()
            ))
            .entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(10)
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": ").append(e.getValue()).append(" salidas\n"));
        ctx.append("\n");

        ctx.append("VALOR DEL INVENTARIO POR TIPO:\n");
        productoRepo.findAll().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                p -> p.getTipo() != null ? p.getTipo().getNombre() : "Sin tipo",
                java.util.stream.Collectors.summingDouble(p -> {
                    double costo = p.getCostoUnitario() != null
                        ? p.getCostoUnitario().doubleValue() : 0;
                    int stock = p.getStockTotal() != null ? p.getStockTotal() : 0;
                    return costo * stock;
                })
            ))
            .entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": S/ ").append(String.format("%.2f", e.getValue()))
                .append("\n"));
        ctx.append("\n");
        
        ctx.append("INGRESOS POR SEDE:\n");
        ingresoRepo.findAll().stream()
            .collect(java.util.stream.Collectors.groupingBy(
                i -> i.getSede().getNombre(),
                java.util.stream.Collectors.counting()
            ))
            .entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": ").append(e.getValue()).append(" ingresos\n"));
        ctx.append("\n");
        
        ctx.append("VALOR TOTAL DEL INVENTARIO:\n");
        double valorTotal = productoRepo.findAll().stream()
            .mapToDouble(p -> {
                double costo = p.getCostoUnitario() != null
                    ? p.getCostoUnitario().doubleValue() : 0;
                int stock = p.getStockTotal() != null ? p.getStockTotal() : 0;
                return costo * stock;
            }).sum();
        ctx.append("- Valor total en almacén: S/ ")
           .append(String.format("%.2f", valorTotal)).append("\n\n");
        
        ctx.append("SEDES:\n");
        sedeRepo.findAll().forEach(s ->
            ctx.append("- ").append(s.getNombre())
               .append(" (").append(s.getDireccion()).append(")\n"));
        ctx.append("\n");

        ctx.append("PRODUCTOS CON STOCK BAJO O CERO:\n");
        productoRepo.findAll().stream()
            .filter(p -> p.getStockTotal() != null && p.getStockTotal() <= 3)
            .forEach(p -> ctx.append("- ")
                .append(p.getDescripcion())
                .append(" | Stock: ").append(p.getStockTotal()).append("\n"));
        ctx.append("\n");

        ctx.append("PRODUCTOS CON MAYOR STOCK:\n");
        productoRepo.findAll().stream()
            .sorted((a, b) -> {
                int sa = a.getStockTotal() != null ? a.getStockTotal() : 0;
                int sb = b.getStockTotal() != null ? b.getStockTotal() : 0;
                return Integer.compare(sb, sa);
            })
            .limit(5)
            .forEach(p -> ctx.append("- ")
                .append(p.getDescripcion())
                .append(": ").append(p.getStockTotal()).append(" unidades\n"));
        ctx.append("\n");

        ctx.append("TODOS LOS PRODUCTOS:\n");
        productoRepo.findAll().forEach(p ->
            ctx.append("- ").append(p.getIdProducto())
               .append(" | ").append(p.getDescripcion())
               .append(" | ").append(p.getTipo() != null
                   ? p.getTipo().getNombre() : "-")
               .append(" | Stock: ").append(p.getStockTotal())
               .append(" | Costo: S/ ").append(p.getCostoUnitario())
               .append("\n"));

        return ctx.toString();
    }
}