package com.seguridad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.seguridad.model.Kardex;
import com.seguridad.model.Producto;
import com.seguridad.model.Sede;
import com.seguridad.repository.ProductoRepository;
import com.seguridad.repository.SedeRepository;
import com.seguridad.service.KardexService;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/kardex")
public class KardexController {

	 @Autowired
	    private KardexService kardexService;

	    @Autowired
	    private ProductoRepository productoRepository; 

	    @Autowired
	    private SedeRepository sedeRepository;       

	    @GetMapping
	    public String listarKardex(Model model) {
	        List<Producto> productos = productoRepository.findAll();
	        List<Sede> sedes = sedeRepository.findAll();

	        model.addAttribute("productos", productos);
	        model.addAttribute("sedes", sedes);

	        return "kardex/lista";
	    }

	    @GetMapping("/lista/json")
	    @ResponseBody
	    public Map<String, Object> obtenerKardexJson(
	            @RequestParam(required = false) String idProducto,
	            @RequestParam(required = false) Integer idSede,
	            @RequestParam(required = false) String texto,
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "10") int size,
	            @RequestParam(defaultValue = "fecha") String sortBy,
	            @RequestParam(defaultValue = "desc") String sortDir) {

	        try {
	            
	            System.out.println("=== KARDEX JSON ===");
	            System.out.println("idProducto: " + idProducto);
	            System.out.println("idSede: " + idSede);
	            System.out.println("texto: " + texto);
	            System.out.println("page: " + page);

	            Sort sort = sortDir.equalsIgnoreCase("asc")
	                    ? Sort.by(sortBy).ascending()
	                    : Sort.by(sortBy).descending();

	            Pageable pageable = PageRequest.of(page, size, sort);

	            Page<Kardex> paginado = kardexService.listarKardex(idProducto, idSede, texto, pageable);
	            
	            Map<String, Object> response = new HashMap<>();
	            response.put("content", paginado.getContent());
	            response.put("totalElements", paginado.getTotalElements());
	            response.put("totalPages", paginado.getTotalPages());
	            response.put("number", paginado.getNumber());
	            response.put("size", paginado.getSize());

	            System.out.println("Registros encontrados: " + paginado.getTotalElements());
	            return response;

	        } catch (Exception e) {
	            System.err.println("ERROR en obtenerKardexJson: " + e.getMessage());
	            e.printStackTrace();

	            Map<String, Object> errorResponse = new HashMap<>();
	            errorResponse.put("content", List.of());
	            errorResponse.put("totalElements", 0);
	            errorResponse.put("totalPages", 0);
	            errorResponse.put("number", page);
	            errorResponse.put("size", size);
	            errorResponse.put("error", e.getMessage());

	            return errorResponse;
	        }
	    }
}
