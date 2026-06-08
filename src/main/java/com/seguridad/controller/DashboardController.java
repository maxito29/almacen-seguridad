package com.seguridad.controller;


import com.seguridad.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired SedeRepository sedeRepo;
    @Autowired ProductoRepository productoRepo;
    @Autowired IngresoRepository ingresoRepo;
    @Autowired SalidaRepository salidaRepo;
    @Autowired TrabajadorRepository trabajadorRepo;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalProductos", productoRepo.count());
        model.addAttribute("totalIngresos", ingresoRepo.count());
        model.addAttribute("totalSalidas", salidaRepo.count());
        model.addAttribute("totalTrabajadores", trabajadorRepo.count());
        model.addAttribute("sedes", sedeRepo.findAll());
        model.addAttribute("productos", productoRepo.findAll());
        model.addAttribute("paginaActiva", "dashboard");
        return "dashboard";
    }
}
