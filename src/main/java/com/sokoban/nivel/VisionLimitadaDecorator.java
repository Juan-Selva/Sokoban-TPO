package com.sokoban.nivel;

/** Agrega vision limitada (radio N) a cualquier Nivel (R20-R21). */
public class VisionLimitadaDecorator extends NivelDecorator {

    private final int radio;

    public VisionLimitadaDecorator(Nivel envuelto, int radio) {
        super(envuelto);
        this.radio = radio;
    }

    @Override
    public boolean tieneVisionLimitada() {
        return true;
    }

    @Override
    public int getRadioVision() {
        return radio;
    }
}
