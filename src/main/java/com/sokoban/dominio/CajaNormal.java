package com.sokoban.dominio;

/** Caja sin caracteristicas especiales. */
public class CajaNormal extends Caja {

    public CajaNormal(Posicion posicion) {
        super(posicion);
    }

    @Override
    public String clavePresentacion() {
        return "CAJA_NORMAL";
    }
}
