package com.sokoban.nivel;

import com.sokoban.dominio.Tablero;

/**
 * Receta de un nivel: sabe construir un Tablero nuevo a partir de su layout y
 * expone su modificador opcional de vision limitada.
 * construirTablero() produce un tablero fresco en cada llamada (sirve para reset).
 */
public interface Nivel {

    Tablero construirTablero();

    boolean tieneVisionLimitada();

    int getRadioVision();
}
