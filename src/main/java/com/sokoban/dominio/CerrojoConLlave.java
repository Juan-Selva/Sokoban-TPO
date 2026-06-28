package com.sokoban.dominio;

/**
 * Estado con la caja llave encima: la llave solo puede salir si la celda del
 * muro asociado esta libre (R11), evitando cerrar el muro sobre una entidad.
 */
public class CerrojoConLlave implements EstadoCerrojo {

    @Override
    public boolean permiteSalidaLlave(Cerrojo cerrojo, Tablero tablero) {
        return tablero.estaLibre(cerrojo.getMuro().getPosicion());
    }

    @Override
    public boolean estaActivo() {
        return true;
    }
}
