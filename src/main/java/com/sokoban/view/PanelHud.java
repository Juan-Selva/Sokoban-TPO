package com.sokoban.view;

import com.sokoban.controller.Controlador;
import com.sokoban.partida.EstadoJuego;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
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
    private final JLabel etiquetaTiempo = new JLabel();
    private final JButton botonUndo = new JButton("Deshacer");

    public PanelHud(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        Font fuente = new Font("SansSerif", Font.BOLD, 14);
        for (JLabel etiqueta : new JLabel[]{etiquetaNivel, etiquetaMovimientos, etiquetaEmpujes, etiquetaUndos, etiquetaTiempo}) {
            etiqueta.setFont(fuente);
            add(etiqueta);
        }

        JButton botonReiniciar = new JButton("Reiniciar");
        botonUndo.addActionListener(e -> controlador.deshacer());
        botonReiniciar.addActionListener(e -> controlador.reiniciar());
        add(botonUndo);
        add(botonReiniciar);

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

        int tiempo = controlador.getTiempoRestante();
        etiquetaTiempo.setText(tiempo >= 0 ? "Tiempo: " + tiempo + "s" : "");
    }
}
