package com.sokoban.nivel;

/** Agrega tiempo limite (en segundos) a cualquier Nivel (R22-R24). */
public class TiempoLimiteDecorator extends NivelDecorator {

    private final int segundos;

    public TiempoLimiteDecorator(Nivel envuelto, int segundos) {
        super(envuelto);
        this.segundos = segundos;
    }

    @Override
    public boolean tieneTiempoLimite() {
        return true;
    }

    @Override
    public int getSegundosLimite() {
        return segundos;
    }
}
