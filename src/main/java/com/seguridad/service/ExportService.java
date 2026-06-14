package com.seguridad.service;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import com.seguridad.model.Ingreso;
import com.seguridad.model.Producto;
import com.seguridad.repository.IngresoRepository;
import com.seguridad.repository.ProductoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ExportService {

	@Autowired
	IngresoRepository ingresoRepo;
	@Autowired
	ProductoRepository productoRepo;

	// ══════════════════════════════════════════════════════════
	// EXCEL — INGRESOS
	// ══════════════════════════════════════════════════════════
	public byte[] exportarIngresosExcel() throws Exception {

        List<Ingreso> ingresos = ingresoRepo.findAll();
        XSSFWorkbook  wb       = new XSSFWorkbook();
        XSSFSheet     sheet    = wb.createSheet("Ingresos");

        // ── Estilos ──────────────────────────────────────────
        XSSFCellStyle estiloTitulo = wb.createCellStyle();
        XSSFFont fuenteTitulo = wb.createFont();
        fuenteTitulo.setBold(true);
        fuenteTitulo.setFontHeightInPoints((short) 14);
        fuenteTitulo.setColor(IndexedColors.WHITE.getIndex());
        estiloTitulo.setFont(fuenteTitulo);
        estiloTitulo.setFillForegroundColor(
            new XSSFColor(new byte[]{(byte)26,(byte)26,(byte)46}, null));
        estiloTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloTitulo.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle estiloHeader = wb.createCellStyle();
        XSSFFont fuenteHeader = wb.createFont();
        fuenteHeader.setBold(true);
        fuenteHeader.setColor(IndexedColors.WHITE.getIndex());
        estiloHeader.setFont(fuenteHeader);
        estiloHeader.setFillForegroundColor(
            new XSSFColor(new byte[]{(byte)240,(byte)165,(byte)0}, null));
        estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloHeader.setAlignment(HorizontalAlignment.CENTER);
        estiloHeader.setBorderBottom(BorderStyle.THIN);

        XSSFCellStyle estiloFila = wb.createCellStyle();
        estiloFila.setBorderBottom(BorderStyle.THIN);
        estiloFila.setBorderLeft(BorderStyle.THIN);
        estiloFila.setBorderRight(BorderStyle.THIN);
        estiloFila.setBottomBorderColor(
            IndexedColors.GREY_25_PERCENT.getIndex());

        XSSFCellStyle estiloFilaAlt = wb.createCellStyle();
        estiloFilaAlt.cloneStyleFrom(estiloFila);
        estiloFilaAlt.setFillForegroundColor(
            new XSSFColor(new byte[]{(byte)248,(byte)249,(byte)250}, null));
        estiloFilaAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle estiloMoneda = wb.createCellStyle();
        estiloMoneda.cloneStyleFrom(estiloFila);
        XSSFDataFormat fmt = wb.createDataFormat();
        estiloMoneda.setDataFormat(
        	    fmt.getFormat("\"S/\" #,##0.00")
        	);

        // ── Fila titulo ───────────────────────────────────────
        Row rowTitulo = sheet.createRow(0);
        rowTitulo.setHeightInPoints(28);
        Cell cellTitulo = rowTitulo.createCell(0);
        cellTitulo.setCellValue("REPORTE DE INGRESOS — SISTEMA ALMACÉN");
        cellTitulo.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

        // ── Fila fecha ────────────────────────────────────────
        Row rowFecha = sheet.createRow(1);
        Cell cellFecha = rowFecha.createCell(0);
        cellFecha.setCellValue("Generado: " +
            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

        // ── Headers ───────────────────────────────────────────
        String[] headers = {"#", "Producto", "Código",
            "Proveedor", "Sede", "Cantidad",
            "Costo Unit.", "Total", "Fecha"};

        Row rowHeader = sheet.createRow(3);
        rowHeader.setHeightInPoints(20);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = rowHeader.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(estiloHeader);
        }

        // ── Datos ─────────────────────────────────────────────
        int rowNum = 4;
        double totalGeneral = 0;

        for (Ingreso ing : ingresos) {
            Row row = sheet.createRow(rowNum);
            XSSFCellStyle estilo = rowNum % 2 == 0
                ? estiloFilaAlt : estiloFila;

            row.createCell(0).setCellValue(ing.getIdIngreso());
            row.createCell(1).setCellValue(
                ing.getProducto().getDescripcion());
            row.createCell(2).setCellValue(
                ing.getProducto().getIdProducto());
            row.createCell(3).setCellValue(
                ing.getProveedor() != null
                    ? ing.getProveedor().getNombre() : "-");
            row.createCell(4).setCellValue(
                ing.getSede().getNombre());
            row.createCell(5).setCellValue(ing.getCantidad());

            Cell celdaCosto = row.createCell(6);
            celdaCosto.setCellValue(ing.getCostoUnitario());
            celdaCosto.setCellStyle(estiloMoneda);

            Cell celdaTotal = row.createCell(7);
            celdaTotal.setCellValue(ing.getTotal());
            celdaTotal.setCellStyle(estiloMoneda);

            row.createCell(8).setCellValue(
                ing.getFecha() != null
                    ? new SimpleDateFormat("dd/MM/yyyy")
                        .format(ing.getFecha()) : "-");

            for (int i = 0; i <= 8; i++) {
                if (i != 6 && i != 7)
                    row.getCell(i).setCellStyle(estilo);
            }

            totalGeneral += ing.getTotal() != null
                ? ing.getTotal() : 0;
            rowNum++;
        }

        // ── Fila total ────────────────────────────────────────
        Row rowTotal = sheet.createRow(rowNum + 1);
        XSSFCellStyle estiloTotal = wb.createCellStyle();
        XSSFFont fuenteTotal = wb.createFont();
        fuenteTotal.setBold(true);
        estiloTotal.setFont(fuenteTotal);
        estiloTotal.setFillForegroundColor(
            new XSSFColor(new byte[]{(byte)240,(byte)165,(byte)0}, null));
        estiloTotal.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloTotal.setDataFormat(
        	    fmt.getFormat("\"S/\" #,##0.00")
        	);

        Cell celdaLabelTotal = rowTotal.createCell(6);
        celdaLabelTotal.setCellValue("TOTAL GENERAL:");
        XSSFCellStyle estiloLabelTotal = wb.createCellStyle();
        estiloLabelTotal.setFont(fuenteTotal);
        estiloLabelTotal.setFillForegroundColor(
            new XSSFColor(new byte[]{(byte)240,(byte)165,(byte)0}, null));
        estiloLabelTotal.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        celdaLabelTotal.setCellStyle(estiloLabelTotal);

        Cell celdaTotal = rowTotal.createCell(7);
        celdaTotal.setCellValue(totalGeneral);
        celdaTotal.setCellStyle(estiloTotal);

        // ── Ancho de columnas ─────────────────────────────────
        sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 8000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 2500);
        sheet.setColumnWidth(6, 3500);
        sheet.setColumnWidth(7, 3500);
        sheet.setColumnWidth(8, 3500);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }

	// ══════════════════════════════════════════════════════════
	// EXCEL — PRODUCTOS
	// ══════════════════════════════════════════════════════════
	public byte[] exportarProductosExcel() throws Exception {

		List<Producto> productos = productoRepo.findAll();
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Productos");

		XSSFCellStyle estiloHeader = wb.createCellStyle();
		XSSFFont fuenteHeader = wb.createFont();
		fuenteHeader.setBold(true);
		fuenteHeader.setColor(IndexedColors.WHITE.getIndex());
		estiloHeader.setFont(fuenteHeader);
		estiloHeader.setFillForegroundColor(new XSSFColor(new byte[] { (byte) 26, (byte) 26, (byte) 46 }, null));
		estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloHeader.setAlignment(HorizontalAlignment.CENTER);

		XSSFCellStyle estiloTitulo = wb.createCellStyle();
		XSSFFont fuenteTitulo = wb.createFont();
		fuenteTitulo.setBold(true);
		fuenteTitulo.setFontHeightInPoints((short) 14);
		fuenteTitulo.setColor(IndexedColors.WHITE.getIndex());
		estiloTitulo.setFont(fuenteTitulo);
		estiloTitulo.setFillForegroundColor(new XSSFColor(new byte[] { (byte) 240, (byte) 165, (byte) 0 }, null));
		estiloTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estiloTitulo.setAlignment(HorizontalAlignment.CENTER);

		Row rowTitulo = sheet.createRow(0);
		rowTitulo.setHeightInPoints(28);
		Cell cellTit = rowTitulo.createCell(0);
		cellTit.setCellValue("REPORTE DE PRODUCTOS — STOCK ACTUAL");
		cellTit.setCellStyle(estiloTitulo);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

		Row rowFecha = sheet.createRow(1);
		rowFecha.createCell(0).setCellValue("Generado: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

		String[] headers = { "Código", "Descripción", "Tipo", "EAN", "Costo Unit.", "Stock", "Estado" };

		Row rowHeader = sheet.createRow(3);
		rowHeader.setHeightInPoints(20);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = rowHeader.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(estiloHeader);
		}

		int rowNum = 4;
		for (Producto p : productos) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(p.getIdProducto());
			row.createCell(1).setCellValue(p.getDescripcion());
			row.createCell(2).setCellValue(p.getTipo() != null ? p.getTipo().getNombre() : "-");
			row.createCell(3).setCellValue(p.getEanInt() != null ? p.getEanInt() : "-");
			row.createCell(4).setCellValue(p.getCostoUnitario() != null ? p.getCostoUnitario() : 0);
			row.createCell(5).setCellValue(p.getStockTotal() != null ? p.getStockTotal() : 0);
			row.createCell(6).setCellValue(p.getEstado() == 1 ? "Activo" : "Suspendido");
		}

		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(1, 9000);
		sheet.setColumnWidth(2, 3500);
		sheet.setColumnWidth(3, 3500);
		sheet.setColumnWidth(4, 3500);
		sheet.setColumnWidth(5, 2500);
		sheet.setColumnWidth(6, 3000);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wb.write(out);
		wb.close();
		return out.toByteArray();
	}

	// ══════════════════════════════════════════════════════════
	// PDF — INGRESOS
	// ══════════════════════════════════════════════════════════
	public byte[] exportarIngresosPdf() throws Exception {

		List<Ingreso> ingresos = ingresoRepo.findAll();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document doc = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(doc, out);
		doc.open();

		// ── Fuentes ───────────────────────────────────────────
		Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.WHITE);
		Font fuenteHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
		Font fuenteNormal = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.DARK_GRAY);
		Font fuenteTotal = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);

		BaseColor colorOscuro = new BaseColor(26, 26, 46);
		BaseColor colorDorado = new BaseColor(240, 165, 0);
		BaseColor colorGrisClaro = new BaseColor(248, 249, 250);

		// ── Titulo ────────────────────────────────────────────
		PdfPTable tablaTitulo = new PdfPTable(1);
		tablaTitulo.setWidthPercentage(100);
		PdfPCell celdaTitulo = new PdfPCell(new Phrase("REPORTE DE INGRESOS — SISTEMA ALMACÉN", fuenteTitulo));
		celdaTitulo.setBackgroundColor(colorDorado);
		celdaTitulo.setPadding(12);
		celdaTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		celdaTitulo.setBorder(Rectangle.NO_BORDER);
		tablaTitulo.addCell(celdaTitulo);
		doc.add(tablaTitulo);

		// ── Subtitulo fecha ───────────────────────────────────
		Font fuenteSub = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
		Paragraph fecha = new Paragraph("Generado: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()),
				fuenteSub);
		fecha.setAlignment(Element.ALIGN_RIGHT);
		fecha.setSpacingBefore(4);
		fecha.setSpacingAfter(12);
		doc.add(fecha);

		// ── Tabla de datos ────────────────────────────────────
		PdfPTable tabla = new PdfPTable(8);
		tabla.setWidthPercentage(100);
		tabla.setWidths(new float[] { 1f, 3f, 1.5f, 2.5f, 2f, 1f, 1.5f, 1.5f });

		String[] headers = { "#", "Producto", "Código", "Proveedor", "Sede", "Cant.", "Costo", "Total" };

		for (String h : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(h, fuenteHeader));
			cell.setBackgroundColor(colorOscuro);
			cell.setPadding(7);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBorderColor(BaseColor.GRAY);
			tabla.addCell(cell);
		}

		double totalGeneral = 0;
		boolean alterno = false;

		for (Ingreso ing : ingresos) {
			BaseColor colorFila = alterno ? colorGrisClaro : BaseColor.WHITE;
			alterno = !alterno;

			String[] valores = { String.valueOf(ing.getIdIngreso()), ing.getProducto().getDescripcion(),
					ing.getProducto().getIdProducto(),
					ing.getProveedor() != null ? ing.getProveedor().getNombre() : "-", ing.getSede().getNombre(),
					String.valueOf(ing.getCantidad()), "S/ " + String.format("%.2f", ing.getCostoUnitario()),
					"S/ " + String.format("%.2f", ing.getTotal()) };

			for (int i = 0; i < valores.length; i++) {
				PdfPCell cell = new PdfPCell(new Phrase(valores[i], fuenteNormal));
				cell.setBackgroundColor(colorFila);
				cell.setPadding(5);
				cell.setHorizontalAlignment(i >= 5 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
				cell.setBorderColor(new BaseColor(220, 220, 220));
				tabla.addCell(cell);
			}

			totalGeneral += ing.getTotal() != null ? ing.getTotal() : 0;
		}

		// ── Fila total ────────────────────────────────────────
		PdfPCell celdaEspacio = new PdfPCell(new Phrase(""));
		celdaEspacio.setColspan(6);
		celdaEspacio.setBorder(Rectangle.NO_BORDER);
		tabla.addCell(celdaEspacio);

		PdfPCell celdaLabelTotal = new PdfPCell(new Phrase("TOTAL:", fuenteTotal));
		celdaLabelTotal.setBackgroundColor(colorDorado);
		celdaLabelTotal.setPadding(7);
		celdaLabelTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		tabla.addCell(celdaLabelTotal);

		PdfPCell celdaTotal = new PdfPCell(new Phrase("S/ " + String.format("%.2f", totalGeneral), fuenteTotal));
		celdaTotal.setBackgroundColor(colorDorado);
		celdaTotal.setPadding(7);
		celdaTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		tabla.addCell(celdaTotal);

		doc.add(tabla);

		// ── Pie de pagina ─────────────────────────────────────
		Font fuentePie = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
		Paragraph pie = new Paragraph("Sistema de Gestión de Almacén — Empresa de Seguridad Privada", fuentePie);
		pie.setAlignment(Element.ALIGN_CENTER);
		pie.setSpacingBefore(20);
		doc.add(pie);

		doc.close();
		return out.toByteArray();
	}

	// ══════════════════════════════════════════════════════════
	// PDF — PRODUCTOS
	// ══════════════════════════════════════════════════════════
	public byte[] exportarProductosPdf() throws Exception {

		List<Producto> productos = productoRepo.findAll();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document doc = new Document(PageSize.A4);
		PdfWriter.getInstance(doc, out);
		doc.open();

		Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.WHITE);
		Font fuenteHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
		Font fuenteNormal = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.DARK_GRAY);

		BaseColor colorOscuro = new BaseColor(26, 26, 46);
		BaseColor colorDorado = new BaseColor(240, 165, 0);
		BaseColor colorGrisClaro = new BaseColor(248, 249, 250);
		BaseColor colorRojo = new BaseColor(220, 53, 69);
		BaseColor colorVerde = new BaseColor(25, 135, 84);

		PdfPTable tablaTitulo = new PdfPTable(1);
		tablaTitulo.setWidthPercentage(100);
		PdfPCell celdaTitulo = new PdfPCell(new Phrase("REPORTE DE STOCK — PRODUCTOS", fuenteTitulo));
		celdaTitulo.setBackgroundColor(colorDorado);
		celdaTitulo.setPadding(12);
		celdaTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		celdaTitulo.setBorder(Rectangle.NO_BORDER);
		tablaTitulo.addCell(celdaTitulo);
		doc.add(tablaTitulo);

		Font fuenteSub = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
		Paragraph fecha = new Paragraph("Generado: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()),
				fuenteSub);
		fecha.setAlignment(Element.ALIGN_RIGHT);
		fecha.setSpacingBefore(4);
		fecha.setSpacingAfter(12);
		doc.add(fecha);

		PdfPTable tabla = new PdfPTable(6);
		tabla.setWidthPercentage(100);
		tabla.setWidths(new float[] { 2f, 4f, 2f, 1.5f, 1.5f, 1.5f });

		String[] headers = { "Código", "Descripción", "Tipo", "Costo", "Stock", "Estado" };

		for (String h : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(h, fuenteHeader));
			cell.setBackgroundColor(colorOscuro);
			cell.setPadding(7);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			tabla.addCell(cell);
		}

		boolean alterno = false;
		for (Producto p : productos) {
			BaseColor colorFila = alterno ? colorGrisClaro : BaseColor.WHITE;
			alterno = !alterno;

			String[] valores = { p.getIdProducto(), p.getDescripcion(),
					p.getTipo() != null ? p.getTipo().getNombre() : "-",
					"S/ " + String.format("%.2f", p.getCostoUnitario() != null ? p.getCostoUnitario() : 0),
					String.valueOf(p.getStockTotal() != null ? p.getStockTotal() : 0),
					p.getEstado() == 1 ? "Activo" : "Suspendido" };

			for (int i = 0; i < valores.length; i++) {
				PdfPCell cell = new PdfPCell(new Phrase(valores[i], fuenteNormal));
				cell.setPadding(5);

				// Color especial para stock
				if (i == 4) {
					int stock = p.getStockTotal() != null ? p.getStockTotal() : 0;
					if (stock == 0) {
						cell.setBackgroundColor(new BaseColor(248, 215, 218));
					} else if (stock <= 3) {
						cell.setBackgroundColor(new BaseColor(255, 243, 205));
					} else {
						cell.setBackgroundColor(colorFila);
					}
				} else {
					cell.setBackgroundColor(colorFila);
				}

				cell.setBorderColor(new BaseColor(220, 220, 220));
				tabla.addCell(cell);
			}
		}

		doc.add(tabla);

		Font fuentePie = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
		Paragraph pie = new Paragraph("Sistema de Gestión de Almacén — Empresa de Seguridad Privada", fuentePie);
		pie.setAlignment(Element.ALIGN_CENTER);
		pie.setSpacingBefore(20);
		doc.add(pie);

		doc.close();
		return out.toByteArray();
	}
}