package com.sokoban.dominio;

/**
 * Patron State: encapsula el comportamiento (transitable o no) y la apariencia
 * del muro abierto/cerrado. El muro delega aqui en lugar de usar un if(abierto).
 */
public interface EstadoMuro {

    boolean esTransitable();

    /** Token de presentacion del muro segun su estado actual. */
    String clavePresentacion();
}
