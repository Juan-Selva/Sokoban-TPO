package com.sokoban.partida;

import com.sokoban.observer.Observable;
import com.sokoban.observer.Observer;
import java.util.ArrayList;
import java.util.List;

/**
 * Cuenta regresiva opcional del nivel (R22-R24). Es Observable: avisa a sus
 * observadores cuando el tiempo se agota, pero NO decide que hacer al expirar
 * (esa decision la toma el EstadoJuego). La fuente de los ticks (un timer de
 * Swing, por ejemplo) vive en la capa de presentacion.
 */
public class Temporizador implements Observable {

    private final int tiempoLimite;
    private int tiempoRestante;
    private final List<Observer> observadores = new ArrayList<>();

    public Temporizador(int tiempoLimiteSegundos) {
        this.tiempoLimite = tiempoLimiteSegundos;
        this.tiempoRestante = tiempoLimiteSegundos;
    }

    public void iniciar() {
        this.tiempoRestante = tiempoLimite;
    }

    /** Avanza un segundo; al llegar a cero notifica (una sola vez). */
    public void tick() {
        if (tiempoRestante == 0) {
            return;
        }
        tiempoRestante--;
        if (tiempoRestante == 0) {
            notificar();
        }
    }

    public int getTiempoRestante() {
        return tiempoRestante;
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
