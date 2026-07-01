package com.sokoban.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Pantalla de inicio: titulo, un motivo (caja sobre destino) y los botones
 * Jugar / Salir. Sigue la estetica oscura del tablero y su paleta de colores.
 * No conoce el modelo: recibe los callbacks a ejecutar (Jugar / Salir).
 */
public class PanelInicio extends JPanel {

    private static final Color FONDO_TOP = new Color(36, 36, 36);
    private static final Color FONDO_BOT = new Color(16, 16, 16);
    private static final Color REJILLA = new Color(255, 255, 255, 12);
    private static final Color CAJA = new Color(174, 127, 73);
    private static final Color CAJA_BORDE = new Color(120, 86, 46);
    private static final Color DESTINO = new Color(233, 196, 70);
    private static final Color TITULO = new Color(233, 196, 70);
    private static final Color SUBTITULO = new Color(185, 185, 185);
    private static final Color VERDE = new Color(84, 150, 58);
    private static final Color AZUL = new Color(52, 101, 164);
    private static final Color GRIS = new Color(95, 95, 95);
    private static final Color ROJO = new Color(150, 52, 48);

    public PanelInicio(Runnable alJugar, Runnable alNiveles, Runnable alInstrucciones, Runnable alSalir) {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;

        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 4, 0);
        add(new IconoCaja(), gc);

        JLabel titulo = new JLabel("SOKOBAN");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 68));
        titulo.setForeground(TITULO);
        gc.gridy = 1;
        gc.insets = new Insets(8, 0, 0, 0);
        add(titulo, gc);

        JLabel sub = new JLabel("Empujá cada caja hasta su destino");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 20));
        sub.setForeground(SUBTITULO);
        gc.gridy = 2;
        gc.insets = new Insets(2, 0, 30, 0);
        add(sub, gc);

        JButton jugar = crearBoton("Jugar", VERDE);
        jugar.addActionListener(e -> alJugar.run());
        gc.gridy = 3;
        gc.insets = new Insets(8, 0, 8, 0);
        add(jugar, gc);

        JButton niveles = crearBoton("Niveles", AZUL);
        niveles.addActionListener(e -> alNiveles.run());
        gc.gridy = 4;
        add(niveles, gc);

        JButton instrucciones = crearBoton("Instrucciones", GRIS);
        instrucciones.addActionListener(e -> alInstrucciones.run());
        gc.gridy = 5;
        add(instrucciones, gc);

        JButton salir = crearBoton("Salir", ROJO);
        salir.addActionListener(e -> alSalir.run());
        gc.gridy = 6;
        add(salir, gc);
    }

    private JButton crearBoton(String texto, Color base) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fondo = getModel().isRollover() ? base.brighter() : base;
                g.setColor(fondo);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g.setColor(base.darker());
                g.setStroke(new BasicStroke(2));
                g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
                g.dispose();
                super.paintComponent(g0);
            }
        };
        boton.setFont(new Font("SansSerif", Font.BOLD, 24));
        boton.setForeground(Color.WHITE);
        boton.setHorizontalAlignment(SwingConstants.CENTER);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setPreferredSize(new Dimension(300, 58));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        g.setPaint(new GradientPaint(0, 0, FONDO_TOP, 0, h, FONDO_BOT));
        g.fillRect(0, 0, w, h);
        g.setColor(REJILLA);
        for (int x = 0; x < w; x += 46) {
            g.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += 46) {
            g.drawLine(0, y, w, y);
        }
        g.dispose();
    }

    /** Motivo decorativo: un destino (rombo dorado) con una caja encima. */
    private static final class IconoCaja extends JComponent {

        IconoCaja() {
            setPreferredSize(new Dimension(170, 170));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int s = Math.min(getWidth(), getHeight());
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            int d = (int) (s * 0.36);
            Polygon rombo = new Polygon(
                    new int[]{cx, cx + d, cx, cx - d},
                    new int[]{cy - d, cy, cy + d, cy}, 4);
            g.setColor(DESTINO);
            g.fillPolygon(rombo);

            int b = (int) (s * 0.52);
            int x = cx - b / 2;
            int y = cy - b / 2;
            g.setColor(CAJA);
            g.fillRoundRect(x, y, b, b, 16, 16);
            g.setColor(CAJA_BORDE);
            g.setStroke(new BasicStroke(4));
            g.drawRoundRect(x, y, b, b, 16, 16);
            g.drawLine(x + 10, y + 10, x + b - 10, y + b - 10);
            g.drawLine(x + b - 10, y + 10, x + 10, y + b - 10);
            g.dispose();
        }
    }
}
