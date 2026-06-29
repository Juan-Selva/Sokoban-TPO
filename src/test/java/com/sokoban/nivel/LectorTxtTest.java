package com.sokoban.nivel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sokoban.dominio.Tablero;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class LectorTxtTest {

    private LectorTxt lector() {
        return new LectorTxt(new CeldaFactory(), new EntidadFactory(), new ItemFactory());
    }

    @Test
    void cargaNivel1ConJugadorYUnaCaja() throws IOException {
        Nivel nivel = lector().leer("levels/nivel1.txt");
        Tablero tablero = nivel.construirTablero();

        assertEquals(10, tablero.getFilas());
        assertEquals(15, tablero.getColumnas());
        assertNotNull(tablero.getJugador());
        assertEquals(2, tablero.getCajas().size());
    }

    @Test
    void nivel2TieneTerrenoResbaladizoYCaja() throws IOException {
        Nivel nivel = lector().leer("levels/nivel2.txt");
        Tablero tablero = nivel.construirTablero();

        assertFalse(nivel.tieneVisionLimitada());
        assertEquals(0, tablero.getItems().size());
        assertEquals(1, tablero.getCajas().size());
        assertEquals("CAJA_NORMAL", tablero.getCajas().get(0).clavePresentacion());
    }

    @Test
    void nivel3TieneCerrojoMuroBotellasYCaja() throws IOException {
        Nivel nivel = lector().leer("levels/nivel3.txt");
        Tablero tablero = nivel.construirTablero();

        assertEquals(3, tablero.getItems().size());
        assertEquals(2, tablero.getCajas().size());
    }

    @Test
    void nivel4NoEstaGanadoAlCargar() throws IOException {
        Nivel nivel = lector().leer("levels/nivel4.txt");
        Tablero tablero = nivel.construirTablero();

        assertEquals(2, tablero.getCajas().size());
        assertEquals("CAJA_NORMAL", tablero.getCajas().stream()
                .filter(c -> c.cuentaParaVictoria())
                .findFirst()
                .orElseThrow()
                .clavePresentacion());
        assertFalse(tablero.hayVictoria());
    }

    @Test
    void nivel5TieneCajaFragil() throws IOException {
        Nivel nivel = lector().leer("levels/nivel5.txt");
        Tablero tablero = nivel.construirTablero();

        assertEquals(1, tablero.getCajas().size());
        assertEquals("CAJA_FRAGIL", tablero.getCajas().get(0).clavePresentacion());
        assertEquals("3", tablero.getCajas().get(0).etiqueta());
    }

    @Test
    void nivel6TieneCajaPesada() throws IOException {
        Nivel nivel = lector().leer("levels/nivel6.txt");
        Tablero tablero = nivel.construirTablero();

        assertEquals(1, tablero.getCajas().size());
        assertEquals("CAJA_PESADA", tablero.getCajas().get(0).clavePresentacion());
    }

    @Test
    void nivel7TieneVisionLimitada() throws IOException {
        Nivel nivel = lector().leer("levels/nivel7.txt");
        Tablero tablero = nivel.construirTablero();

        assertTrue(nivel.tieneVisionLimitada());
        assertEquals(2, nivel.getRadioVision());
        assertEquals(1, tablero.getCajas().size());
        assertFalse(tablero.hayVictoria());
    }
}
