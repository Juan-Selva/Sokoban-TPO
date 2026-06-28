package com.sokoban.partida;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Caretaker del patron Memento: cola acotada con los ultimos 15 estados (R14).
 * Guarda y entrega mementos sin conocer su contenido.
 */
public class Historial {

    private static final int CAPACIDAD = 15;

    private final Deque<EstadoJuegoMemento> estados = new ArrayDeque<>();

    public void guardar(EstadoJuegoMemento memento) {
        estados.addLast(memento);
        if (estados.size() > CAPACIDAD) {
            estados.removeFirst();
        }
    }

    /**
     * Retrocede n pasos en un unico salto (R15) y devuelve el estado destino,
     * que queda como el mas reciente. Si hay menos de n, va al mas antiguo
     * disponible. Nunca deja el historial vacio.
     */
    public EstadoJuegoMemento retroceder(int n) {
        int pasos = Math.min(n, estados.size() - 1);
        for (int i = 0; i < pasos; i++) {
            estados.removeLast();
        }
        return estados.peekLast();
    }

    public boolean tieneAlgoQueDeshacer() {
        return estados.size() > 1;
    }

    public void descartar() {
        estados.clear();
    }

    public int cantidad() {
        return estados.size();
    }
}
