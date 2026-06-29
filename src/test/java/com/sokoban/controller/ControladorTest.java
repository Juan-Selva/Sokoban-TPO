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

        // nivel1: dos cajas sobre dos destinos.
        controlador.mover(Direccion.ARRIBA);
        controlador.mover(Direccion.ABAJO);
        controlador.mover(Direccion.IZQUIERDA);
        controlador.mover(Direccion.ARRIBA);

        assertEquals(2, controlador.getNumeroNivel());
        assertNotNull(controlador.getJuego().getTablero().getJugador());
    }
}
