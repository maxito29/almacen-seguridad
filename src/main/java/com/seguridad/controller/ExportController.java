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
    
    @GetMapping("/salidas/excel")
    public ResponseEntity<byte[]> salidasExcel() throws Exception {
        byte[] datos = exportService.exportarSalidasExcel();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Salidas_" +
                new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".xlsx")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(datos);
    }

    @GetMapping("/salidas/pdf")
    public ResponseEntity<byte[]> salidasPdf() throws Exception {
        byte[] datos = exportService.exportarSalidasPdf();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Salidas_" +
                new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(datos);
    }

    @GetMapping("/trabajadores/excel")
    public ResponseEntity<byte[]> trabajadoresExcel() throws Exception {
        byte[] datos = exportService.exportarTrabajadoresExcel();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Trabajadores_" +
                new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".xlsx")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(datos);
    }

    @GetMapping("/trabajadores/pdf")
    public ResponseEntity<byte[]> trabajadoresPdf() throws Exception {
        byte[] datos = exportService.exportarTrabajadoresPdf();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Trabajadores_" +
                new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(datos);
    }

    @GetMapping("/proveedores/excel")
    public ResponseEntity<byte[]> proveedoresExcel() throws Exception {
        byte[] datos = exportService.exportarProveedoresExcel();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Proveedores_" +
                new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".xlsx")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(datos);
    }

    @GetMapping("/proveedores/pdf")
    public ResponseEntity<byte[]> proveedoresPdf() throws Exception {
        byte[] datos = exportService.exportarProveedoresPdf();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Proveedores_" +
                new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(datos);
    }
    
    
}