package com.sokoban.partida;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sokoban.observer.Observer;
import org.junit.jupiter.api.Test;

class TemporizadorTest {

    private static class Testigo implements Observer {
        boolean notificado = false;

        @Override
        public void actualizar() {
            notificado = true;
        }
    }

    @Test
    void notificaUnaVezAlLlegarACero() {
        Temporizador temporizador = new Temporizador(3);
        Testigo testigo = new Testigo();
        temporizador.agregarObservador(testigo);

        temporizador.tick();
        temporizador.tick();
        assertFalse(testigo.notificado);
        assertEquals(1, temporizador.getTiempoRestante());

        temporizador.tick();
        assertTrue(testigo.notificado);
        assertEquals(0, temporizador.getTiempoRestante());
    }

    @Test
    void noBajaDeCero() {
        Temporizador temporizador = new Temporizador(1);
        temporizador.tick();
        temporizador.tick();
        assertEquals(0, temporizador.getTiempoRestante());
    }
}
