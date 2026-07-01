package com.sokoban.nivel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.Tablero;
import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 * Verifica que el lector arma correctamente cada nivel del juego: dimensiones,
 * piezas y mecanicas presentes. Acompana la evolucion de los .txt (5 niveles).
 */
class LectorTxtTest {

    private LectorTxt lector() {
        return new LectorTxt(new CeldaFactory(), new EntidadFactory(), new ItemFactory());
    }

    private boolean hayCaja(Tablero t, String clave) {
        return t.getCajas().stream().anyMatch(c -> c.clavePresentacion().equals(clave));
    }

    @Test
    void nivel1TieneDosCajasYSinVision() throws IOException {
        Nivel nivel = lector().leer("levels/nivel1.txt");
        Tablero tablero = nivel.construirTablero();

        assertEquals(6, tablero.getFilas());
        assertEquals(8, tablero.getColumnas());
        assertNotNull(tablero.getJugador());
        assertEquals(2, tablero.getCajas().size());
        assertFalse(nivel.tieneVisionLimitada());
        assertFalse(tablero.hayVictoria());
    }

    @Test
    void nivel2TieneTerrenoResbaladizoYBotella() throws IOException {
        Nivel nivel = lector().leer("levels/nivel2.txt");
        Tablero tablero = nivel.construirTablero();

        assertFalse(nivel.tieneVisionLimitada());
        assertEquals(2, tablero.getCajas().size());
        assertEquals(1, tablero.getItems().size());
        assertEquals("TERRENO_RESBALADIZO", tablero.celdaEn(new Posicion(3, 3)).clavePresentacion());
    }

    @Test
    void nivel3TieneLlaveCerrojoYMuro() throws IOException {
        Nivel nivel = lector().leer("levels/nivel3.txt");
        Tablero tablero = nivel.construirTablero();

        // 2 cajas normales + 1 caja llave.
        assertEquals(3, tablero.getCajas().size());
        assertTrue(hayCaja(tablero, "CAJA_LLAVE"));
        assertEquals("MURO_CERRADO", tablero.celdaEn(new Posicion(3, 5)).clavePresentacion());
        assertEquals("CERROJO", tablero.celdaEn(new Posicion(5, 2)).clavePresentacion());
    }

    @Test
    void nivel4CombinaFragilPesadaLlaveYBotellas() throws IOException {
        Nivel nivel = lector().leer("levels/nivel4.txt");
        Tablero tablero = nivel.construirTablero();

        // caja normal + fragil + pesada + llave.
        assertEquals(4, tablero.getCajas().size());
        assertTrue(hayCaja(tablero, "CAJA_FRAGIL"));
        assertTrue(hayCaja(tablero, "CAJA_PESADA"));
        assertTrue(hayCaja(tablero, "CAJA_LLAVE"));
        assertEquals(2, tablero.getItems().size());
        assertFalse(tablero.hayVictoria());
    }

    @Test
    void nivel5TieneTodoYVisionLimitada() throws IOException {
        Nivel nivel = lector().leer("levels/nivel5.txt");
        Tablero tablero = nivel.construirTablero();

        assertTrue(nivel.tieneVisionLimitada());
        assertEquals(3, nivel.getRadioVision());
        assertEquals(4, tablero.getCajas().size());
        assertTrue(hayCaja(tablero, "CAJA_FRAGIL"));
        assertTrue(hayCaja(tablero, "CAJA_PESADA"));
        assertEquals(4, tablero.getItems().size());
    }
}
