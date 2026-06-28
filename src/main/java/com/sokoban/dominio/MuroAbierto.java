package com.sokoban.dominio;

/** Estado "abierto": el muro se comporta como celda vacia (transitable). */
public class MuroAbierto implements EstadoMuro {

    @Override
    public boolean esTransitable() {
        return true;
    }

    @Override
    public String clavePresentacion() {
        return "MURO_ABIERTO";
    }
}
