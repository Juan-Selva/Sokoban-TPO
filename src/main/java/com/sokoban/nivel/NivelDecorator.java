package com.sokoban.nivel;

import com.sokoban.dominio.Tablero;

/**
 * Decorator base: envuelve un Nivel y delega todo en el. Permite agregar
 * modificadores (vision limitada) de forma componible, sin explosion de subclases.
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
}
