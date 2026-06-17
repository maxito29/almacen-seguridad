package com.seguridad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seguridad.model.Kardex;
import com.seguridad.repository.KardexRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class KardexService {

	@Autowired
    private KardexRepository kardexRepository;

	 public Page<Kardex> listarKardex(String idProducto, Integer idSede, String texto, Pageable pageable) {
	        try {
	            System.out.println("--- SERVICE KARDEX ---");
	            System.out.println("idProducto: " + idProducto);
	            System.out.println("idSede: " + idSede);
	            System.out.println("texto: " + texto);

	            Page<Kardex> resultado;

	            // con producto, sede y texto
	            if (idProducto != null && !idProducto.isEmpty() && idSede != null && texto != null && !texto.isEmpty()) {
	                System.out.println("con producto, sede y texto");
	                resultado = kardexRepository.filtrarKardex(idProducto, idSede, texto, pageable);
	            }
	            // con producto y sede
	            else if (idProducto != null && !idProducto.isEmpty() && idSede != null) {
	                System.out.println("con producto y sede");
	                resultado = kardexRepository.findByProducto_IdProductoAndSede_IdSede(idProducto, idSede, pageable);
	            }
	            // con producto y textro
	            else if (idProducto != null && !idProducto.isEmpty() && texto != null && !texto.isEmpty()) {
	                System.out.println("con producto y textro");
	                resultado = kardexRepository.filtrarKardex(idProducto, null, texto, pageable);
	            }
	            // con sede y texto
	            else if (idSede != null && texto != null && !texto.isEmpty()) {
	                System.out.println("con sede y texto");
	                resultado = kardexRepository.filtrarKardex(null, idSede, texto, pageable);
	            }
	            // solo producto
	            else if (idProducto != null && !idProducto.isEmpty()) {
	                System.out.println("solo producto");
	                resultado = kardexRepository.findByProducto_IdProducto(idProducto, pageable);
	            }
	            // solo sede
	            else if (idSede != null) {
	                System.out.println("solo sede");
	                resultado = kardexRepository.findBySede_IdSede(idSede, pageable);
	            }
	            // solo texto
	            else if (texto != null && !texto.isEmpty()) {
	                System.out.println("solo texto");
	                resultado = kardexRepository.buscarPorTexto(texto, pageable);
	            }
	            // todos
	            else {
	                System.out.println("sin filtros todos");
	                resultado = kardexRepository.findAll(pageable);
	            }

	            System.out.println("Registros obtenidos: " + resultado.getTotalElements());
	            return resultado;

	        } catch (Exception e) {
	            System.err.println("ERROR en listarKardex: " + e.getMessage());
	            e.printStackTrace();
	            throw e;
	        }
	    }
	
}
