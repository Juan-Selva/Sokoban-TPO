package com.sokoban.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sokoban.dominio.Direccion;
import org.junit.jupiter.api.Test;

/**
 * Integracion de toda la pila (carga -> dominio -> partida -> controlador) sin
 * vista: resolver el nivel 1 debe avanzar automaticamente al nivel 2.
 */
class ControladorTest {

    @Test
    void resolverElNivel1AvanzaAlNivel2() {
        Controlador controlador = new Controlador();
        controlador.iniciar();

        assertEquals(1, controlador.getNumeroNivel());

        // nivel1: @ $ . -> tres empujes a la derecha llevan la caja al destino.
        controlador.mover(Direccion.DERECHA);
        controlador.mover(Direccion.DERECHA);
        controlador.mover(Direccion.DERECHA);

        assertEquals(2, controlador.getNumeroNivel());
        assertNotNull(controlador.getJuego().getTablero().getJugador());
    }
}
