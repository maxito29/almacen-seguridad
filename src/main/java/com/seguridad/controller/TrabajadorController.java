package com.seguridad.controller;

import com.seguridad.model.Sede;
import com.seguridad.model.Trabajador;
import com.seguridad.repository.SedeRepository;
import com.seguridad.security.AccesoSedeHelper;
import com.seguridad.service.TrabajadorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/trabajadores")
public class TrabajadorController {

    @Autowired
    TrabajadorService trabajadorService;

    @Autowired
    SedeRepository sedeRepo;

    @Autowired
    AccesoSedeHelper accesoSedeHelper;

    @GetMapping
    public String listar(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         Authentication auth) {

        Integer idSedeRestriccion = accesoSedeHelper.idSedeRestriccion(auth);

        Page<Trabajador> paginado = trabajadorService.listar(page, 10, idSedeRestriccion);

        model.addAttribute("trabajadores", paginado.getContent());
        model.addAttribute("paginado", paginado);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginado.getTotalPages());
        if (idSedeRestriccion == null) {
            model.addAttribute("sedes", sedeRepo.findAll());
        } else {
            model.addAttribute("sedes", sedeRepo.findById(idSedeRestriccion)
                    .map(List::of).orElse(List.of()));
        }

        model.addAttribute("paginaActiva", "trabajadores");

        return "trabajadores/lista";
    }

    @PostMapping("/guardar/ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarAjax(
            @RequestParam Map<String, String> params,
            Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        try {
            Trabajador trabajador = new Trabajador();
            String idStr = params.get("idTrabajador");
            if (idStr != null && !idStr.isBlank()) {
                trabajador.setIdTrabajador(Integer.parseInt(idStr));
            }

            trabajador.setNombreCompleto(params.get("nombreCompleto"));
            trabajador.setDocumentoIdentidad(params.get("documentoIdentidad"));
            trabajador.setPuesto(params.get("puesto"));
            trabajador.setCliente(params.get("cliente"));
            Integer idSedeRestriccion = accesoSedeHelper.idSedeRestriccion(auth);
            if (idSedeRestriccion != null) {
                Sede sede = new Sede();
                sede.setIdSede(idSedeRestriccion);
                trabajador.setSede(sede);
            } else {
                String sedeId = params.get("sede.idSede");
                if (sedeId != null && !sedeId.isBlank()) {
                    Sede sede = new Sede();
                    sede.setIdSede(Integer.parseInt(sedeId));
                    trabajador.setSede(sede);
                }
            }

            String activoCesado = params.get("activoCesado");
            if (activoCesado != null && !activoCesado.isBlank()) {
                trabajador.setActivoCesado(activoCesado);
            }

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
            @PathVariable Integer id,
            Authentication auth) {

        Map<String, Object> response = new HashMap<>();

        try {
            Integer idSedeRestriccion = accesoSedeHelper.idSedeRestriccion(auth);
            if (idSedeRestriccion != null) {
                Trabajador t = trabajadorService.obtenerPorId(id);
                if (t == null) {
                    response.put("success", false);
                    response.put("mensaje", "Trabajador no encontrado");
                    return ResponseEntity.ok(response);
                }
                if (t.getSede() == null || !idSedeRestriccion.equals(t.getSede().getIdSede())) {
                    response.put("success", false);
                    response.put("mensaje", "No tienes permiso sobre este registro");
                    return ResponseEntity.ok(response);
                }
            }

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
            @RequestParam(defaultValue = "") String buscar,
            Authentication auth) {

        Integer idSedeRestriccion = accesoSedeHelper.idSedeRestriccion(auth);

        Page<Trabajador> paginado;

        boolean tieneTexto = !buscar.isEmpty();
        boolean tieneEstado = !estado.isEmpty();

        if (tieneTexto && tieneEstado) {

            paginado = trabajadorService.buscarConFiltro(
                    buscar, estado, page, 10, idSedeRestriccion);

        } else if (tieneTexto) {

            paginado = trabajadorService.buscar(
                    buscar, page, 10, idSedeRestriccion);

        } else if (tieneEstado) {

            paginado = trabajadorService.listarPorEstado(
                    estado, page, 10, idSedeRestriccion);

        } else {

            paginado = trabajadorService.listar(
                    page, 10, idSedeRestriccion);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("trabajadores", paginado.getContent());
        response.put("totalElements", paginado.getTotalElements());
        response.put("totalPages", paginado.getTotalPages());
        response.put("currentPage", page);

        return response;
    }
}