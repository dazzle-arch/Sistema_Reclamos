package com.sistemareclamos.api.reportes;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.List;

public final class GraficoReporte {

    private static final int MAX_BARRAS = 12;
    private static final Color AZUL = new Color(0x0F, 0x75, 0xB8);

    private GraficoReporte() {}

    /** Devuelve null si no hay nada que graficar. */
    public static BufferedImage barras(List<String> etiquetas, List<Double> valores) {
        if (etiquetas.isEmpty() || valores.stream().allMatch(v -> v == 0)) return null;

        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        int n = Math.min(MAX_BARRAS, etiquetas.size());
        for (int i = 0; i < n; i++) {
            datos.addValue(valores.get(i), "Cantidad", recortar(etiquetas.get(i)));
        }

        JFreeChart grafico = ChartFactory.createBarChart(null, null, null, datos,
                PlotOrientation.VERTICAL, false, false, false);
        grafico.setBackgroundPaint(Color.WHITE);
        grafico.setAntiAlias(true);

        CategoryPlot plot = grafico.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(0xD0, 0xD7, 0xDE));
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        ((NumberAxis) plot.getRangeAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        BarRenderer r = (BarRenderer) plot.getRenderer();
        r.setBarPainter(new StandardBarPainter());
        r.setShadowVisible(false);
        r.setSeriesPaint(0, AZUL);
        r.setMaximumBarWidth(0.12);
        r.setDefaultItemLabelsVisible(true);
        r.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",
                new java.text.DecimalFormat("0")));

        return grafico.createBufferedImage(900, 360);
    }

    private static String recortar(String s) {
        return s.length() > 22 ? s.substring(0, 21) + "…" : s;
    }
}