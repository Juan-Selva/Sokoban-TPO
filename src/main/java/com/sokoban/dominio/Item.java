package com.sokoban.dominio;

/**
 * Objeto recogible que descansa sobre el piso (no se empuja). Cuando el jugador
 * entra a su celda, aplica su efecto y se retira del tablero. Cada item define
 * su efecto de forma polimorfica (aplicar), sin que nadie pregunte su tipo.
 */
public interface Item {

    Posicion getPosicion();

    /** Efecto al ser recogido por el jugador. */
    void aplicar(Jugador jugador);

    /** Token neutro de presentacion (la View lo mapea a imagen/color). */
    String clavePresentacion();
}
