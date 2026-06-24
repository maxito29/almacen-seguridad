package com.seguridad.controller;

import com.seguridad.model.Salida;
import com.seguridad.service.SalidaService;
import com.seguridad.repository.SalidaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/salidas")
public class SalidaController {

    @Autowired
    private SalidaService salidaService;

    @Autowired
    private SalidaRepository salidaRepository;

    @GetMapping
    public String listar(
            @RequestParam(required = false) Integer estado,
            @RequestParam(required = false) String texto,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        Page<Salida> lista = salidaRepository.filtrarSalidas(
                null,
                estado,
                texto,
                pageable
        );

        model.addAttribute("lista", lista);
        model.addAttribute("estado", estado);
        model.addAttribute("texto", texto);

        return "salidas/lista";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Salida salida) {

        salidaService.guardar(salida);

        return "redirect:/salidas";
    }

    @GetMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable Integer id) {

        Salida s = salidaRepository.findById(id).orElse(null);

        if (s != null) {
            if (s.getEstado() == 1) {
                s.setEstado(0);
            } else {
                s.setEstado(1);
            }
            salidaRepository.save(s);
        }

        return "redirect:/salidas";
    }
}