package com.seguridad.controller;


import com.seguridad.model.Producto;
import com.seguridad.repository.*;

import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class DashboardController {

    @Autowired SedeRepository       sedeRepo;
    @Autowired ProductoRepository   productoRepo;
    @Autowired IngresoRepository    ingresoRepo;
    @Autowired SalidaRepository     salidaRepo;
    @Autowired TrabajadorRepository trabajadorRepo;

    @GetMapping("/")
    public String dashboard(Model model,
            @RequestParam(defaultValue = "0") int page) throws Exception {

        // ── Paginacion stock ──────────────────────────────────────
        Pageable pageable = PageRequest.of(page, 8,
            Sort.by("stockTotal").ascending());
        Page<Producto> paginado = productoRepo.findAll(pageable);

        // ── KPIs ─────────────────────────────────────────────────
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

        ObjectMapper mapper = new ObjectMapper();

        // ── Grafico 1: Dona — productos por tipo ─────────────────
        List<Object[]> porTipo = productoRepo.contarPorTipo();
        List<String>  tipoLabels  = new ArrayList<>();
        List<Long>    tipoCounts  = new ArrayList<>();
        for (Object[] row : porTipo) {
            tipoLabels.add((String) row[0]);
            tipoCounts.add((Long)   row[1]);
        }
        model.addAttribute("tipoLabels", mapper.writeValueAsString(tipoLabels));
        model.addAttribute("tipoCounts", mapper.writeValueAsString(tipoCounts));

        // ── Grafico 2: Barras — ingresos vs salidas por mes ──────
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -5);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date fechaInicio = cal.getTime();

        String[] mesesNombres = {"Ene","Feb","Mar","Abr","May","Jun",
                                  "Jul","Ago","Sep","Oct","Nov","Dic"};

        // Construir mapa de los ultimos 6 meses
        List<String> mesesLabels   = new ArrayList<>();
        List<Long>   ingresosData  = new ArrayList<>();
        List<Long>   salidasData   = new ArrayList<>();

        Calendar iterador = Calendar.getInstance();
        iterador.add(Calendar.MONTH, -5);
        for (int i = 0; i < 6; i++) {
            mesesLabels.add(mesesNombres[iterador.get(Calendar.MONTH)]);
            ingresosData.add(0L);
            salidasData.add(0L);
            iterador.add(Calendar.MONTH, 1);
        }

        // Llenar con datos reales de ingresos
        List<Object[]> ingresosPorMes = ingresoRepo.contarPorMes(fechaInicio);
        Calendar base = Calendar.getInstance();
        base.add(Calendar.MONTH, -5);
        int mesBase = base.get(Calendar.MONTH); // 0-11

        for (Object[] row : ingresosPorMes) {
            int mes   = ((Number) row[0]).intValue() - 1; // MONTH() devuelve 1-12
            int index = (mes - mesBase + 12) % 12;
            if (index < 6) ingresosData.set(index, ((Number) row[1]).longValue());
        }

        // Llenar con datos reales de salidas
        List<Object[]> salidasPorMes = salidaRepo.contarPorMes(fechaInicio);
        for (Object[] row : salidasPorMes) {
            int mes   = ((Number) row[0]).intValue() - 1;
            int index = (mes - mesBase + 12) % 12;
            if (index < 6) salidasData.set(index, ((Number) row[1]).longValue());
        }

        model.addAttribute("mesesLabels",  mapper.writeValueAsString(mesesLabels));
        model.addAttribute("ingresosData", mapper.writeValueAsString(ingresosData));
        model.addAttribute("salidasData",  mapper.writeValueAsString(salidasData));

        // ── Grafico 3: Top 5 productos por stock ─────────────────
        List<Object[]> top5 = productoRepo.top5Productos(
            PageRequest.of(0, 5));
        List<String> top5Labels  = new ArrayList<>();
        List<Integer> top5Stock  = new ArrayList<>();
        for (Object[] row : top5) {
            // Acortar descripcion si es muy larga
            String desc = (String) row[0];
            top5Labels.add(desc.length() > 25 ? desc.substring(0, 25) + "..." : desc);
            top5Stock.add(((Number) row[1]).intValue());
        }
        model.addAttribute("top5Labels", mapper.writeValueAsString(top5Labels));
        model.addAttribute("top5Stock",  mapper.writeValueAsString(top5Stock));

        return "dashboard";
    }
}