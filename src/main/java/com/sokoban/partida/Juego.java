package com.sokoban.partida;

import com.sokoban.comandos.Command;
import com.sokoban.dominio.Direccion;
import com.sokoban.dominio.Tablero;
import com.sokoban.observer.Observable;
import com.sokoban.observer.Observer;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade del modelo: unica superficie que ven el Controller y la View. Esconde
 * Tablero, EstadoJuego, ResolutorMovimiento e Historial. Es Observable: la View
 * se registra para repintar cuando cambia el estado, sin que el modelo la conozca.
 *
 * Los Command operan sobre esta fachada (ejecutar). Cada ejecucion notifica una
 * sola vez a los observadores.
 */
public class Juego implements Observable {

    private final Tablero tablero;
    private final EstadoJuego estadoJuego;
    private final ResolutorMovimiento resolutor;
    private final List<Observer> observadores = new ArrayList<>();
    private EventoJuego ultimoEvento = EventoJuego.NADA;

    public Juego(Tablero tablero) {
        this.tablero = tablero;
        this.estadoJuego = new EstadoJuego(tablero);
        this.resolutor = new ResolutorMovimiento(tablero, estadoJuego);
        this.estadoJuego.guardarEstadoInicial();
    }

    /** Punto de entrada uniforme para las acciones del jugador (Command). */
    public void ejecutar(Command command) {
        command.ejecutar(this);
        notificar();
    }

    public void mover(Direccion direccion) {
        EventoJuego evento = resolutor.resolver(direccion);
        evento.aplicarConsecuencia(this); // p. ej. SIN_ENERGIA reinicia el nivel
        this.ultimoEvento = evento;
    }

    public void deshacer() {
        boolean huboUndo = estadoJuego.ejecutarUndo();
        this.ultimoEvento = huboUndo ? EventoJuego.UNDO : EventoJuego.NADA;
    }

    public void reiniciar() {
        estadoJuego.reiniciar();
        this.ultimoEvento = EventoJuego.REINICIO;
    }

    /** Evento de la ultima accion ejecutada (lo usa la vista para el sonido). */
    public EventoJuego getUltimoEvento() {
        return ultimoEvento;
    }

    public boolean hayVictoria() {
        return tablero.hayVictoria();
    }

    /** Resumen del nivel actual (contadores, puntaje y calificacion). */
    public ResultadoNivel calcularResultado() {
        return estadoJuego.calcularResultado();
    }

    public Tablero getTablero() {
        return tablero;
    }

    public EstadoJuego getEstadoJuego() {
        return estadoJuego;
    }

    @Override
    public void agregarObservador(Observer observador) {
        observadores.add(observador);
    }

    @Override
    public void quitarObservador(Observer observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificar() {
        for (Observer observador : observadores) {
            observador.actualizar();
        }
    }
}
