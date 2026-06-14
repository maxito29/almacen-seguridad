package com.seguridad.controller;

import com.seguridad.model.Proveedor;
import com.seguridad.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired ProveedorService proveedorService;

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page) {

        Page<Proveedor> paginado = proveedorService.listar(page, 10);

        model.addAttribute("proveedores",  paginado.getContent());
        model.addAttribute("paginado",     paginado);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginado.getTotalPages());
        model.addAttribute("paginaActiva", "proveedores");
        return "proveedores/lista";
    }

    @PostMapping("/guardar/ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarAjax(
            @ModelAttribute Proveedor proveedor) {
        Map<String, Object> response = new HashMap<>();
        try {
            proveedorService.guardar(proveedor);
            response.put("success", true);
            response.put("mensaje", "Proveedor guardado correctamente");
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/estado/ajax/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cambiarEstadoAjax(
            @PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            proveedorService.cambiarEstado(id);
            response.put("success", true);
            response.put("mensaje", "Estado actualizado");
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lista/json")
    @ResponseBody
    public Map<String, Object> listarJson(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String estado,
            @RequestParam(defaultValue = "") String buscar) {

        Page<Proveedor> paginado;
        boolean tieneTexto  = !buscar.isEmpty();
        boolean tieneEstado = !estado.isEmpty();

        if (tieneTexto && tieneEstado) {
            int estadoNum = estado.equals("activo") ? 1 : 2;
            paginado = proveedorService.buscarConFiltro(
                buscar, estadoNum, page, 10);
        } else if (tieneTexto) {
            paginado = proveedorService.buscar(buscar, page, 10);
        } else if (tieneEstado) {
            int estadoNum = estado.equals("activo") ? 1 : 2;
            paginado = proveedorService.listarPorEstado(estadoNum, page, 10);
        } else {
            paginado = proveedorService.listar(page, 10);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("proveedores",   paginado.getContent());
        response.put("totalElements", paginado.getTotalElements());
        response.put("totalPages",    paginado.getTotalPages());
        response.put("currentPage",   page);
        return response;
    }
}