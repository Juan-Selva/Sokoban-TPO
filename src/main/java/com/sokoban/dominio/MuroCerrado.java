package com.sokoban.dominio;

/** Estado "cerrado": el muro se comporta como pared (bloquea). */
public class MuroCerrado implements EstadoMuro {

    @Override
    public boolean esTransitable() {
        return false;
    }

    @Override
    public String clavePresentacion() {
        return "MURO_CERRADO";
    }
}
