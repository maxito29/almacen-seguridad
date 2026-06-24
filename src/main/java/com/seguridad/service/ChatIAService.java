package com.seguridad.service;

import com.seguridad.model.Usuario;
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
    @Autowired StockSedeRepository stockSedeRepo;

    private final RestTemplate restTemplate = new RestTemplate();

    public String preguntar(String pregunta, Usuario usuario, List<Map<String, String>> historial) {

        String contexto = construirContexto(usuario);

        String systemPrompt =
            "Eres un asistente experto del Sistema de Gestión de " +
            "Almacén de una empresa de seguridad privada en Perú. " +
            "Responde de forma breve, clara y profesional en español. " +
            "Usa los datos reales del sistema para responder. " +
            "No inventes datos que no estén en el contexto. " +
            "Si te preguntan por stock, muestra los números exactos del contexto. " +
            "Si te preguntan por recomendaciones, basa tus sugerencias en los datos reales. " +
            "Si el usuario es ALMACEN, NUNCA menciones datos de otras sedes. " +
            "Cuando muestres listas, usa formato con viñetas (•) para mejor lectura. " +
            "Si no tienes el dato exacto, dilo claramente en vez de inventar. " +
            "Puedes hacer cálculos simples con los datos del contexto (sumas, promedios). " +
            "\n\n=== DATOS ACTUALES DEL SISTEMA ===\n" + contexto;

        // Construir lista de mensajes
        List<Map<String, Object>> mensajes = new ArrayList<>();

        // 1. Sistema
        Map<String, Object> sistema = new HashMap<>();
        sistema.put("role", "system");
        sistema.put("content", systemPrompt);
        mensajes.add(sistema);

        // 2. Historial previo
        if (historial != null) {
            for (Map<String, String> h : historial) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("role", h.get("role"));
                msg.put("content", h.get("content"));
                mensajes.add(msg);
            }
        }

        // 3. Pregunta actual
        Map<String, Object> msgUsuario = new HashMap<>();
        msgUsuario.put("role", "user");
        msgUsuario.put("content", pregunta);
        mensajes.add(msgUsuario);

        // Request a Groq
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");
        requestBody.put("messages", mensajes);
        requestBody.put("max_tokens", 1024);
        requestBody.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

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

    private String construirContexto(Usuario usuario) {
        boolean esAlmacen = "ALMACEN".equals(usuario.getRol());
        Integer idSede = esAlmacen && usuario.getSede() != null
                         ? usuario.getSede().getIdSede() : null;
        String nombreSede = esAlmacen && usuario.getSede() != null
                            ? usuario.getSede().getNombre() : "todas las sedes";

        StringBuilder ctx = new StringBuilder();

        if (esAlmacen) {
            ctx.append("IMPORTANTE: Este usuario es ALMACEN de la sede: ")
               .append(nombreSede)
               .append(". Responde SIEMPRE solo con datos de esa sede.\n\n");
        }

        // ── RESUMEN GENERAL ──────────────────────────────────────
        ctx.append("RESUMEN GENERAL").append(esAlmacen ? " - SEDE: " + nombreSede : "").append(":\n");
        ctx.append("- Total productos en catálogo: ").append(productoRepo.count()).append("\n");

        if (esAlmacen) {
            ctx.append("- Total ingresos en tu sede: ")
               .append(ingresoRepo.countBySede_IdSede(idSede)).append("\n");
            ctx.append("- Total salidas en tu sede: ")
               .append(salidaRepo.countBySede_IdSede(idSede)).append("\n");
            ctx.append("- Total trabajadores en tu sede: ")
               .append(trabajadorRepo.countBySede_IdSede(idSede)).append("\n\n");
        } else {
            ctx.append("- Total ingresos registrados: ").append(ingresoRepo.count()).append("\n");
            ctx.append("- Total salidas registradas: ").append(salidaRepo.count()).append("\n");
            ctx.append("- Total trabajadores: ").append(trabajadorRepo.count()).append("\n\n");
        }

        // ── INGRESOS POR MES (últimos 6 meses) ───────────────────
        ctx.append("INGRESOS POR MES - ÚLTIMOS 6 MESES").append(esAlmacen ? " (SEDE: " + nombreSede + ")" : "").append(":\n");
        java.util.Calendar cal6 = java.util.Calendar.getInstance();
        cal6.add(java.util.Calendar.MONTH, -6);
        java.util.Date hace6Meses = cal6.getTime();

        ingresoRepo.findAll().stream()
            .filter(i -> idSede == null || i.getSede().getIdSede().equals(idSede))
            .filter(i -> i.getFecha() != null && i.getFecha().after(hace6Meses))
            .collect(java.util.stream.Collectors.groupingBy(
                i -> {
                    java.util.Calendar c = java.util.Calendar.getInstance();
                    c.setTime(i.getFecha());
                    return c.get(java.util.Calendar.YEAR) + "-" +
                           String.format("%02d", c.get(java.util.Calendar.MONTH) + 1);
                },
                java.util.stream.Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": ").append(e.getValue()).append(" ingresos\n"));
        ctx.append("\n");

        // ── SALIDAS POR MES (últimos 6 meses) ────────────────────
        ctx.append("SALIDAS POR MES - ÚLTIMOS 6 MESES").append(esAlmacen ? " (SEDE: " + nombreSede + ")" : "").append(":\n");
        salidaRepo.findAll().stream()
            .filter(s -> idSede == null || s.getSede().getIdSede().equals(idSede))
            .filter(s -> s.getFecha() != null && s.getFecha().after(hace6Meses))
            .collect(java.util.stream.Collectors.groupingBy(
                s -> {
                    java.util.Calendar c = java.util.Calendar.getInstance();
                    c.setTime(s.getFecha());
                    return c.get(java.util.Calendar.YEAR) + "-" +
                           String.format("%02d", c.get(java.util.Calendar.MONTH) + 1);
                },
                java.util.stream.Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> ctx.append("- ").append(e.getKey())
                .append(": ").append(e.getValue()).append(" salidas\n"));
        ctx.append("\n");

        // ── INGRESOS RECIENTES (últimos 20) ──────────────────────
        ctx.append("INGRESOS RECIENTES (últimos 20):\n");
        ingresoRepo.findAll().stream()
            .filter(i -> idSede == null || i.getSede().getIdSede().equals(idSede))
            .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
            .limit(20)
            .forEach(i -> ctx.append("- ")
                .append(i.getProducto().getDescripcion())
                .append(" | Cant: ").append(i.getCantidad())
                .append(" | Total: S/ ").append(i.getTotal())
                .append(" | Sede: ").append(i.getSede().getNombre())
                .append(" | Fecha: ").append(i.getFecha()).append("\n"));
        ctx.append("\n");

        // ── SALIDAS RECIENTES (últimas 20) ───────────────────────
        ctx.append("SALIDAS RECIENTES (últimas 20):\n");
        salidaRepo.findAll().stream()
            .filter(s -> idSede == null || s.getSede().getIdSede().equals(idSede))
            .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
            .limit(20)
            .forEach(s -> ctx.append("- ")
                .append(s.getProducto().getDescripcion())
                .append(" | Cant: ").append(s.getCantidad())
                .append(" | Sede: ").append(s.getSede().getNombre())
                .append(" | Trabajador: ").append(
                    s.getTrabajador() != null ? s.getTrabajador().getNombreCompleto() : "-")
                .append(" | Fecha: ").append(s.getFecha()).append("\n"));
        ctx.append("\n");

        // ── PRODUCTOS MÁS SOLICITADOS ────────────────────────────
        ctx.append("PRODUCTOS MÁS SOLICITADOS (por cantidad de salidas)").append(esAlmacen ? " EN TU SEDE" : "").append(":\n");
        salidaRepo.findAll().stream()
            .filter(s -> idSede == null || s.getSede().getIdSede().equals(idSede))
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

        // ── STOCK ────────────────────────────────────────────────
        if (esAlmacen && idSede != null) {
            ctx.append("STOCK EN TU SEDE (").append(nombreSede).append("):\n");
            stockSedeRepo.findBySede_IdSede(idSede).stream()
                .sorted((a, b) -> Integer.compare(a.getCantidad(), b.getCantidad()))
                .forEach(ss -> ctx.append("- ").append(ss.getProducto().getDescripcion())
                   .append(": ").append(ss.getCantidad()).append(" unidades\n"));
            ctx.append("\n");

            ctx.append("PRODUCTOS CON STOCK BAJO EN TU SEDE (≤10):\n");
            stockSedeRepo.findBySede_IdSede(idSede).stream()
                .filter(ss -> ss.getCantidad() <= 10)
                .forEach(ss -> ctx.append("- ").append(ss.getProducto().getDescripcion())
                   .append(": ").append(ss.getCantidad()).append(" unidades\n"));
            ctx.append("\n");
        } else {
            ctx.append("STOCK GLOBAL POR PRODUCTO:\n");
            productoRepo.findAll().stream()
                .sorted((a, b) -> Integer.compare(
                    a.getStockTotal() != null ? a.getStockTotal() : 0,
                    b.getStockTotal() != null ? b.getStockTotal() : 0))
                .forEach(p -> ctx.append("- ").append(p.getDescripcion())
                   .append(" | Stock: ").append(p.getStockTotal())
                   .append(" | Tipo: ").append(p.getTipo() != null ? p.getTipo().getNombre() : "-")
                   .append(" | Costo: S/ ").append(p.getCostoUnitario()).append("\n"));
            ctx.append("\n");

            ctx.append("STOCK POR SEDE (detalle):\n");
            sedeRepo.findAll().forEach(s -> {
                ctx.append("• Sede ").append(s.getNombre()).append(":\n");
                stockSedeRepo.findBySede_IdSede(s.getIdSede()).forEach(ss ->
                    ctx.append("  - ").append(ss.getProducto().getDescripcion())
                       .append(": ").append(ss.getCantidad()).append(" unidades\n"));
            });
            ctx.append("\n");

            ctx.append("PRODUCTOS CON STOCK BAJO (global ≤10):\n");
            productoRepo.findAll().stream()
                .filter(p -> p.getStockTotal() != null && p.getStockTotal() <= 10)
                .forEach(p -> ctx.append("- ").append(p.getDescripcion())
                   .append(": ").append(p.getStockTotal()).append("\n"));
            ctx.append("\n");
        }

        // ── TRABAJADORES ─────────────────────────────────────────
        ctx.append("TRABAJADORES").append(esAlmacen ? " EN TU SEDE" : "").append(":\n");
        trabajadorRepo.findAll().stream()
            .filter(t -> idSede == null ||
                    (t.getSede() != null && t.getSede().getIdSede().equals(idSede)))
            .forEach(t -> ctx.append("- ").append(t.getNombreCompleto())
                .append(" | Puesto: ").append(t.getPuesto() != null ? t.getPuesto() : "-")
                .append(" | Sede: ").append(t.getSede() != null ? t.getSede().getNombre() : "-")
                .append(" | Estado: ").append(t.getActivoCesado() != null ? t.getActivoCesado() : "-")
                .append("\n"));
        ctx.append("\n");

        ctx.append("TRABAJADORES CON MÁS SALIDAS").append(esAlmacen ? " EN TU SEDE" : "").append(":\n");
        salidaRepo.findAll().stream()
            .filter(s -> s.getTrabajador() != null)
            .filter(s -> idSede == null || s.getSede().getIdSede().equals(idSede))
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
        
        ctx.append("PROVEEDORES ACTIVOS:\n");
        proveedorRepo.findAll().stream()
            .filter(p -> p.getEstado() == 1)
            .forEach(p -> ctx.append("- ").append(p.getNombre())
               .append(" | RUC: ").append(p.getRuc() != null ? p.getRuc() : "-")
               .append(" | Tipo: ").append(p.getTipo() != null ? p.getTipo() : "-")
               .append(" | SUNAT: ").append(p.getEstadoSunat() != null ? p.getEstadoSunat() : "-")
               .append("\n"));
        ctx.append("\n");

        // ── SOLO ADMIN ───────────────────────────────────────────
        if (!esAlmacen) {
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
                    .append(": S/ ").append(String.format("%.2f", e.getValue())).append("\n"));
            ctx.append("\n");

            double valorTotal = productoRepo.findAll().stream()
                .mapToDouble(p -> {
                    double costo = p.getCostoUnitario() != null
                        ? p.getCostoUnitario().doubleValue() : 0;
                    int stock = p.getStockTotal() != null ? p.getStockTotal() : 0;
                    return costo * stock;
                }).sum();
            ctx.append("VALOR TOTAL DEL INVENTARIO: S/ ")
               .append(String.format("%.2f", valorTotal)).append("\n\n");

            ctx.append("SEDES:\n");
            sedeRepo.findAll().forEach(s ->
                ctx.append("- ").append(s.getNombre())
                   .append(" | Dirección: ").append(s.getDireccion() != null ? s.getDireccion() : "-")
                   .append("\n"));
            ctx.append("\n");

            ctx.append("PROVEEDORES ACTIVOS:\n");
            proveedorRepo.findAll().stream()
                .filter(p -> p.getEstado() == 1)
                .forEach(p -> ctx.append("- ").append(p.getNombre())
                   .append(" | RUC: ").append(p.getRuc() != null ? p.getRuc() : "-")
                   .append(" | Tipo: ").append(p.getTipo() != null ? p.getTipo() : "-")
                   .append(" | SUNAT: ").append(p.getEstadoSunat() != null ? p.getEstadoSunat() : "-")
                   .append("\n"));
            ctx.append("\n");
        }

        return ctx.toString();
    }
}