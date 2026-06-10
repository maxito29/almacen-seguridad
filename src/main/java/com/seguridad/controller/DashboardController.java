package com.seguridad.controller;


import com.seguridad.model.Producto;
import com.seguridad.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    @Autowired SedeRepository       sedeRepo;
    @Autowired ProductoRepository   productoRepo;
    @Autowired IngresoRepository    ingresoRepo;
    @Autowired SalidaRepository     salidaRepo;
    @Autowired TrabajadorRepository trabajadorRepo;

    @GetMapping("/")
    public String dashboard(Model model,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 8, Sort.by("stockTotal").ascending());
        Page<Producto> paginado = productoRepo.findAll(pageable);

        model.addAttribute("totalProductos",    productoRepo.count());
        model.addAttribute("totalIngresos",     ingresoRepo.count());
        model.addAttribute("totalSalidas",      salidaRepo.count());
        model.addAttribute("totalTrabajadores", trabajadorRepo.count());
        model.addAttribute("sedes",             sedeRepo.findAll());
        model.addAttribute("productos",         paginado.getContent());
        model.addAttribute("paginado",          paginado);
        model.addAttribute("paginaActual",      page);
        model.addAttribute("totalPaginas",      paginado.getTotalPages());
        model.addAttribute("paginaActiva",      "dashboard");
        return "dashboard";
    }

}
