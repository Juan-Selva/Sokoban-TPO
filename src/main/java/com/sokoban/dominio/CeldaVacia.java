package com.sokoban.dominio;

/** Piso normal: transitable, sin efectos especiales. */
public class CeldaVacia extends Celda {

    public CeldaVacia(Posicion posicion) {
        super(posicion);
    }

    @Override
    public boolean esTransitable() {
        return true;
    }

    @Override
    public String clavePresentacion() {
        return "CELDA_VACIA";
    }
}
