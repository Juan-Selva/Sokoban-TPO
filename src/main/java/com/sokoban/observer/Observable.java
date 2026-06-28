package com.sokoban.observer;

/** Sujeto observable: mantiene observadores y los notifica. */
public interface Observable {
    void agregarObservador(Observer observador);

    void quitarObservador(Observer observador);

    void notificar();
}
