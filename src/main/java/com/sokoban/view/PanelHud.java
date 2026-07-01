package com.sokoban.view;

import com.sokoban.controller.Controlador;
import com.sokoban.partida.EstadoJuego;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * HUD: nivel, movimientos, empujes, tiempo restante (si aplica) y botones de
 * deshacer y reiniciar. Lee el estado a traves del controlador (fachada Juego).
 */
public class PanelHud extends JPanel {

    private final transient Controlador controlador;

    private final JLabel etiquetaNivel = new JLabel();
    private final JLabel etiquetaMovimientos = new JLabel();
    private final JLabel etiquetaEmpujes = new JLabel();
    private final JLabel etiquetaUndos = new JLabel();
    private final JLabel etiquetaEnergia = new JLabel();
    private final JButton botonUndo = new JButton("Deshacer");

    public PanelHud(Controlador controlador) {
        this.controlador = controlador;
        // Dos filas apiladas (info arriba, botones abajo): asi la altura del HUD
        // es determinística y no se superpone con el tablero aunque el texto sea largo.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        Font fuente = new Font("SansSerif", Font.BOLD, 14);
        JPanel filaInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 2));
        filaInfo.setOpaque(false);
        filaInfo.setAlignmentX(LEFT_ALIGNMENT);
        for (JLabel etiqueta : new JLabel[]{etiquetaNivel, etiquetaMovimientos, etiquetaEmpujes, etiquetaUndos, etiquetaEnergia}) {
            etiqueta.setFont(fuente);
            filaInfo.add(etiqueta);
        }

        JButton botonReiniciar = new JButton("Reiniciar");
        botonUndo.addActionListener(e -> controlador.deshacer());
        botonReiniciar.addActionListener(e -> controlador.reiniciar());
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filaBotones.setOpaque(false);
        filaBotones.setAlignmentX(LEFT_ALIGNMENT);
        filaBotones.add(botonUndo);
        filaBotones.add(botonReiniciar);

        add(filaInfo);
        add(filaBotones);

        refrescar();
    }

    public void refrescar() {
        if (controlador.getJuego() == null) {
            return;
        }
        EstadoJuego estado = controlador.getJuego().getEstadoJuego();
        etiquetaNivel.setText("Nivel: " + controlador.getNumeroNivel());
        etiquetaMovimientos.setText("Movimientos: " + estado.getMovimientos());
        etiquetaEmpujes.setText("Empujes: " + estado.getEmpujes());
        etiquetaUndos.setText("Undos restantes: " + estado.getUndosDisponibles());

        // El boton refleja directamente el predicado del modelo (sin condicionales).
        botonUndo.setEnabled(estado.puedeUndo());

        var jugador = controlador.getJuego().getTablero().getJugador();
        etiquetaEnergia.setText("Energia: " + jugador.getEnergia() + "/" + jugador.getEnergiaMaxima());
    }
}
