package com.sokoban.nivel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sokoban.dominio.Direccion;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.Tablero;
import com.sokoban.partida.EstadoJuego;
import com.sokoban.partida.ResolutorMovimiento;
import org.junit.jupiter.api.Test;

class NivelBuilderTest {

    private NivelBuilder builder() {
        return new NivelBuilder(new CeldaFactory(), new EntidadFactory(), new ItemFactory());
    }

    @Test
    void nivelBaseNoTieneVision() {
        NivelBuilder builder = builder();
        builder.dimensiones(1, 1);
        builder.agregarCelda(' ', new Posicion(0, 0));

        Nivel nivel = builder.build();

        assertFalse(nivel.tieneVisionLimitada());
    }

    @Test
    void elDecoradorDeVisionSeAplica() {
        NivelBuilder builder = builder();
        builder.dimensiones(1, 1);
        builder.agregarCelda(' ', new Posicion(0, 0));
        builder.conVisionLimitada(3);

        Nivel nivel = builder.build();

        assertTrue(nivel.tieneVisionLimitada());
        assertEquals(3, nivel.getRadioVision());
    }

    @Test
    void laCajaLlaveSobreSuCerrojoAbreElMuroEnUnNivelConstruido() {
        // Layout: @ K C M  (jugador, llave, cerrojo, muro) en una fila.
        NivelBuilder builder = builder();
        builder.dimensiones(1, 4);
        builder.agregarEntidad('@', new Posicion(0, 0));
        builder.agregarEntidad('K', new Posicion(0, 1));
        builder.agregarCelda('C', new Posicion(0, 2));
        builder.agregarCelda('M', new Posicion(0, 3));

        Tablero tablero = builder.build().construirTablero();
        Posicion posMuro = new Posicion(0, 3);

        assertFalse(tablero.celdaEn(posMuro).esTransitable());

        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        // Empuja la llave a la derecha, sobre el cerrojo: abre el muro.
        resolutor.resolver(Direccion.DERECHA);

        assertTrue(tablero.celdaEn(posMuro).esTransitable());
        assertNotNull(tablero.getJugador());
    }
}
