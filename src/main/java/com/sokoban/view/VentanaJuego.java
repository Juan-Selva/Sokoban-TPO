package com.sokoban.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.sokoban.controller.Controlador;
import com.sokoban.dominio.Direccion;
import com.sokoban.observer.Observer;
import com.sokoban.partida.ResultadoNivel;

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
    private final CardLayout cartas = new CardLayout();
    private final JPanel raiz = new JPanel(cartas);

    public VentanaJuego(Controlador controlador) {
        this.controlador = controlador;
        controlador.setVista(this);

        setTitle("Sokoban");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(480, 360));
        setSize(1000, 700);                        // tamano al restaurar desde maximizado
        setExtendedState(JFrame.MAXIMIZED_BOTH);   // abrir en pantalla completa (maximizada)

        this.panelHud = new PanelHud(controlador);
        this.panelTablero = new PanelTablero(controlador);

        // El juego (HUD + tablero) vive en una carta; el menu de inicio en otra.
        JPanel panelJuego = new JPanel(new BorderLayout());
        panelJuego.add(panelHud, BorderLayout.NORTH);
        panelJuego.add(panelTablero, BorderLayout.CENTER);

        PanelInicio panelInicio = new PanelInicio(
                this::jugar,
                this::seleccionarNivel,
                this::mostrarInstrucciones,
                () -> System.exit(0));
        raiz.add(panelInicio, "menu");
        raiz.add(panelJuego, "juego");
        setContentPane(raiz);
        cartas.show(raiz, "menu");

        registrarTeclas();
    }

    /** Arranca la partida desde el nivel 1 y muestra el tablero (boton Jugar). */
    private void jugar() {
        controlador.iniciar();
        cartas.show(raiz, "juego");
        requestFocusInWindow();
    }

    private void seleccionarNivel() {
        int totalNiveles = controlador.getTotalNiveles();
        String[] opciones = new String[totalNiveles];
        for (int i = 0; i < totalNiveles; i++) {
            opciones[i] = "Nivel " + (i + 1);
        }
        String seleccionado = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona un nivel para jugar:",
                "Niveles",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opciones,
                opciones[0]);
        if (seleccionado != null) {
            int indice = Integer.parseInt(seleccionado.replaceAll("\\D+", "")) - 1;
            controlador.iniciar(indice);
            cartas.show(raiz, "juego");
            requestFocusInWindow();
        }
    }

    private void mostrarInstrucciones() {
        String mensaje = "Objetivo:\n"
                + "Empuja cada caja hasta su destino.\n\n"
                + "Controles:\n"
                + "Flechas o WASD = mover\n"
                + "U = deshacer\n"
                + "R = reiniciar nivel\n\n"
                + "Mecánicas especiales:\n"
                + "- Botella de poder: recoge botellas para recuperar energía extra.\n"
                + "- Cajas pesadas: empujarlas consume más energía, así que planea bien tus movimientos.\n\n"
                + "Usa 'Niveles' para elegir directamente un nivel.\n"
                + "Presiona 'Salir' para cerrar el juego.";
        JOptionPane.showMessageDialog(this, mensaje, "Instrucciones", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Notificacion del modelo (Observer): repintar tablero y HUD. */
    @Override
    public void actualizar() {
        panelTablero.repaint();
        panelHud.refrescar();
    }

    /** Reconstruye el layout cuando cambia el nivel (tamano del tablero). */
    public void refrescarNivel() {
        // La ventana permanece maximizada (pantalla completa) en todos los niveles;
        // no se hace pack() para no reajustarla al tablero. El tablero se reescala
        // solo al panel (paintComponent), asi que solo refrescamos HUD y repintamos.
        panelHud.refrescar();
        panelTablero.ajustarTamano();
        revalidate();
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
        JComponent rootPane = getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla), nombre);
        rootPane.getActionMap().put(nombre, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accion.run();
            }
        });
    }
}
