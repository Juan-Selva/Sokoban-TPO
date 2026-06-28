package com.sokoban.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Cada celda/entidad devuelve un token neutro de presentacion de forma
 * polimorfica. La View lo mapea a una imagen/color, sin que el dominio conozca
 * Swing y sin instanceof.
 */
class ClavePresentacionTest {

    @Test
    void cadaCeldaDevuelveSuClave() {
        assertEquals("CELDA_VACIA", new CeldaVacia(new Posicion(0, 0)).clavePresentacion());
        assertEquals("PARED", new Pared(new Posicion(0, 0)).clavePresentacion());
        assertEquals("DESTINO", new Destino(new Posicion(0, 0)).clavePresentacion());
        assertEquals("TERRENO_RESBALADIZO", new TerrenoResbaladizo(new Posicion(0, 0)).clavePresentacion());
        assertEquals("CERROJO", new Cerrojo(new Posicion(0, 0)).clavePresentacion());
    }

    @Test
    void laClaveDelMuroDependeDeSuEstado() {
        MuroAbiertoCerrado muro = new MuroAbiertoCerrado(new Posicion(0, 0));

        assertEquals("MURO_CERRADO", muro.clavePresentacion());

        muro.abrir();
        assertEquals("MURO_ABIERTO", muro.clavePresentacion());

        muro.cerrar();
        assertEquals("MURO_CERRADO", muro.clavePresentacion());
    }

    @Test
    void cadaEntidadDevuelveSuClave() {
        assertEquals("JUGADOR", new Jugador(new Posicion(0, 0)).clavePresentacion());
        assertEquals("CAJA_NORMAL", new CajaNormal(new Posicion(0, 0)).clavePresentacion());
        assertEquals("CAJA_FRAGIL", new CajaFragil(new Posicion(0, 0), 3).clavePresentacion());
        assertEquals("CAJA_LLAVE", new CajaLlave(new Posicion(0, 0)).clavePresentacion());
    }
}
