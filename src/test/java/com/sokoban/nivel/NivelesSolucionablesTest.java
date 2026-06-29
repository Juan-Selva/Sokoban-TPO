package com.sokoban.nivel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sokoban.comandos.MoverCommand;
import com.sokoban.dominio.Direccion;
import com.sokoban.partida.Juego;
import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 * Verifica que los niveles del juego son efectivamente completables, aplicando
 * una secuencia de movimientos conocida y comprobando la victoria. Sirve de red
 * de seguridad ante cambios en el layout de los .txt.
 */
class NivelesSolucionablesTest {

    private static final Direccion A = Direccion.ARRIBA;
    private static final Direccion B = Direccion.ABAJO;
    private static final Direccion I = Direccion.IZQUIERDA;
    private static final Direccion D = Direccion.DERECHA;

    private Juego cargar(String ruta) throws IOException {
        LectorTxt lector = new LectorTxt(new CeldaFactory(), new EntidadFactory(), new ItemFactory());
        Nivel nivel = lector.leer(ruta);
        return new Juego(nivel.construirTablero());
    }

    private void jugar(Juego juego, Direccion... pasos) {
        for (Direccion paso : pasos) {
            juego.ejecutar(new MoverCommand(paso));
        }
    }

    @Test
    void nivel1SeCompletaConDosCajas() throws IOException {
        Juego juego = cargar("levels/nivel1.txt");
        assertFalse(juego.hayVictoria());

        jugar(juego, A, B, I, A);

        assertTrue(juego.hayVictoria());
    }

    @Test
    void nivel2SeCompletaConDeslizamiento() throws IOException {
        Juego juego = cargar("levels/nivel2.txt");
        assertFalse(juego.hayVictoria());

        jugar(juego, A, D, D, A);

        assertTrue(juego.hayVictoria());
    }

    @Test
    void nivel3SeCompletaConCajaNormal() throws IOException {
        Juego juego = cargar("levels/nivel3.txt");
        assertFalse(juego.hayVictoria());

        jugar(juego, B, B, D, D);

        assertTrue(juego.hayVictoria());
    }

    @Test
    void nivel4SeCompletaConCajaNormalYLlave() throws IOException {
        Juego juego = cargar("levels/nivel4.txt");
        assertFalse(juego.hayVictoria());

        jugar(juego, B, B, I, B, D, D, D, D, D, D);

        assertTrue(juego.hayVictoria());
    }

    @Test
    void nivel5SeCompletaConCajaFragil() throws IOException {
        Juego juego = cargar("levels/nivel5.txt");
        assertFalse(juego.hayVictoria());

        jugar(juego, I, A);

        assertTrue(juego.hayVictoria());
    }

    @Test
    void nivel6SeCompletaConCajaPesada() throws IOException {
        Juego juego = cargar("levels/nivel6.txt");
        assertFalse(juego.hayVictoria());

        jugar(juego, I, A);

        assertTrue(juego.hayVictoria());
    }

    @Test
    void nivel7SeCompletaPeseALaVisionReducida() throws IOException {
        Juego juego = cargar("levels/nivel7.txt");
        assertFalse(juego.hayVictoria());

        jugar(juego, B, B, B, B, B, B, B, D, D, D, D, D, D, D, D, A, A);

        assertTrue(juego.hayVictoria());
    }
}
