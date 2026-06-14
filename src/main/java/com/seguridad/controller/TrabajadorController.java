package com.seguridad.controller;

import com.seguridad.model.Sede;
import com.seguridad.model.Trabajador;
import com.seguridad.repository.SedeRepository;
import com.seguridad.service.TrabajadorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/trabajadores")
public class TrabajadorController {

    @Autowired
    TrabajadorService trabajadorService;

    @Autowired
    SedeRepository sedeRepo;

    @GetMapping
    public String listar(Model model,
                         @RequestParam(defaultValue = "0") int page) {

        Page<Trabajador> paginado = trabajadorService.listar(page, 10);

        model.addAttribute("trabajadores", paginado.getContent());
        model.addAttribute("paginado", paginado);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginado.getTotalPages());
        model.addAttribute("sedes", sedeRepo.findAll());
        model.addAttribute("paginaActiva", "trabajadores");

        return "trabajadores/lista";
    }

    @PostMapping("/guardar/ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarAjax(
            @RequestParam Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            Trabajador trabajador = new Trabajador();

            // ID (para edición)
            String idStr = params.get("idTrabajador");
            if (idStr != null && !idStr.isBlank()) {
                trabajador.setIdTrabajador(Integer.parseInt(idStr));
            }

            trabajador.setNombreCompleto(params.get("nombreCompleto"));
            trabajador.setDocumentoIdentidad(params.get("documentoIdentidad"));
            trabajador.setPuesto(params.get("puesto"));
            trabajador.setCliente(params.get("cliente"));

            // Sede — solo si viene un valor válido
            String sedeId = params.get("sede.idSede");
            if (sedeId != null && !sedeId.isBlank()) {
                Sede sede = new Sede();
                sede.setIdSede(Integer.parseInt(sedeId));
                trabajador.setSede(sede);
            }
            
         // Dentro de guardarAjax
            String activoCesado = params.get("activoCesado");
            if (activoCesado != null && !activoCesado.isBlank()) {
                trabajador.setActivoCesado(activoCesado);
            }

            // Fecha de ingreso
            String fechaStr = params.get("fechaIngreso");
            if (fechaStr != null && !fechaStr.isBlank()) {
                try {
                    java.text.SimpleDateFormat sdf =
                        new java.text.SimpleDateFormat("yyyy-MM-dd");
                    trabajador.setFechaIngreso(sdf.parse(fechaStr));
                } catch (Exception ignored) {}
            }

            trabajadorService.guardar(trabajador);
            response.put("success", true);
            response.put("mensaje", "Trabajador guardado correctamente");

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
            trabajadorService.cambiarEstado(id);

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

        Page<Trabajador> paginado;

        boolean tieneTexto = !buscar.isEmpty();
        boolean tieneEstado = !estado.isEmpty();

        if (tieneTexto && tieneEstado) {

            paginado = trabajadorService.buscarConFiltro(
                    buscar,
                    estado,
                    page,
                    10);

        } else if (tieneTexto) {

            paginado = trabajadorService.buscar(
                    buscar,
                    page,
                    10);

        } else if (tieneEstado) {

            paginado = trabajadorService.listarPorEstado(
                    estado,
                    page,
                    10);

        } else {

            paginado = trabajadorService.listar(
                    page,
                    10);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("trabajadores", paginado.getContent());
        response.put("totalElements", paginado.getTotalElements());
        response.put("totalPages", paginado.getTotalPages());
        response.put("currentPage", page);

        return response;
    }
}
