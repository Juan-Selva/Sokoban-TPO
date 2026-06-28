package com.sokoban.dominio;

/**
 * Entidad controlada por la persona. Decide si puede moverse consultando al
 * tablero. La coordinacion del empuje (mover jugador + caja juntos) y el
 * deslizamiento los orquesta el ResolutorMovimiento (capa partida); aqui se
 * resuelve el movimiento simple a una celda libre.
 */
public class Jugador extends Entidad {

    public Jugador(Posicion posicion) {
        super(posicion);
    }

    /** Movimiento simple a una celda transitable y libre. */
    public boolean intentarMover(Direccion direccion) {
        Posicion destino = posicion.sumar(direccion);
        if (!tablero.puedeRecibirEntidad(destino)) {
            return false;
        }
        tablero.mover(this, destino);
        return true;
    }

    @Override
    public void agregarseA(Tablero tablero) {
        tablero.setJugador(this);
    }

    @Override
    public String clavePresentacion() {
        return "JUGADOR";
    }
}
