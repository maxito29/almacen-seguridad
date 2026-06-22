package com.seguridad.controller;

import com.seguridad.model.*;
import com.seguridad.repository.*;
import com.seguridad.security.AccesoSedeHelper;
import com.seguridad.service.IngresoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/ingresos")
public class IngresoController {

	private final IngresoRepository ingresoRepository;
	@Autowired IngresoService ingresoService;
    @Autowired ProductoRepository productoRepo;
    @Autowired ProveedorRepository proveedorRepo;
    @Autowired SedeRepository sedeRepo;
    @Autowired AccesoSedeHelper accesoSedeHelper;

	IngresoController(IngresoRepository ingresoRepository) {
		this.ingresoRepository = ingresoRepository;
	}

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String buscar,
            Authentication auth) {

        Integer idSedeRestriccion = accesoSedeHelper.idSedeRestriccion(auth);

        Page<Ingreso> paginado = ingresoService.listar(page, 10, idSedeRestriccion);

        model.addAttribute("ingresos",     paginado.getContent());
        model.addAttribute("paginado",     paginado);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginado.getTotalPages());
        model.addAttribute("productos",    productoRepo.findAll());
        model.addAttribute("proveedores",  proveedorRepo.findAll());

        if (idSedeRestriccion == null) {
            model.addAttribute("sedes", sedeRepo.findAll());
        } else {
            model.addAttribute("sedes", sedeRepo.findById(idSedeRestriccion)
                    .map(List::of).orElse(List.of()));
        }

        model.addAttribute("paginaActiva", "ingresos");
        return "ingresos/lista";
    }

    @PostMapping("/guardar/ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarAjax(
            @ModelAttribute Ingreso ingreso,
            Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer idSedeRestriccion = accesoSedeHelper.idSedeRestriccion(auth);
            if (idSedeRestriccion != null) {
                Sede sedePermitida = sedeRepo.findById(idSedeRestriccion)
                        .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
                ingreso.setSede(sedePermitida);
            }

            ingresoService.guardar(ingreso);
            response.put("success", true);
            response.put("mensaje", "Ingreso guardado correctamente");
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
                Ingreso ingreso = ingresoRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));
                if (!idSedeRestriccion.equals(ingreso.getSede().getIdSede())) {
                    response.put("success", false);
                    response.put("mensaje", "No tienes permiso sobre este registro");
                    return ResponseEntity.ok(response);
                }
            }

            ingresoService.cambiarEstado(id);
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

        Page<Ingreso> paginado;

        boolean tieneTexto = !buscar.isEmpty();
        boolean tieneEstado = !estado.isEmpty();

        if (tieneTexto && tieneEstado) {
            int estadoNum = estado.equals("activo") ? 1 : 2;
            paginado = ingresoService.buscarConFiltro(buscar, estadoNum, page, 10, idSedeRestriccion);

        } else if (tieneTexto) {
            paginado = ingresoService.buscar(buscar, page, 10, idSedeRestriccion);

        } else if (tieneEstado) {
            int estadoNum = estado.equals("activo") ? 1 : 2;
            Pageable pageable = PageRequest.of(page, 10);
            paginado = (idSedeRestriccion == null)
                    ? ingresoRepository.findByEstado(estadoNum, pageable)
                    : ingresoRepository.findByEstadoAndSede_IdSede(estadoNum, idSedeRestriccion, pageable);

        } else {
            paginado = ingresoService.listar(page, 10, idSedeRestriccion);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("ingresos",      paginado.getContent());
        response.put("totalElements", paginado.getTotalElements());
        response.put("totalPages",    paginado.getTotalPages());
        response.put("currentPage",   page);

        return response;
    }
}