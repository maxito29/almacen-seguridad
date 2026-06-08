package com.seguridad.controller;


import com.seguridad.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository repo;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", repo.findAll());
        return "productos/lista";
    }
}
