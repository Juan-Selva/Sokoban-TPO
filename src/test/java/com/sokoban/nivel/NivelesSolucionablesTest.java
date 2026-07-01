package com.sokoban.nivel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sokoban.dominio.Direccion;
import com.sokoban.partida.Juego;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Red de seguridad: verifica que cada nivel del juego tiene solucion sobre el
 * motor real. Se apoya en {@link BuscadorSolucionNivel} (BFS que contempla
 * energia, caja fragil, caja pesada, llave/cerrojo/muro y deslizamiento) para
 * hallar una secuencia de movimientos y comprobar la victoria. No depende de un
 * layout puntual: si algun nivel deja de ser resoluble, el test falla.
 */
class NivelesSolucionablesTest {

    private Juego cargar(String ruta) throws IOException {
        LectorTxt lector = new LectorTxt(new CeldaFactory(), new EntidadFactory(), new ItemFactory());
        return new Juego(lector.leer(ruta).construirTablero());
    }

    private void verificarResoluble(String ruta, int limiteProfundidad) throws IOException {
        List<Direccion> pasos = BuscadorSolucionNivel.buscar(ruta, limiteProfundidad);
        assertFalse(pasos.isEmpty(), "No se encontro solucion para " + ruta);

        Juego juego = cargar(ruta);
        assertFalse(juego.hayVictoria());
        for (Direccion paso : pasos) {
            juego.mover(paso);
        }
        assertTrue(juego.hayVictoria(), "La secuencia hallada no completa " + ruta);
    }

    @Test
    void nivel1EsResoluble() throws IOException {
        verificarResoluble("levels/nivel1.txt", 15);
    }

    @Test
    void nivel2EsResoluble() throws IOException {
        verificarResoluble("levels/nivel2.txt", 15);
    }

    @Test
    void nivel3EsResoluble() throws IOException {
        verificarResoluble("levels/nivel3.txt", 22);
    }

    @Test
    void nivel4EsResoluble() throws IOException {
        verificarResoluble("levels/nivel4.txt", 35);
    }

    @Test
    void nivel5EsResoluble() throws IOException {
        verificarResoluble("levels/nivel5.txt", 40);
    }
}
