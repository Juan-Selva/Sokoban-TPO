package com.sokoban.dominio;

/**
 * Piso transitable; cuando una caja entra por un empuje, se desliza en la misma
 * direccion (R12). La continuacion del deslizamiento la orquesta el
 * ResolutorMovimiento (capa partida), que conoce la direccion del empuje;
 * el dominio solo expone el tipo de celda de forma polimorfica.
 */
public class TerrenoResbaladizo extends Celda {

    public TerrenoResbaladizo(Posicion posicion) {
        super(posicion);
    }

    @Override
    public boolean esTransitable() {
        return true;
    }

    @Override
    public boolean provocaDeslizamiento() {
        return true;
    }

    @Override
    public String clavePresentacion() {
        return "TERRENO_RESBALADIZO";
    }
}
