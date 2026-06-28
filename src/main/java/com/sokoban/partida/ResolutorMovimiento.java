package com.sokoban.partida;

import com.sokoban.dominio.Caja;
import com.sokoban.dominio.Direccion;
import com.sokoban.dominio.Jugador;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.Tablero;

/**
 * Template Method de la resolucion de un movimiento. El esqueleto (calcular
 * destino -> resolver empuje o movimiento simple -> deslizamiento -> registrar ->
 * verificar victoria) es FIJO; los pasos variables se delegan a los objetos del
 * dominio por polimorfismo (la caja decide su empuje, la celda si desliza), de
 * modo que el resolutor no contiene ninguna logica especifica por tipo.
 */
public class ResolutorMovimiento {

    private final Tablero tablero;
    private final EstadoJuego estadoJuego;

    public ResolutorMovimiento(Tablero tablero, EstadoJuego estadoJuego) {
        this.tablero = tablero;
        this.estadoJuego = estadoJuego;
    }

    public final EventoJuego resolver(Direccion direccion) {
        Jugador jugador = tablero.getJugador();
        Posicion destino = calcularDestino(jugador.getPosicion(), direccion);

        if (!tablero.dentroDeLimites(destino)) {
            return EventoJuego.NADA;
        }

        Caja caja = tablero.cajaEn(destino);
        boolean huboEmpuje = false;
        boolean huboMovimiento;
        EventoJuego evento;

        if (caja != null) {
            huboMovimiento = resolverEmpuje(jugador, caja, destino, direccion);
            huboEmpuje = huboMovimiento;
            evento = EventoJuego.EMPUJE;
        } else {
            huboMovimiento = jugador.intentarMover(direccion);
            evento = EventoJuego.MOVIMIENTO;
        }

        if (!huboMovimiento) {
            return EventoJuego.NADA;
        }

        estadoJuego.registrarMovimiento(huboEmpuje);
        return evento;
    }

    protected Posicion calcularDestino(Posicion origen, Direccion direccion) {
        return origen.sumar(direccion);
    }

    protected boolean resolverEmpuje(Jugador jugador, Caja caja, Posicion destino, Direccion direccion) {
        boolean cajaMovida = caja.intentarEmpujar(direccion);
        if (!cajaMovida) {
            return false;
        }

        tablero.mover(jugador, destino);

        if (caja.debeSerEliminada()) {
            tablero.eliminar(caja);
        } else {
            resolverDeslizamiento(caja, direccion);
        }
        return true;
    }

    protected void resolverDeslizamiento(Caja caja, Direccion direccion) {
        while (tablero.celdaEn(caja.getPosicion()).provocaDeslizamiento()) {
            if (!caja.deslizar(direccion)) {
                return;
            }
        }
    }

    public boolean hayVictoria() {
        return tablero.hayVictoria();
    }
}
