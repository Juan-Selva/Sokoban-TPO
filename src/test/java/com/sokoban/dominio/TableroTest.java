package com.sokoban.dominio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TableroTest {

    private Tablero tableroFilaDePiso(int columnas) {
        Tablero tablero = new Tablero(1, columnas);
        for (int c = 0; c < columnas; c++) {
            tablero.setCelda(new Posicion(0, c), new CeldaVacia(new Posicion(0, c)));
        }
        return tablero;
    }

    @Test
    void noPuedeRecibirEntidadEnParedNiFueraDeLimitesNiOcupada() {
        Tablero tablero = new Tablero(1, 3);
        tablero.setCelda(new Posicion(0, 0), new CeldaVacia(new Posicion(0, 0)));
        tablero.setCelda(new Posicion(0, 1), new Pared(new Posicion(0, 1)));
        tablero.setCelda(new Posicion(0, 2), new CeldaVacia(new Posicion(0, 2)));
        tablero.agregarCaja(new CajaNormal(new Posicion(0, 2)));

        assertTrue(tablero.puedeRecibirEntidad(new Posicion(0, 0)));
        assertFalse(tablero.puedeRecibirEntidad(new Posicion(0, 1)));   // pared
        assertFalse(tablero.puedeRecibirEntidad(new Posicion(0, 2)));   // caja
        assertFalse(tablero.puedeRecibirEntidad(new Posicion(5, 5)));   // fuera
    }

    @Test
    void jugadorSeMueveACeldaLibre() {
        Tablero tablero = tableroFilaDePiso(3);
        Jugador jugador = new Jugador(new Posicion(0, 0));
        tablero.setJugador(jugador);

        assertTrue(jugador.intentarMover(Direccion.DERECHA));
        assertTrue(jugador.getPosicion().equals(new Posicion(0, 1)));
    }

    @Test
    void victoriaIgnoraLaCajaLlave() {
        // [destino][cerrojo] : caja normal en destino, caja llave en cerrojo.
        Tablero tablero = new Tablero(1, 2);
        tablero.setCelda(new Posicion(0, 0), new Destino(new Posicion(0, 0)));
        tablero.setCelda(new Posicion(0, 1), new Cerrojo(new Posicion(0, 1)));

        tablero.agregarCaja(new CajaNormal(new Posicion(0, 0)));
        tablero.agregarCaja(new CajaLlave(new Posicion(0, 1)));

        // La caja llave NO esta sobre un destino, pero igual hay victoria (R17).
        assertTrue(tablero.hayVictoria());
    }

    @Test
    void noHayVictoriaSiUnaCajaNormalNoEstaEnDestino() {
        Tablero tablero = new Tablero(1, 2);
        tablero.setCelda(new Posicion(0, 0), new Destino(new Posicion(0, 0)));
        tablero.setCelda(new Posicion(0, 1), new CeldaVacia(new Posicion(0, 1)));

        tablero.agregarCaja(new CajaNormal(new Posicion(0, 0)));
        tablero.agregarCaja(new CajaNormal(new Posicion(0, 1)));

        assertFalse(tablero.hayVictoria());
    }

    @Test
    void noHayVictoriaSiNoQuedanCajasObjetivo() {
        Tablero tablero = new Tablero(1, 1);
        tablero.setCelda(new Posicion(0, 0), new Destino(new Posicion(0, 0)));

        assertFalse(tablero.hayVictoria());
    }
}
