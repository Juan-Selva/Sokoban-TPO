package com.sokoban.dominio;

/** Estructural: bloquea el paso de cualquier entidad. Nunca transitable (R3). */
public class Pared extends Celda {

    public Pared(Posicion posicion) {
        super(posicion);
    }

    @Override
    public boolean esTransitable() {
        return false;
    }

    @Override
    public String clavePresentacion() {
        return "PARED";
    }
}
