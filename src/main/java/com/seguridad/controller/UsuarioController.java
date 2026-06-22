package com.seguridad.controller;

import com.seguridad.model.Usuario;
import com.seguridad.repository.SedeRepository;
import com.seguridad.service.EmailService;
import com.seguridad.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired UsuarioService usuarioService;
    @Autowired SedeRepository sedeRepo;
    @Autowired EmailService emailService;

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page) {
        Page<Usuario> paginado = usuarioService.listar(page, 10);
        model.addAttribute("usuarios",     paginado.getContent());
        model.addAttribute("paginado",     paginado);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginado.getTotalPages());
        model.addAttribute("sedes",        sedeRepo.findAll());
        model.addAttribute("paginaActiva", "usuarios");
        return "usuarios/lista";
    }
    

    @PostMapping("/guardar/ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarAjax(
            @RequestParam Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean esNuevo = params.get("idUsuario") == null || params.get("idUsuario").isBlank();

            Usuario usuario = new Usuario();
            if (!esNuevo) {
                usuario.setIdUsuario(Integer.parseInt(params.get("idUsuario")));
            }
            usuario.setUsername(params.get("username"));
            usuario.setPassword(params.get("password"));
            usuario.setNombre(params.get("nombre"));
            usuario.setEmail(params.get("email"));          
            usuario.setRol(params.get("rol"));

            if (params.get("estado") != null && !params.get("estado").isBlank()) {
                usuario.setEstado(Integer.parseInt(params.get("estado")));
            }
            if ("ALMACEN".equals(params.get("rol"))
                    && params.get("idSede") != null
                    && !params.get("idSede").isBlank()) {
                sedeRepo.findById(Integer.parseInt(params.get("idSede")))
                        .ifPresent(usuario::setSede);
            }

            usuarioService.guardar(usuario);

            if (esNuevo && usuario.getEmail() != null && !usuario.getEmail().isBlank()) {
                emailService.enviarBienvenida(
                    usuario.getEmail(), usuario.getUsername(),
                    usuario.getNombre(), params.get("password"));
            }

            response.put("success", true);
            response.put("mensaje", "Usuario guardado correctamente");
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
            usuarioService.cambiarEstado(id);
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
        Page<Usuario> paginado;
        boolean tieneTexto  = !buscar.isEmpty();
        boolean tieneEstado = !estado.isEmpty();
        if (tieneTexto && tieneEstado) {
            int estadoNum = estado.equals("activo") ? 1 : 2;
            paginado = usuarioService.buscarConFiltro(buscar, estadoNum, page, 10);
        } else if (tieneTexto) {
            paginado = usuarioService.buscar(buscar, page, 10);
        } else if (tieneEstado) {
            int estadoNum = estado.equals("activo") ? 1 : 2;
            paginado = usuarioService.listarPorEstado(estadoNum, page, 10);
        } else {
            paginado = usuarioService.listar(page, 10);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("usuarios",      paginado.getContent());
        response.put("totalElements", paginado.getTotalElements());
        response.put("totalPages",    paginado.getTotalPages());
        response.put("currentPage",   page);
        return response;
    }
}