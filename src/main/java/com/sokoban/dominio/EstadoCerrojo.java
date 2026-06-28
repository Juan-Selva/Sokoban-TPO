package com.sokoban.dominio;

/**
 * Patron State del cerrojo, espejo de EstadoMuro: encapsula el comportamiento
 * segun tenga o no una caja llave encima. Asi el cerrojo no usa if(tieneLlave)
 * para decidir si la llave puede salir (R11): el despacho es polimorfico.
 */
public interface EstadoCerrojo {

    /** R11: si la caja llave que esta encima puede ser empujada fuera. */
    boolean permiteSalidaLlave(Cerrojo cerrojo, Tablero tablero);

    /** Si el cerrojo esta activado (con una caja llave encima). */
    boolean estaActivo();
}
