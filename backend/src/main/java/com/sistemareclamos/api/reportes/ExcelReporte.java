package com.sistemareclamos.api.reportes;

import com.sistemareclamos.api.reportes.DatosReporte.Col;
import com.sistemareclamos.api.reportes.DatosReporte.Tipo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ExcelReporte {

    private static final byte[] AZUL = {(byte) 0x0F, (byte) 0x75, (byte) 0xB8};
    private static final byte[] AZUL_OSCURO = {(byte) 0x0B, (byte) 0x4F, (byte) 0x7A};
    private static final byte[] AZUL_CLARO = {(byte) 0xEA, (byte) 0xF3, (byte) 0xFA};
    private static final int FILA_CABECERA = 5;

    public byte[] generar(DatosReporte datos, String generadoPor, String fechaHora) {
        try (XSSFWorkbook libro = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet hoja = libro.createSheet("Reporte");
            int cols = datos.columnas().size();

            // Título y datos generales
            XSSFCellStyle estTitulo = estilo(libro, null, 16, true, AZUL_OSCURO, null, false);
            XSSFCellStyle estMeta = estilo(libro, null, 10, false, new byte[]{0x5F, 0x6B, 0x76}, null, false);
            XSSFCellStyle estTotal = estilo(libro, null, 12, true, AZUL_OSCURO, AZUL_CLARO, false);

            fila(hoja, 0, datos.titulo(), estTitulo, 26);
            fila(hoja, 1, datos.filtros(), estMeta, 16);
            fila(hoja, 2, "Generado por " + generadoPor + "  ·  " + fechaHora + "  ·  Documento confidencial", estMeta, 16);
            fila(hoja, 3, datos.etiquetaTotal() + ": " + datos.total(), estTotal, 22);
            if (cols > 1) {
                for (int r = 0; r <= 3; r++) hoja.addMergedRegion(new CellRangeAddress(r, r, 0, cols - 1));
            }

            // Cabecera de la tabla
            XSSFCellStyle estCab = estilo(libro, null, 10, true, new byte[]{(byte) 255, (byte) 255, (byte) 255}, AZUL, true);
            estCab.setAlignment(HorizontalAlignment.CENTER);
            Row cab = hoja.createRow(FILA_CABECERA);
            cab.setHeightInPoints(22);
            int[] ancho = new int[cols];
            for (int i = 0; i < cols; i++) {
                Cell c = cab.createCell(i);
                c.setCellValue(datos.columnas().get(i).titulo());
                c.setCellStyle(estCab);
                ancho[i] = datos.columnas().get(i).titulo().length();
            }

            // Filas (celdas de texto, números y porcentajes reales)
            DataFormat fmt = libro.createDataFormat();
            XSSFCellStyle[][] estilos = new XSSFCellStyle[3][2];
            for (Tipo t : Tipo.values()) {
                for (int z = 0; z < 2; z++) {
                    XSSFCellStyle s = estilo(libro, null, 10, false, new byte[]{0, 0, 0}, z == 1 ? AZUL_CLARO : null, true);
                    if (t == Tipo.ENTERO) s.setDataFormat(fmt.getFormat("#,##0"));
                    if (t == Tipo.PORCENTAJE) s.setDataFormat(fmt.getFormat("0.0\"%\""));
                    if (t != Tipo.TEXTO) s.setAlignment(HorizontalAlignment.RIGHT);
                    estilos[t.ordinal()][z] = s;
                }
            }
            int n = 0;
            for (Object[] f : datos.filas()) {
                Row row = hoja.createRow(FILA_CABECERA + 1 + n);
                for (int i = 0; i < cols; i++) {
                    Col col = datos.columnas().get(i);
                    Cell c = row.createCell(i);
                    c.setCellStyle(estilos[col.tipo().ordinal()][n % 2]);
                    if (col.tipo() == Tipo.TEXTO) {
                        c.setCellValue(String.valueOf(f[i]));   // siempre texto: nunca se interpreta como fórmula
                        ancho[i] = Math.max(ancho[i], String.valueOf(f[i]).length());
                    } else {
                        c.setCellValue(((Number) f[i]).doubleValue());
                    }
                }
                n++;
            }

            // Ajustes finales
            for (int i = 0; i < cols; i++) {
                hoja.setColumnWidth(i, Math.min(60, Math.max(14, ancho[i] + 4)) * 256);
            }
            hoja.createFreezePane(0, FILA_CABECERA + 1);
            hoja.setAutoFilter(new CellRangeAddress(FILA_CABECERA, FILA_CABECERA + Math.max(1, n), 0, cols - 1));
            hoja.setDisplayGridlines(false);
            hoja.getPrintSetup().setLandscape(true);
            hoja.setFitToPage(true);

            libro.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el Excel", e);
        }
    }

    private static void fila(XSSFSheet hoja, int indice, String texto, XSSFCellStyle estilo, float alto) {
        Row r = hoja.createRow(indice);
        r.setHeightInPoints(alto);
        Cell c = r.createCell(0);
        c.setCellValue(texto);
        c.setCellStyle(estilo);
    }

    private static XSSFCellStyle estilo(XSSFWorkbook libro, String ignorado, int tam, boolean negrita,
                                        byte[] colorTexto, byte[] fondo, boolean borde) {
        XSSFFont fuente = libro.createFont();
        fuente.setFontHeightInPoints((short) tam);
        fuente.setBold(negrita);
        fuente.setColor(new XSSFColor(colorTexto, null));
        XSSFCellStyle s = libro.createCellStyle();
        s.setFont(fuente);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        if (fondo != null) {
            s.setFillForegroundColor(new XSSFColor(fondo, null));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (borde) {
            s.setBorderBottom(BorderStyle.THIN);
            s.setBottomBorderColor(new XSSFColor(new byte[]{(byte) 0xD0, (byte) 0xD7, (byte) 0xDE}, null));
        }
        return s;
    }
}