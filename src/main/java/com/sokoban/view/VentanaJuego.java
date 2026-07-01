package com.sokoban.view;

import com.sokoban.controller.Controlador;
import com.sokoban.dominio.Direccion;
import com.sokoban.observer.Observer;
import com.sokoban.partida.ResultadoNivel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 * Ventana principal. Es Observer del Juego: cuando el modelo cambia, repinta.
 * Traduce las teclas a llamadas del controlador (que crea los Command). No
 * conoce el modelo en detalle: pasa por el controlador y la fachada.
 */
public class VentanaJuego extends JFrame implements Observer {

    private final transient Controlador controlador;
    private final PanelTablero panelTablero;
    private final PanelHud panelHud;
    private final transient ReproductorSonidos sonidos = new ReproductorSonidos();

    public VentanaJuego(Controlador controlador) {
        this.controlador = controlador;
        controlador.setVista(this);

        setTitle("Sokoban");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(480, 360));

        this.panelHud = new PanelHud(controlador);
        this.panelTablero = new PanelTablero(controlador);
        add(panelHud, BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);

        registrarTeclas();
    }

    /** Notificacion del modelo (Observer): repintar tablero y HUD. */
    @Override
    public void actualizar() {
        panelTablero.repaint();
        panelHud.refrescar();
    }

    /** Reconstruye el layout cuando cambia el nivel (tamano del tablero). */
    public void refrescarNivel() {
        // Refrescar el HUD antes de empaquetar: asi pack() dimensiona la ventana
        // con los textos ya cargados y el HUD no se superpone con el tablero.
        panelHud.refrescar();
        panelTablero.ajustarTamano();
        pack();
        setLocationRelativeTo(null);
        panelTablero.repaint();
    }

    public void mostrarVictoria(ResultadoNivel resultado, boolean hayMasNiveles) {
        String estrellas = "★".repeat(resultado.getEstrellas())
                + "☆".repeat(3 - resultado.getEstrellas());
        String cierre = hayMasNiveles
                ? "Pasamos al siguiente nivel."
                : "¡Completaste todos los niveles!";
        String mensaje = "Nivel completado   " + estrellas + "\n\n"
                + "Movimientos: " + resultado.getMovimientos() + "\n"
                + "Empujes: " + resultado.getEmpujes() + "\n"
                + "Undos usados: " + resultado.getUndos() + "\n"
                + "Puntaje: " + resultado.getScore() + "\n\n"
                + cierre;
        JOptionPane.showMessageDialog(this, mensaje, "Victoria", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarDerrota() {
        JOptionPane.showOptionDialog(
                this,
                "Perdiste el nivel.",
                "Perdiste",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                new Object[] { "Reiniciar" },
                "Reiniciar");
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Reproduce el efecto asociado a un evento de juego (best-effort). */
    public void reproducirSonido(String evento) {
        sonidos.reproducir(evento);
    }

    private void registrarTeclas() {
        vincular("UP", () -> controlador.mover(Direccion.ARRIBA));
        vincular("DOWN", () -> controlador.mover(Direccion.ABAJO));
        vincular("LEFT", () -> controlador.mover(Direccion.IZQUIERDA));
        vincular("RIGHT", () -> controlador.mover(Direccion.DERECHA));

        vincular("W", () -> controlador.mover(Direccion.ARRIBA));
        vincular("S", () -> controlador.mover(Direccion.ABAJO));
        vincular("A", () -> controlador.mover(Direccion.IZQUIERDA));
        vincular("D", () -> controlador.mover(Direccion.DERECHA));

        vincular("U", controlador::deshacer);
        vincular("R", controlador::reiniciar);
    }

    private void vincular(String tecla, Runnable accion) {
        String nombre = "accion_" + tecla;
        JComponent raiz = getRootPane();
        raiz.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla), nombre);
        raiz.getActionMap().put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accion.run();
            }
        });
    }
}
