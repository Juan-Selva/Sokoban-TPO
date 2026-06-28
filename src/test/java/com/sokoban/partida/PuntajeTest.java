package com.sokoban.partida;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Puntaje y calificacion por estrellas (§5.4). El score es aritmetica de negocio
 * y cada estrella decide su criterio polimorficamente (enum Calificacion).
 */
class PuntajeTest {

    @Test
    void scoreUsaLaFormulaYNuncaEsNegativo() {
        // 1000 - 3*10 - 5*4 - 100*1 = 850
        assertEquals(850, new ResultadoNivel(10, 4, 1).getScore());
        // max(0, ...) evita negativos
        assertEquals(0, new ResultadoNivel(1000, 0, 0).getScore());
    }

    @Test
    void tresEstrellasSinUndosYScoreAlto() {
        ResultadoNivel r = new ResultadoNivel(10, 5, 0); // score 945, undos 0
        assertEquals(3, r.getEstrellas());
        assertEquals(Calificacion.TRES_ESTRELLAS, r.getCalificacion());
    }

    @Test
    void dosEstrellasSiHuboUndosPeroScoreSuficiente() {
        ResultadoNivel r = new ResultadoNivel(10, 5, 1); // score 845, undos 1 -> no 3*
        assertEquals(2, r.getEstrellas());
    }

    @Test
    void unaEstrellaCuandoElScoreEsBajo() {
        ResultadoNivel r = new ResultadoNivel(200, 50, 0); // score 150 < 300
        assertEquals(1, r.getEstrellas());
    }
}
