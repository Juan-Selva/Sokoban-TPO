package com.sokoban.dominio;

import java.util.Objects;

/**
 * Value object inmutable: identifica una ubicacion (fila, columna) del tablero.
 * Igualdad por valor. Sumar una direccion devuelve SIEMPRE una nueva Posicion
 * (no muta), evitando aliasing (ver 05-justificacion-diseno §1.4).
 */
public final class Posicion {

    private final int fila;
    private final int columna;

    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    /** Devuelve una nueva Posicion desplazada por los deltas de la direccion. */
    public Posicion sumar(Direccion direccion) {
        return new Posicion(fila + direccion.getDeltaFila(), columna + direccion.getDeltaColumna());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Posicion)) {
            return false;
        }
        Posicion otra = (Posicion) o;
        return fila == otra.fila && columna == otra.columna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }

    @Override
    public String toString() {
        return "(" + fila + ", " + columna + ")";
    }
}
