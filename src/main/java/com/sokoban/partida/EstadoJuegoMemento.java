package com.sokoban.partida;

import com.sokoban.dominio.MementoTablero;

/**
 * Memento del juego: snapshot inmutable formado por el estado fisico del tablero
 * y los contadores logicos. Solo EstadoJuego lo crea y lo consume; el Historial
 * (Caretaker) lo guarda sin mirar adentro.
 */
public final class EstadoJuegoMemento {

    private final MementoTablero estadoTablero;
    private final int movimientos;
    private final int empujes;

    public EstadoJuegoMemento(MementoTablero estadoTablero, int movimientos, int empujes) {
        this.estadoTablero = estadoTablero;
        this.movimientos = movimientos;
        this.empujes = empujes;
    }

    public MementoTablero getEstadoTablero() {
        return estadoTablero;
    }

    public int getMovimientos() {
        return movimientos;
    }

    public int getEmpujes() {
        return empujes;
    }
}
