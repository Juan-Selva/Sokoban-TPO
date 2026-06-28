package com.sokoban.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CajaFragilTest {

    /** Tablero minimo: una fila de piso para poder empujar. */
    private Tablero tableroFilaDePiso(int columnas) {
        Tablero tablero = new Tablero(1, columnas);
        for (int c = 0; c < columnas; c++) {
            tablero.setCelda(new Posicion(0, c), new CeldaVacia(new Posicion(0, c)));
        }
        return tablero;
    }

    @Test
    void cadaEmpujeEfectivoRestaUnaResistencia() {
        Tablero tablero = tableroFilaDePiso(5);
        CajaFragil caja = new CajaFragil(new Posicion(0, 0), 3);
        tablero.agregarCaja(caja);

        caja.intentarEmpujar(Direccion.DERECHA);
        assertEquals(2, caja.getResistencia());
        caja.intentarEmpujar(Direccion.DERECHA);
        assertEquals(1, caja.getResistencia());
        assertFalse(caja.estaRota());

        caja.intentarEmpujar(Direccion.DERECHA);
        assertEquals(0, caja.getResistencia());
        assertTrue(caja.estaRota());
    }

    @Test
    void empujeBloqueadoNoRestaResistencia() {
        Tablero tablero = new Tablero(1, 2);
        tablero.setCelda(new Posicion(0, 0), new CeldaVacia(new Posicion(0, 0)));
        tablero.setCelda(new Posicion(0, 1), new Pared(new Posicion(0, 1)));

        CajaFragil caja = new CajaFragil(new Posicion(0, 0), 3);
        tablero.agregarCaja(caja);

        boolean movida = caja.intentarEmpujar(Direccion.DERECHA);

        assertFalse(movida);
        assertEquals(3, caja.getResistencia());
        assertEquals(new Posicion(0, 0), caja.getPosicion());
    }
}
