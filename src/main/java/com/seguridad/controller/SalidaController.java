package com.seguridad.controller;

import com.seguridad.model.*;
import com.seguridad.repository.*;
import com.seguridad.security.AccesoSedeHelper;
import com.seguridad.service.SalidaService;

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
@RequestMapping("/salidas")
public class SalidaController {

    private final SalidaRepository salidaRepository;
    @Autowired SalidaService salidaService;
    @Autowired ProductoRepository productoRepo;
    @Autowired TrabajadorRepository trabajadorRepo;
    @Autowired SedeRepository sedeRepo;
    @Autowired AccesoSedeHelper accesoSedeHelper;

    SalidaController(SalidaRepository salidaRepository) {
        this.salidaRepository = salidaRepository;
    }

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String buscar,
            Authentication auth) {

        Integer idSedeRestriccion = accesoSedeHelper.idSedeRestriccion(auth);

        Page<Salida> paginado = salidaService.listar(page, 10, idSedeRestriccion);

        model.addAttribute("salidas",      paginado.getContent());
        model.addAttribute("paginado",     paginado);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginado.getTotalPages());
        model.addAttribute("productos",    productoRepo.findAll());
        model.addAttribute("trabajadores", trabajadorRepo.findByActivoCesado("ACTIVO"));

        if (idSedeRestriccion == null) {
            model.addAttribute("sedes", sedeRepo.findAll());
        } else {
            model.addAttribute("sedes", sedeRepo.findById(idSedeRestriccion)
                    .map(List::of).orElse(List.of()));
        }

        model.addAttribute("paginaActiva", "salidas");
        return "salidas/lista";
    }

    @PostMapping("/guardar/ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarAjax(
            @ModelAttribute Salida salida,
            Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer idSedeRestriccion = accesoSedeHelper.idSedeRestriccion(auth);
            if (idSedeRestriccion != null) {
                Sede sedePermitida = sedeRepo.findById(idSedeRestriccion)
                        .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
                salida.setSede(sedePermitida);
            }

            salidaService.guardar(salida);
            response.put("success", true);
            response.put("mensaje", "Salida guardada correctamente");
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
                Salida salida = salidaRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Salida no encontrada"));
                if (!idSedeRestriccion.equals(salida.getSede().getIdSede())) {
                    response.put("success", false);
                    response.put("mensaje", "No tienes permiso sobre este registro");
                    return ResponseEntity.ok(response);
                }
            }

            salidaService.cambiarEstado(id);
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

        Page<Salida> paginado;

        boolean tieneTexto  = !buscar.isEmpty();
        boolean tieneEstado = !estado.isEmpty();

        if (tieneTexto && tieneEstado) {
            int estadoNum = estado.equals("activo") ? 1 : 2;
            paginado = salidaService.buscarConFiltro(buscar, estadoNum, page, 10, idSedeRestriccion);

        } else if (tieneTexto) {
            paginado = salidaService.buscar(buscar, page, 10, idSedeRestriccion);

        } else if (tieneEstado) {
            int estadoNum = estado.equals("activo") ? 1 : 2;
            Pageable pageable = PageRequest.of(page, 10);
            paginado = (idSedeRestriccion == null)
                    ? salidaRepository.findByEstado(estadoNum, pageable)
                    : salidaRepository.findByEstadoAndSede_IdSede(estadoNum, idSedeRestriccion, pageable);

        } else {
            paginado = salidaService.listar(page, 10, idSedeRestriccion);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("salidas",       paginado.getContent());
        response.put("totalElements", paginado.getTotalElements());
        response.put("totalPages",    paginado.getTotalPages());
        response.put("currentPage",   page);

        return response;
        
    }
    
    
}