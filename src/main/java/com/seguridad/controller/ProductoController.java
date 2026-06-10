package com.seguridad.controller;

import com.seguridad.model.Producto;
import com.seguridad.repository.TipoEquipoRepository;
import com.seguridad.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired ProductoService productoService;
    @Autowired TipoEquipoRepository tipoEquipoRepo;

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page) {

        Page<Producto> paginado = productoService.listar(page, 10);

        model.addAttribute("productos",    paginado.getContent());
        model.addAttribute("paginado",     paginado);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginado.getTotalPages());
        model.addAttribute("tipos",        tipoEquipoRepo.findAll());
        model.addAttribute("paginaActiva", "productos");
        return "productos/lista";
    }

    @PostMapping("/guardar/ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarAjax(
            @ModelAttribute Producto producto) {
        Map<String, Object> response = new HashMap<>();
        try {
            productoService.guardar(producto);
            response.put("success", true);
            response.put("mensaje", "Producto guardado correctamente");
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/estado/ajax/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cambiarEstadoAjax(
            @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            productoService.cambiarEstado(id);
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

        Page<Producto> paginado;
        boolean tieneTexto  = !buscar.isEmpty();
        boolean tieneEstado = !estado.isEmpty();

        if (tieneTexto && tieneEstado) {
            int estadoNum = estado.equals("activo") ? 1 : 2;
            paginado = productoService.buscarConFiltro(buscar, estadoNum, page, 10);
        } else if (tieneTexto) {
            paginado = productoService.buscar(buscar, page, 10);
        } else if (tieneEstado) {
            int estadoNum = estado.equals("activo") ? 1 : 2;
            paginado = productoService.listarPorEstado(estadoNum, page, 10);
        } else {
            paginado = productoService.listar(page, 10);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("productos",     paginado.getContent());
        response.put("totalElements", paginado.getTotalElements());
        response.put("totalPages",    paginado.getTotalPages());
        response.put("currentPage",   page);
        return response;
    }
}