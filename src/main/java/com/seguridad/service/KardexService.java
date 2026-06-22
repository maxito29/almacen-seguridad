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

    public Page<Kardex> listarKardex(String idProducto, Integer idSede,
                                      String tipoMov, String texto,
                                      Pageable pageable) {
        try {
            System.out.println("--- SERVICE KARDEX ---");
            System.out.println("idProducto: " + idProducto);
            System.out.println("idSede: " + idSede);
            System.out.println("tipoMov: " + tipoMov);
            System.out.println("texto: " + texto);
            String idProductoNorm = (idProducto != null && !idProducto.isEmpty())
                    ? idProducto : null;
            String tipoMovNorm = (tipoMov != null && !tipoMov.isEmpty())
                    ? tipoMov : null;
            String textoNorm = (texto != null && !texto.isEmpty())
                    ? texto : null;

            Page<Kardex> resultado = kardexRepository.filtrarKardex(
                    idProductoNorm, idSede, tipoMovNorm, textoNorm, pageable);

            System.out.println("Registros obtenidos: " + resultado.getTotalElements());
            return resultado;
        } catch (Exception e) {
            System.err.println("ERROR en listarKardex: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}