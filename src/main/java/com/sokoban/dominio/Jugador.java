package com.sokoban.dominio;

/**
 * Entidad controlada por la persona. Decide si puede moverse consultando al
 * tablero. La coordinacion del empuje (mover jugador + caja juntos) y el
 * deslizamiento los orquesta el ResolutorMovimiento (capa partida); aqui se
 * resuelve el movimiento simple a una celda libre.
 *
 * Tiene una reserva de ENERGIA: cada accion (mover/empujar) la consume y se
 * repone con items (ver BotellaAgua). La energia es parte del estado del juego,
 * por lo que se guarda y restaura en el memento.
 */
public class Jugador extends Entidad {

    private static final int ENERGIA_POR_DEFECTO = 20;

    private final int energiaMaxima;
    private int energia;

    public Jugador(Posicion posicion) {
        this(posicion, ENERGIA_POR_DEFECTO);
    }

    public Jugador(Posicion posicion, int energiaMaxima) {
        super(posicion);
        this.energiaMaxima = energiaMaxima;
        this.energia = energiaMaxima;
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

    public int getEnergia() {
        return energia;
    }

    public int getEnergiaMaxima() {
        return energiaMaxima;
    }

    public boolean tieneEnergia(int costo) {
        return energia >= costo;
    }

    public void consumir(int costo) {
        this.energia = Math.max(0, energia - costo);
    }

    public void reponer(int cantidad) {
        this.energia = Math.min(energiaMaxima, energia + cantidad);
    }

    @Override
    public void agregarseA(Tablero tablero) {
        tablero.setJugador(this);
    }

    @Override
    public void capturarEstadoEn(MementoTablero memento) {
        super.capturarEstadoEn(memento);
        memento.guardarEnergiaJugador(energia);
    }

    @Override
    public void restaurarEstadoDe(MementoTablero memento) {
        super.restaurarEstadoDe(memento);
        this.energia = memento.getEnergiaJugador();
    }

    @Override
    public String clavePresentacion() {
        return "JUGADOR";
    }
}
