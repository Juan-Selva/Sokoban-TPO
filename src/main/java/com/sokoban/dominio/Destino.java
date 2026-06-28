package com.sokoban.dominio;

/** Piso transitable que marca donde debe quedar una caja para la victoria (R17). */
public class Destino extends Celda {

    public Destino(Posicion posicion) {
        super(posicion);
    }

    @Override
    public boolean esTransitable() {
        return true;
    }

    @Override
    public boolean cuentaParaVictoria() {
        return true;
    }

    @Override
    public String clavePresentacion() {
        return "DESTINO";
    }
}
