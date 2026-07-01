package com.sokoban.partida;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Verifica que el aviso al jugador se elige por polimorfismo del evento (doble
 * despacho, sin condicionales): solo las derrotas avisan; el reinicio manual y
 * el resto de los eventos no.
 */
class PresentacionEventoTest {

    /** Presentador de prueba que cuenta cuantas veces se avisa una derrota. */
    private static final class PresentadorSpy implements PresentadorPartida {
        private int derrotas = 0;

        @Override
        public void alPerderNivel() {
            derrotas++;
        }
    }

    @Test
    void losEventosDeDerrotaAvisan() {
        PresentadorSpy spy = new PresentadorSpy();

        EventoJuego.SIN_ENERGIA.presentar(spy);
        EventoJuego.CAJA_FRAGIL_ROTA.presentar(spy);

        assertEquals(2, spy.derrotas);
    }

    @Test
    void losDemasEventosNoAvisan() {
        PresentadorSpy spy = new PresentadorSpy();

        EventoJuego.NADA.presentar(spy);
        EventoJuego.MOVIMIENTO.presentar(spy);
        EventoJuego.EMPUJE.presentar(spy);
        EventoJuego.UNDO.presentar(spy);
        EventoJuego.REINICIO.presentar(spy);

        assertEquals(0, spy.derrotas);
    }
}
