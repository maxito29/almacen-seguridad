package com.seguridad.controller;

import com.seguridad.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/export")
public class ExportController {

    @Autowired ExportService exportService;
    @GetMapping("/ingresos/excel")
    public ResponseEntity<byte[]> ingresosExcel() throws Exception {
        byte[] datos = exportService.exportarIngresosExcel();
        String nombre = "Ingresos_" +
            new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())
            + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + nombre)
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument" +
                ".spreadsheetml.sheet"))
            .body(datos);
    }

    @GetMapping("/ingresos/pdf")
    public ResponseEntity<byte[]> ingresosPdf() throws Exception {
        byte[] datos = exportService.exportarIngresosPdf();
        String nombre = "Ingresos_" +
            new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())
            + ".pdf";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + nombre)
            .contentType(MediaType.APPLICATION_PDF)
            .body(datos);
    }

    @GetMapping("/productos/excel")
    public ResponseEntity<byte[]> productosExcel() throws Exception {
        byte[] datos = exportService.exportarProductosExcel();
        String nombre = "Productos_" +
            new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())
            + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + nombre)
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument" +
                ".spreadsheetml.sheet"))
            .body(datos);
    }

    @GetMapping("/productos/pdf")
    public ResponseEntity<byte[]> productosPdf() throws Exception {
        byte[] datos = exportService.exportarProductosPdf();
        String nombre = "Productos_" +
            new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())
            + ".pdf";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + nombre)
            .contentType(MediaType.APPLICATION_PDF)
            .body(datos);
    }
}