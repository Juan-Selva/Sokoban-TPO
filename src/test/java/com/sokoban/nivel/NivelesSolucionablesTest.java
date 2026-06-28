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
        LectorTxt lector = new LectorTxt(new CeldaFactory(), new EntidadFactory());
        Nivel nivel = lector.leer(ruta);
        return new Juego(nivel.construirTablero());
    }

    private void jugar(Juego juego, Direccion... pasos) {
        for (Direccion paso : pasos) {
            juego.ejecutar(new MoverCommand(paso));
        }
    }

    @Test
    void nivel3SeCompletaAbriendoElMuroConLaLlave() throws IOException {
        Juego juego = cargar("levels/nivel3.txt");
        assertFalse(juego.hayVictoria());

        // Empujar la llave al cerrojo (abre el muro), bajar y empujar la caja al destino.
        jugar(juego, D, D, D, B, B, D, B, I);

        assertTrue(juego.hayVictoria());
    }

    @Test
    void nivel4SeCompletaPeseALaVisionReducida() throws IOException {
        Juego juego = cargar("levels/nivel4.txt");
        assertFalse(juego.hayVictoria());

        // Empujar la caja a la derecha y luego hacia abajo hasta el destino.
        jugar(juego, B, B, D, D, D, D, D, A, D, B, B);

        assertTrue(juego.hayVictoria());
    }
}
