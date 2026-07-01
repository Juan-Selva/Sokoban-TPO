package com.sokoban.partida;

import com.sokoban.dominio.Caja;
import com.sokoban.dominio.Direccion;
import com.sokoban.dominio.Item;
import com.sokoban.dominio.Jugador;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.Tablero;

/**
 * Template Method de la resolucion de un movimiento. El esqueleto (validar
 * energia -> calcular destino -> resolver empuje o movimiento simple -> consumir
 * energia -> recoger item -> registrar) es FIJO; los pasos variables se delegan a
 * los objetos del dominio por polimorfismo (la caja decide su empuje y su costo,
 * la celda si desliza), de modo que el resolutor no contiene logica por tipo.
 *
 * Energia: moverse cuesta 1 y empujar cuesta lo que define la caja. Si el jugador
 * no tiene energia ni para moverse, devuelve SIN_ENERGIA (la capa superior
 * reinicia el nivel). Si no le alcanza para empujar una caja puntual, no pasa
 * nada (puede seguir caminando).
 */
public class ResolutorMovimiento {

    private static final int COSTO_MOVIMIENTO = 1;

    private final Tablero tablero;
    private final EstadoJuego estadoJuego;

    public ResolutorMovimiento(Tablero tablero, EstadoJuego estadoJuego) {
        this.tablero = tablero;
        this.estadoJuego = estadoJuego;
    }

    public final EventoJuego resolver(Direccion direccion) {
        Jugador jugador = tablero.getJugador();

        if (!jugador.tieneEnergia(COSTO_MOVIMIENTO)) {
            return EventoJuego.SIN_ENERGIA;
        }

        Posicion destino = calcularDestino(jugador.getPosicion(), direccion);
        if (!tablero.dentroDeLimites(destino)) {
            return EventoJuego.NADA;
        }

        Caja caja = tablero.cajaEn(destino);
        if (caja != null) {
            return resolverConCaja(jugador, caja, destino, direccion);
        }
        return resolverSimple(jugador, direccion);
    }

    protected Posicion calcularDestino(Posicion origen, Direccion direccion) {
        return origen.sumar(direccion);
    }

    private EventoJuego resolverConCaja(Jugador jugador, Caja caja, Posicion destino, Direccion direccion) {
        int costo = caja.getCostoEmpuje();
        if (!jugador.tieneEnergia(costo)) {
            return EventoJuego.NADA;
        }
        if (!resolverEmpuje(jugador, caja, destino, direccion)) {
            return EventoJuego.NADA;
        }
        jugador.consumir(costo);
        finalizarTurno(jugador, true);
        if (caja.reiniciaNivelAlEliminarse()) {
            return EventoJuego.CAJA_FRAGIL_ROTA;
        }
        return EventoJuego.EMPUJE;
    }

    private EventoJuego resolverSimple(Jugador jugador, Direccion direccion) {
        if (!jugador.intentarMover(direccion)) {
            return EventoJuego.NADA;
        }
        jugador.consumir(COSTO_MOVIMIENTO);
        finalizarTurno(jugador, false);
        return EventoJuego.MOVIMIENTO;
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

    /** Recoge el item de la celda actual (si hay) y registra el movimiento. */
    private void finalizarTurno(Jugador jugador, boolean huboEmpuje) {
        recogerItem(jugador);
        estadoJuego.registrarMovimiento(huboEmpuje);
    }

    private void recogerItem(Jugador jugador) {
        Item item = tablero.itemEn(jugador.getPosicion());
        if (item != null) {
            item.aplicar(jugador);
            tablero.quitarItem(item);
        }
    }

    public boolean hayVictoria() {
        return tablero.hayVictoria();
    }
}
