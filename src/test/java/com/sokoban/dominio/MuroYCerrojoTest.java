package com.sokoban.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Verifica el patron State del muro y la activacion por caja llave, sin que el
 * codigo de produccion use ningun if/instanceof para determinar el estado.
 */
class MuroYCerrojoTest {

    @Test
    void muroArrancaCerradoYNoEsTransitable() {
        MuroAbiertoCerrado muro = new MuroAbiertoCerrado(new Posicion(0, 0));
        assertFalse(muro.esTransitable());
    }

    @Test
    void abrirYCerrarCambianElComportamiento() {
        MuroAbiertoCerrado muro = new MuroAbiertoCerrado(new Posicion(0, 0));

        muro.abrir();
        assertTrue(muro.esTransitable());

        muro.cerrar();
        assertFalse(muro.esTransitable());
    }

    @Test
    void cajaLlaveSobreSuCerrojoAbreElMuro() {
        MuroAbiertoCerrado muro = new MuroAbiertoCerrado(new Posicion(0, 2));
        Cerrojo cerrojo = new Cerrojo(new Posicion(0, 1));
        cerrojo.setMuro(muro);

        CajaLlave llave = new CajaLlave(new Posicion(0, 1));
        llave.setCerrojoAsignado(cerrojo);

        // La caja llave "entra" al cerrojo (doble despacho).
        cerrojo.efectoEntrada(llave);

        assertTrue(cerrojo.estaActivo());
        assertTrue(muro.esTransitable());

        // Y al salir, el muro vuelve a cerrarse.
        cerrojo.efectoSalida(llave);
        assertFalse(cerrojo.estaActivo());
        assertFalse(muro.esTransitable());
    }

    @Test
    void cajaNormalNoActivaElCerrojo() {
        MuroAbiertoCerrado muro = new MuroAbiertoCerrado(new Posicion(0, 2));
        Cerrojo cerrojo = new Cerrojo(new Posicion(0, 1));
        cerrojo.setMuro(muro);

        cerrojo.efectoEntrada(new CajaNormal(new Posicion(0, 1)));

        assertFalse(cerrojo.estaActivo());
        assertFalse(muro.esTransitable());
    }

    /**
     * R11: la caja llave NO puede salir del cerrojo si la celda del muro asociado
     * tiene una entidad encima. El empuje se bloquea (la llave no se mueve y el
     * muro sigue abierto). La decision se resuelve por State, sin condicionales.
     */
    @Test
    void cajaLlaveNoPuedeSalirSiElMuroEstaOcupado() {
        Tablero tablero = armarTableroConLlaveEnCerrojo();
        CajaLlave llave = (CajaLlave) tablero.cajaEn(new Posicion(0, 1));
        MuroAbiertoCerrado muro = (MuroAbiertoCerrado) tablero.celdaEn(new Posicion(0, 3));

        // Ocupamos la celda del muro (abierto) con una caja normal.
        tablero.agregarCaja(new CajaNormal(new Posicion(0, 3)));

        boolean movida = llave.intentarEmpujar(Direccion.IZQUIERDA);

        assertFalse(movida);
        assertEquals(new Posicion(0, 1), llave.getPosicion());
        assertTrue(muro.esTransitable());
    }

    /**
     * R11 (caso permitido): si el muro asociado esta libre, la caja llave sale y
     * el muro vuelve a cerrarse.
     */
    @Test
    void cajaLlaveSaleSiElMuroEstaLibre() {
        Tablero tablero = armarTableroConLlaveEnCerrojo();
        CajaLlave llave = (CajaLlave) tablero.cajaEn(new Posicion(0, 1));
        MuroAbiertoCerrado muro = (MuroAbiertoCerrado) tablero.celdaEn(new Posicion(0, 3));

        boolean movida = llave.intentarEmpujar(Direccion.IZQUIERDA);

        assertTrue(movida);
        assertEquals(new Posicion(0, 0), llave.getPosicion());
        assertFalse(muro.esTransitable());
    }

    /** Tablero 1x5: vacia, cerrojo(con llave), vacia, muro, vacia. */
    private Tablero armarTableroConLlaveEnCerrojo() {
        Tablero tablero = new Tablero(1, 5);
        tablero.setCelda(new Posicion(0, 0), new CeldaVacia(new Posicion(0, 0)));
        Cerrojo cerrojo = new Cerrojo(new Posicion(0, 1));
        tablero.setCelda(new Posicion(0, 1), cerrojo);
        tablero.setCelda(new Posicion(0, 2), new CeldaVacia(new Posicion(0, 2)));
        MuroAbiertoCerrado muro = new MuroAbiertoCerrado(new Posicion(0, 3));
        tablero.setCelda(new Posicion(0, 3), muro);
        tablero.setCelda(new Posicion(0, 4), new CeldaVacia(new Posicion(0, 4)));

        cerrojo.setMuro(muro);

        CajaLlave llave = new CajaLlave(new Posicion(0, 1));
        llave.setCerrojoAsignado(cerrojo);
        tablero.agregarCaja(llave);

        // La llave activa su cerrojo (doble despacho): el muro queda abierto.
        cerrojo.efectoEntrada(llave);
        return tablero;
    }
}
