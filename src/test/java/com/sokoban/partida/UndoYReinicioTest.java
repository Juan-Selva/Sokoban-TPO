package com.sokoban.partida;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sokoban.dominio.CeldaVacia;
import com.sokoban.dominio.Direccion;
import com.sokoban.dominio.Jugador;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.Tablero;
import org.junit.jupiter.api.Test;

class UndoYReinicioTest {

    private Tablero filaDePiso(int columnas) {
        Tablero tablero = new Tablero(1, columnas);
        for (int c = 0; c < columnas; c++) {
            tablero.setCelda(new Posicion(0, c), new CeldaVacia(new Posicion(0, c)));
        }
        tablero.setJugador(new Jugador(new Posicion(0, 0)));
        return tablero;
    }

    private void moverDerecha(ResolutorMovimiento resolutor, int veces) {
        for (int i = 0; i < veces; i++) {
            resolutor.resolver(Direccion.DERECHA);
        }
    }

    @Test
    void undoSaltaCincoMovimientosAtras() {
        Tablero tablero = filaDePiso(20);
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        moverDerecha(resolutor, 6);
        assertEquals(new Posicion(0, 6), tablero.getJugador().getPosicion());
        assertEquals(6, estado.getMovimientos());

        assertTrue(estado.ejecutarUndo());

        assertEquals(new Posicion(0, 1), tablero.getJugador().getPosicion());
        assertEquals(1, estado.getMovimientos());
    }

    @Test
    void conMenosDeCincoUndoVaAlEstadoInicial() {
        Tablero tablero = filaDePiso(20);
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        moverDerecha(resolutor, 3);
        assertTrue(estado.ejecutarUndo());

        assertEquals(new Posicion(0, 0), tablero.getJugador().getPosicion());
        assertEquals(0, estado.getMovimientos());
    }

    @Test
    void maximoTresUndosConsecutivosYUnMovimientoRecarga() {
        Tablero tablero = filaDePiso(20);
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        moverDerecha(resolutor, 15);

        assertTrue(estado.ejecutarUndo());
        assertTrue(estado.ejecutarUndo());
        assertTrue(estado.ejecutarUndo());
        assertEquals(3, estado.getUndosConsecutivos());
        assertFalse(estado.puedeUndo());
        assertFalse(estado.ejecutarUndo());

        // Un movimiento recarga los undos.
        resolutor.resolver(Direccion.DERECHA);
        assertEquals(0, estado.getUndosConsecutivos());
        assertTrue(estado.puedeUndo());
    }

    @Test
    void undosDisponiblesDecrecenYSeRecarganAlMover() {
        Tablero tablero = filaDePiso(20);
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        moverDerecha(resolutor, 15);
        assertEquals(3, estado.getUndosDisponibles());

        estado.ejecutarUndo();
        assertEquals(2, estado.getUndosDisponibles());
        estado.ejecutarUndo();
        estado.ejecutarUndo();
        assertEquals(0, estado.getUndosDisponibles());
        assertFalse(estado.puedeUndo());

        resolutor.resolver(Direccion.DERECHA);
        assertEquals(3, estado.getUndosDisponibles());
        assertTrue(estado.puedeUndo());
    }

    @Test
    void undosTotalesAcumulanYNoSeRecarganAlMover() {
        Tablero tablero = filaDePiso(20);
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        moverDerecha(resolutor, 15);
        estado.ejecutarUndo();
        resolutor.resolver(Direccion.DERECHA); // recarga los consecutivos, NO los totales
        estado.ejecutarUndo();

        assertEquals(1, estado.getUndosConsecutivos());
        assertEquals(2, estado.getUndosTotales());
    }

    @Test
    void reinicioResetaUndosTotales() {
        Tablero tablero = filaDePiso(20);
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        moverDerecha(resolutor, 10);
        estado.ejecutarUndo();
        assertEquals(1, estado.getUndosTotales());

        estado.reiniciar();
        assertEquals(0, estado.getUndosTotales());
    }

    @Test
    void reinicioVuelveAlEstadoInicial() {
        Tablero tablero = filaDePiso(20);
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        moverDerecha(resolutor, 7);
        estado.reiniciar();

        assertEquals(new Posicion(0, 0), tablero.getJugador().getPosicion());
        assertEquals(0, estado.getMovimientos());
        assertEquals(0, estado.getEmpujes());
        assertEquals(0, estado.getUndosConsecutivos());
    }
}
