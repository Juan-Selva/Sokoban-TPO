package com.sokoban.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

class PosicionTest {

    @Test
    void sumarDevuelveNuevaPosicionDesplazada() {
        Posicion origen = new Posicion(2, 3);

        assertEquals(new Posicion(1, 3), origen.sumar(Direccion.ARRIBA));
        assertEquals(new Posicion(3, 3), origen.sumar(Direccion.ABAJO));
        assertEquals(new Posicion(2, 2), origen.sumar(Direccion.IZQUIERDA));
        assertEquals(new Posicion(2, 4), origen.sumar(Direccion.DERECHA));
    }

    @Test
    void sumarNoMutaElOriginal() {
        Posicion origen = new Posicion(0, 0);
        Posicion movida = origen.sumar(Direccion.DERECHA);

        assertNotSame(origen, movida);
        assertEquals(new Posicion(0, 0), origen);
    }

    @Test
    void igualdadPorValor() {
        assertEquals(new Posicion(5, 7), new Posicion(5, 7));
        assertEquals(new Posicion(5, 7).hashCode(), new Posicion(5, 7).hashCode());
        assertNotEquals(new Posicion(5, 7), new Posicion(7, 5));
    }
}
