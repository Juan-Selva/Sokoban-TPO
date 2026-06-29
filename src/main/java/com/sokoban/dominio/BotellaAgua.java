package com.sokoban.dominio;

/** Item que repone energia al jugador que la recoge. */
public class BotellaAgua implements Item {

    private static final int ENERGIA_QUE_REPONE = 12;

    private final Posicion posicion;

    public BotellaAgua(Posicion posicion) {
        this.posicion = posicion;
    }

    @Override
    public Posicion getPosicion() {
        return posicion;
    }

    @Override
    public void aplicar(Jugador jugador) {
        jugador.reponer(ENERGIA_QUE_REPONE);
    }

    @Override
    public String clavePresentacion() {
        return "BOTELLA_AGUA";
    }
}
