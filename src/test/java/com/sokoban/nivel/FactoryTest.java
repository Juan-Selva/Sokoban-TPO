package com.sokoban.nivel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sokoban.dominio.Posicion;
import org.junit.jupiter.api.Test;

class FactoryTest {

    private final Posicion p = new Posicion(0, 0);

    @Test
    void celdaFactoryMapeaCaracteresAClaves() {
        CeldaFactory factory = new CeldaFactory();
        assertEquals("PARED", factory.crear('#', p).clavePresentacion());
        assertEquals("DESTINO", factory.crear('.', p).clavePresentacion());
        assertEquals("TERRENO_RESBALADIZO", factory.crear('~', p).clavePresentacion());
        assertEquals("CERROJO", factory.crear('C', p).clavePresentacion());
        assertEquals("MURO_CERRADO", factory.crear('M', p).clavePresentacion());
        assertEquals("CELDA_VACIA", factory.crear(' ', p).clavePresentacion());
    }

    @Test
    void celdaFactoryTrataDesconocidoComoPiso() {
        assertEquals("CELDA_VACIA", new CeldaFactory().crear('?', p).clavePresentacion());
    }

    @Test
    void entidadFactoryConoceYMapea() {
        EntidadFactory factory = new EntidadFactory();
        assertTrue(factory.conoce('@'));
        assertTrue(factory.conoce('$'));
        assertTrue(factory.conoce('F'));
        assertTrue(factory.conoce('K'));
        assertTrue(factory.conoce('P'));
        assertFalse(factory.conoce('#'));

        assertEquals("JUGADOR", factory.crear('@', p).clavePresentacion());
        assertEquals("CAJA_NORMAL", factory.crear('$', p).clavePresentacion());
        assertEquals("CAJA_FRAGIL", factory.crear('F', p).clavePresentacion());
        assertEquals("CAJA_LLAVE", factory.crear('K', p).clavePresentacion());
        assertEquals("CAJA_PESADA", factory.crear('P', p).clavePresentacion());
    }

    @Test
    void itemFactoryConoceYMapea() {
        ItemFactory factory = new ItemFactory();
        assertTrue(factory.conoce('B'));
        assertFalse(factory.conoce('@'));
        assertEquals("BOTELLA_AGUA", factory.crear('B', p).clavePresentacion());
    }
}
