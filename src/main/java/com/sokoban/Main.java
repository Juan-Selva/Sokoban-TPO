package com.sokoban;

import com.sokoban.controller.Controlador;
import com.sokoban.view.VentanaJuego;
import javax.swing.SwingUtilities;

/** Punto de entrada: arma MVC y lanza la ventana Swing. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Controlador controlador = new Controlador();
            VentanaJuego ventana = new VentanaJuego(controlador);
            controlador.iniciar();
            ventana.setVisible(true);
        });
    }
}
