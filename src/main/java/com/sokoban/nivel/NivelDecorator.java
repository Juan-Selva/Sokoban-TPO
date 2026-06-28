package com.sokoban.nivel;

import com.sokoban.dominio.Tablero;

/**
 * Decorator base: envuelve un Nivel y delega todo en el. Los decoradores
 * concretos (vision, tiempo) son ortogonales y componibles, evitando la
 * explosion combinatoria de subclases.
 */
public abstract class NivelDecorator implements Nivel {

    protected final Nivel envuelto;

    protected NivelDecorator(Nivel envuelto) {
        this.envuelto = envuelto;
    }

    @Override
    public Tablero construirTablero() {
        return envuelto.construirTablero();
    }

    @Override
    public boolean tieneVisionLimitada() {
        return envuelto.tieneVisionLimitada();
    }

    @Override
    public int getRadioVision() {
        return envuelto.getRadioVision();
    }

    @Override
    public boolean tieneTiempoLimite() {
        return envuelto.tieneTiempoLimite();
    }

    @Override
    public int getSegundosLimite() {
        return envuelto.getSegundosLimite();
    }
}
