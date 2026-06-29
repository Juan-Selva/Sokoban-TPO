package com.sokoban.view;

import com.sokoban.controller.Controlador;
import com.sokoban.dominio.Entidad;
import com.sokoban.dominio.Item;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.Tablero;
import com.sokoban.nivel.Nivel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Dibuja el tablero. Para cada posicion pide la clave de presentacion de la celda
 * y de la entidad (si hay) y la traduce con la Paleta: nunca hace instanceof.
 * Si el nivel tiene vision limitada, oculta las celdas fuera del radio (R20-R21:
 * es solo presentacion, la logica no cambia).
 *
 * El tamano de celda es dinamico: se calcula para llenar el panel y centrar el
 * tablero, de modo que la ventana se puede redimensionar y todo escala.
 */
public class PanelTablero extends JPanel {

    /** Tamano de celda para el tamano "natural" inicial de la ventana. */
    private static final int TILE_BASE = 72;
    /** Los items se dibujan a este porcentaje de la celda (un poco mas chicos). */
    private static final float ESCALA_ITEM = 0.95f;
    private static final Color FONDO = new Color(28, 28, 28);
    private static final Color OCULTO = new Color(18, 18, 18);

    private final transient Controlador controlador;

    public PanelTablero(Controlador controlador) {
        this.controlador = controlador;
        setBackground(FONDO);
    }

    public void ajustarTamano() {
        Tablero tablero = tableroActual();
        if (tablero == null) {
            return;
        }
        setPreferredSize(new Dimension(tablero.getColumnas() * TILE_BASE, tablero.getFilas() * TILE_BASE));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Tablero tablero = tableroActual();
        if (tablero == null) {
            return;
        }

        int columnas = tablero.getColumnas();
        int filas = tablero.getFilas();
        int tile = Math.max(1, Math.min(getWidth() / columnas, getHeight() / filas));
        int offsetX = (getWidth() - tile * columnas) / 2;
        int offsetY = (getHeight() - tile * filas) / 2;

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, tile / 2)));

        Nivel nivel = controlador.getNivelActual();
        Posicion posJugador = tablero.getJugador().getPosicion();

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                Posicion posicion = new Posicion(fila, columna);
                int x = offsetX + columna * tile;
                int y = offsetY + fila * tile;

                if (estaOculta(nivel, posJugador, posicion)) {
                    dibujarOculto(g, x, y, tile);
                    continue;
                }

                int tileItem = Math.round(tile * ESCALA_ITEM);
                int inset = (tile - tileItem) / 2;
                int ix = x + inset;
                int iy = y + inset;

                dibujarTile(g, tablero.celdaEn(posicion).clavePresentacion(), ix, iy, tileItem);

                Item item = tablero.itemEn(posicion);
                if (item != null) {
                    dibujarFicha(g, item.clavePresentacion(), "", ix, iy, tileItem);
                }

                Entidad entidad = tablero.entidadEn(posicion);
                if (entidad != null) {
                    dibujarEntidad(g, entidad, ix, iy, tileItem);
                }
            }
        }
        g.dispose();
    }

    private Tablero tableroActual() {
        if (controlador.getJuego() == null) {
            return null;
        }
        return controlador.getJuego().getTablero();
    }

    private boolean estaOculta(Nivel nivel, Posicion jugador, Posicion posicion) {
        if (nivel == null || !nivel.tieneVisionLimitada()) {
            return false;
        }
        int distancia = Math.max(
                Math.abs(jugador.getFila() - posicion.getFila()),
                Math.abs(jugador.getColumna() - posicion.getColumna()));
        return distancia > nivel.getRadioVision();
    }

    private void dibujarOculto(Graphics2D g, int x, int y, int tile) {
        g.setColor(OCULTO);
        g.fillRect(x, y, tile, tile);
    }

    private void dibujarTile(Graphics2D g, String clave, int x, int y, int tile) {
        Image sprite = PaletaPresentacion.imagen(clave);
        if (sprite != null) {
            g.drawImage(sprite, x, y, tile, tile, null);
            return;
        }
        g.setColor(PaletaPresentacion.color(clave));
        g.fillRect(x, y, tile, tile);
        g.setColor(new Color(0, 0, 0, 40));
        g.drawRect(x, y, tile, tile);

        char letra = PaletaPresentacion.letra(clave);
        if (letra != ' ') {
            g.setColor(new Color(0, 0, 0, 90));
            dibujarTextoCentrado(g, String.valueOf(letra), x, y, tile);
        }
    }

    private void dibujarEntidad(Graphics2D g, Entidad entidad, int x, int y, int tile) {
        dibujarFicha(g, entidad.clavePresentacion(), entidad.etiqueta(), x, y, tile);
    }

    /** Dibuja una "ficha" (entidad o item) con su sprite o, si falta, color + letra. */
    private void dibujarFicha(Graphics2D g, String clave, String etiqueta, int x, int y, int tile) {
        Image sprite = PaletaPresentacion.imagen(clave);
        if (sprite != null) {
            g.drawImage(sprite, x, y, tile, tile, null);
            dibujarEtiqueta(g, etiqueta, x, y, tile);
            return;
        }

        int margen = tile / 8;
        int arco = tile / 5;
        g.setColor(PaletaPresentacion.color(clave));
        g.fillRoundRect(x + margen, y + margen, tile - 2 * margen, tile - 2 * margen, arco, arco);
        g.setColor(new Color(0, 0, 0, 120));
        g.drawRoundRect(x + margen, y + margen, tile - 2 * margen, tile - 2 * margen, arco, arco);

        String texto = etiqueta;
        if (texto.isEmpty()) {
            texto = String.valueOf(PaletaPresentacion.letra(clave));
        }
        dibujarEtiqueta(g, texto, x, y, tile);
    }

    /** Texto opcional sobre la entidad (p. ej. la resistencia de la caja fragil). */
    private void dibujarEtiqueta(Graphics2D g, String texto, int x, int y, int tile) {
        if (texto.isEmpty()) {
            return;
        }
        g.setColor(new Color(30, 30, 30));
        dibujarTextoCentrado(g, texto, x, y, tile);
    }

    private void dibujarTextoCentrado(Graphics2D g, String texto, int x, int y, int tile) {
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (tile - fm.stringWidth(texto)) / 2;
        int ty = y + (tile - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(texto, tx, ty);
    }
}
