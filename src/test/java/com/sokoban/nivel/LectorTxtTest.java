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
        return new LectorTxt(new CeldaFactory(), new EntidadFactory());
    }

    @Test
    void cargaNivel1ConJugadorYUnaCaja() throws IOException {
        Nivel nivel = lector().leer("levels/nivel1.txt");
        Tablero tablero = nivel.construirTablero();

        assertEquals(3, tablero.getFilas());
        assertEquals(7, tablero.getColumnas());
        assertNotNull(tablero.getJugador());
        assertEquals(1, tablero.getCajas().size());
        assertFalse(nivel.tieneTiempoLimite());
    }

    @Test
    void nivel3LeeLaDirectivaDeTiempo() throws IOException {
        Nivel nivel = lector().leer("levels/nivel3.txt");

        assertTrue(nivel.tieneTiempoLimite());
        assertEquals(120, nivel.getSegundosLimite());
        // La cabecera (TIEMPO) no debe contarse como fila del tablero.
        assertEquals(7, nivel.construirTablero().getFilas());
    }
}
