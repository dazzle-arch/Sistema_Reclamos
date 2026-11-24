package com.sistemareclamos.api.reportes;

import com.sistemareclamos.api.reportes.DatosReporte.Col;
import com.sistemareclamos.api.reportes.DatosReporte.Tipo;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PdfReporte {

    private static final Color AZUL = new Color(0x0F, 0x75, 0xB8);
    private static final Color AZUL_OSCURO = new Color(0x0B, 0x4F, 0x7A);
    private static final Color AZUL_CLARO = new Color(0xEA, 0xF3, 0xFA);
    private static final Color GRIS = new Color(0x5F, 0x6B, 0x76);
    private static final String FUENTE = "DejaVu Sans";
    private static final Locale PE = Locale.of("es", "PE");

    private final Map<String, JasperReport> compilados = new ConcurrentHashMap<>();
    private final Image logo = cargarLogo();

    public byte[] generar(DatosReporte datos, String generadoPor, String fechaHora) {
        try {
            JasperReport reporte = compilados.computeIfAbsent(datos.clave(), k -> compilar(datos));

            Map<String, Object> p = new HashMap<>();
            p.put("LOGO", logo);
            p.put("GRAFICO", GraficoReporte.barras(datos.etiquetasGrafico(), datos.valoresGrafico()));
            p.put("TITULO", datos.titulo());
            p.put("FILTROS", datos.filtros());
            p.put("ETIQUETA_TOTAL", datos.etiquetaTotal() + ":  " + String.format(PE, "%,d", datos.total()));
            p.put("TITULO_GRAFICO", datos.titulo() + " (gráfico)");
            p.put("SIN_DATOS", datos.filas().isEmpty() || datos.total() == 0
                    ? "No hay reclamos en el período seleccionado." : "");
            p.put("GENERADO", "Documento confidencial  ·  Generado por " + generadoPor + "  ·  " + fechaHora);

            List<Map<String, ?>> filas = new ArrayList<>();
            for (Object[] fila : datos.filas()) {
                Map<String, Object> m = new HashMap<>();
                for (int i = 0; i < datos.columnas().size(); i++) {
                    m.put("c" + i, formatear(fila[i], datos.columnas().get(i).tipo()));
                }
                filas.add(m);
            }

            JasperPrint print = JasperFillManager.fillReport(reporte, p, new JRMapCollectionDataSource(filas));
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException e) {
            throw new IllegalStateException("No se pudo generar el PDF", e);
        }
    }

    // ---------- diseño de la plantilla ----------
    private JasperReport compilar(DatosReporte datos) {
        try {
            JasperDesign d = new JasperDesign();
            d.setName("reporte_" + datos.clave());
            d.setPageWidth(595);
            d.setPageHeight(842);
            d.setColumnWidth(515);
            d.setLeftMargin(40);
            d.setRightMargin(40);
            d.setTopMargin(36);
            d.setBottomMargin(36);
            d.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

            parametro(d, "LOGO", Image.class);
            parametro(d, "GRAFICO", Image.class);
            for (String n : List.of("TITULO", "FILTROS", "ETIQUETA_TOTAL", "TITULO_GRAFICO", "SIN_DATOS", "GENERADO")) {
                parametro(d, n, String.class);
            }
            for (int i = 0; i < datos.columnas().size(); i++) {
                JRDesignField f = new JRDesignField();
                f.setName("c" + i);
                f.setValueClass(String.class);
                d.addField(f);
            }

            // Estilo de filas alternadas
            JRDesignStyle fila = new JRDesignStyle();
            fila.setName("fila");
            fila.setMode(ModeEnum.OPAQUE);
            fila.setBackcolor(Color.WHITE);
            JRDesignConditionalStyle par = new JRDesignConditionalStyle();
            par.setConditionExpression(expr("Boolean.valueOf($V{REPORT_COUNT}.intValue() % 2 == 0)", Boolean.class));
            par.setMode(ModeEnum.OPAQUE);
            par.setBackcolor(AZUL_CLARO);
            fila.addConditionalStyle(par);
            d.addStyle(fila);

            // Título: logo, nombre del reporte, filtros y total
            JRDesignBand titulo = new JRDesignBand();
            titulo.setHeight(146);
            JRDesignImage img = new JRDesignImage(d);
            img.setX(0); img.setY(0); img.setWidth(70); img.setHeight(80);
            img.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);
            img.setOnErrorType(OnErrorTypeEnum.BLANK);
            img.setExpression(expr("$P{LOGO}", Image.class));
            titulo.addElement(img);
            titulo.addElement(campo("$P{TITULO}", 85, 6, 430, 30, 18, true, AZUL_OSCURO, HorizontalTextAlignEnum.LEFT));
            titulo.addElement(texto("Sistema de Reclamos  ·  Municipalidad Provincial de Arequipa",
                    85, 38, 430, 16, 9, false, GRIS));
            titulo.addElement(campo("$P{FILTROS}", 85, 56, 430, 16, 9, false, GRIS, HorizontalTextAlignEnum.LEFT));
            JRDesignLine linea = new JRDesignLine(d);
            linea.setX(0); linea.setY(90); linea.setWidth(515); linea.setHeight(1);
            linea.setForecolor(AZUL);
            linea.getLinePen().setLineWidth(2f);
            titulo.addElement(linea);
            JRDesignTextField total = campo("$P{ETIQUETA_TOTAL}", 0, 104, 515, 30, 12, true, AZUL_OSCURO,
                    HorizontalTextAlignEnum.LEFT);
            total.setMode(ModeEnum.OPAQUE);
            total.setBackcolor(AZUL_CLARO);
            total.getLineBox().setLeftPadding(10);
            titulo.addElement(total);
            d.setTitle(titulo);

            // Cabecera de la tabla
            JRDesignBand cabecera = new JRDesignBand();
            cabecera.setHeight(24);
            int x = 0;
            for (Col c : datos.columnas()) {
                JRDesignStaticText t = texto(c.titulo(), x, 0, c.ancho(), 24, 9, true, Color.WHITE);
                t.setMode(ModeEnum.OPAQUE);
                t.setBackcolor(AZUL);
                t.setHorizontalTextAlign(c.tipo() == Tipo.TEXTO ? HorizontalTextAlignEnum.LEFT : HorizontalTextAlignEnum.RIGHT);
                t.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
                t.getLineBox().setLeftPadding(6);
                t.getLineBox().setRightPadding(6);
                cabecera.addElement(t);
                x += c.ancho();
            }
            d.setColumnHeader(cabecera);

            // Filas
            JRDesignBand detalle = new JRDesignBand();
            detalle.setHeight(20);
            x = 0;
            for (int i = 0; i < datos.columnas().size(); i++) {
                Col c = datos.columnas().get(i);
                JRDesignTextField t = campo("$F{c" + i + "}", x, 0, c.ancho(), 20, 9, false, Color.BLACK,
                        c.tipo() == Tipo.TEXTO ? HorizontalTextAlignEnum.LEFT : HorizontalTextAlignEnum.RIGHT);
                t.setStyle(fila);
                t.getLineBox().setLeftPadding(6);
                t.getLineBox().setRightPadding(6);
                detalle.addElement(t);
                x += c.ancho();
            }
            ((JRDesignSection) d.getDetailSection()).addBand(detalle);

            // Resumen: aviso de "sin datos" y gráfico
            JRDesignBand resumen = new JRDesignBand();
            resumen.setHeight(280);
            resumen.addElement(campo("$P{SIN_DATOS}", 0, 8, 515, 18, 10, true, new Color(0xB0, 0x2A, 0x37),
                    HorizontalTextAlignEnum.LEFT));
            resumen.addElement(campo("$P{TITULO_GRAFICO}", 0, 34, 515, 16, 10, true, AZUL_OSCURO,
                    HorizontalTextAlignEnum.LEFT));
            JRDesignImage graf = new JRDesignImage(d);
            graf.setX(0); graf.setY(54); graf.setWidth(515); graf.setHeight(212);
            graf.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);
            graf.setOnErrorType(OnErrorTypeEnum.BLANK);
            graf.setExpression(expr("$P{GRAFICO}", Image.class));
            resumen.addElement(graf);
            d.setSummary(resumen);

            // Pie de página
            JRDesignBand pie = new JRDesignBand();
            pie.setHeight(26);
            JRDesignLine lineaPie = new JRDesignLine(d);
            lineaPie.setX(0); lineaPie.setY(2); lineaPie.setWidth(515); lineaPie.setHeight(1);
            lineaPie.setForecolor(AZUL);
            pie.addElement(lineaPie);
            pie.addElement(campo("$P{GENERADO}", 0, 6, 400, 16, 8, false, GRIS, HorizontalTextAlignEnum.LEFT));
            pie.addElement(campo("\"Página \" + $V{PAGE_NUMBER}", 400, 6, 80, 16, 8, false, GRIS,
                    HorizontalTextAlignEnum.RIGHT));
            JRDesignTextField totalPaginas = campo("\" de \" + $V{PAGE_NUMBER}", 480, 6, 35, 16, 8, false, GRIS,
                    HorizontalTextAlignEnum.LEFT);
            totalPaginas.setEvaluationTime(EvaluationTimeEnum.REPORT);
            pie.addElement(totalPaginas);
            d.setPageFooter(pie);

            return JasperCompileManager.compileReport(d);
        } catch (JRException e) {
            throw new IllegalStateException("No se pudo compilar la plantilla del reporte", e);
        }
    }

    // ---------- ayudas ----------
    private static void parametro(JasperDesign d, String nombre, Class<?> clase) throws JRException {
        JRDesignParameter p = new JRDesignParameter();
        p.setName(nombre);
        p.setValueClass(clase);
        d.addParameter(p);
    }

    private static JRDesignExpression expr(String texto, Class<?> clase) {
        JRDesignExpression e = new JRDesignExpression(texto);
        return e;
    }

    private static JRDesignTextField campo(String expresion, int x, int y, int w, int h, float tam,
                                           boolean negrita, Color color, HorizontalTextAlignEnum alin) {
        JRDesignTextField t = new JRDesignTextField();
        t.setX(x); t.setY(y); t.setWidth(w); t.setHeight(h);
        t.setExpression(expr(expresion, String.class));
        t.setFontName(FUENTE);
        t.setFontSize(tam);
        t.setBold(negrita);
        t.setForecolor(color);
        t.setHorizontalTextAlign(alin);
        t.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        t.setBlankWhenNull(true);
        return t;
    }

    private static JRDesignStaticText texto(String contenido, int x, int y, int w, int h, float tam,
                                            boolean negrita, Color color) {
        JRDesignStaticText t = new JRDesignStaticText();
        t.setX(x); t.setY(y); t.setWidth(w); t.setHeight(h);
        t.setText(contenido);
        t.setFontName(FUENTE);
        t.setFontSize(tam);
        t.setBold(negrita);
        t.setForecolor(color);
        t.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
        return t;
    }

    private static String formatear(Object v, Tipo tipo) {
        if (v == null) return "";
        return switch (tipo) {
            case ENTERO -> String.format(PE, "%,d", ((Number) v).longValue());
            case PORCENTAJE -> String.format(PE, "%.1f%%", ((Number) v).doubleValue());
            default -> String.valueOf(v);
        };
    }

    /** Si el logo no existe, el reporte sale igual, sin imagen. */
    private static Image cargarLogo() {
        try (InputStream in = new ClassPathResource("reportes/logo-municipalidad.png").getInputStream()) {
            BufferedImage img = ImageIO.read(in);
            // El archivo original es 1920x1080 con mucho margen blanco: se recorta al escudo y al nombre
            if (img.getWidth() == 1920 && img.getHeight() == 1080) {
                return img.getSubimage(590, 120, 740, 840);
            }
            return img;
        } catch (Exception e) {
            return null;
        }
    }
}