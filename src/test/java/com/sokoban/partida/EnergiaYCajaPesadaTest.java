package com.sokoban.partida;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sokoban.dominio.BotellaAgua;
import com.sokoban.dominio.CajaNormal;
import com.sokoban.dominio.CajaPesada;
import com.sokoban.dominio.CeldaVacia;
import com.sokoban.dominio.Direccion;
import com.sokoban.dominio.Jugador;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.Tablero;
import org.junit.jupiter.api.Test;

class EnergiaYCajaPesadaTest {

    private Tablero filaDePiso(int columnas, Jugador jugador) {
        Tablero tablero = new Tablero(1, columnas);
        for (int c = 0; c < columnas; c++) {
            tablero.setCelda(new Posicion(0, c), new CeldaVacia(new Posicion(0, c)));
        }
        tablero.setJugador(jugador);
        return tablero;
    }

    private ResolutorMovimiento resolutorDe(Tablero tablero) {
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        return new ResolutorMovimiento(tablero, estado);
    }

    @Test
    void moverConsumeUnaEnergia() {
        Jugador jugador = new Jugador(new Posicion(0, 0));
        Tablero tablero = filaDePiso(3, jugador);
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        resolutor.resolver(Direccion.DERECHA);

        assertEquals(19, jugador.getEnergia());
    }

    @Test
    void empujarCajaNormalConsumeUnoYPesadaConsumeTres() {
        Jugador j1 = new Jugador(new Posicion(0, 0));
        Tablero t1 = filaDePiso(4, j1);
        t1.agregarCaja(new CajaNormal(new Posicion(0, 1)));
        resolutorDe(t1).resolver(Direccion.DERECHA);
        assertEquals(19, j1.getEnergia());

        Jugador j2 = new Jugador(new Posicion(0, 0));
        Tablero t2 = filaDePiso(4, j2);
        t2.agregarCaja(new CajaPesada(new Posicion(0, 1)));
        resolutorDe(t2).resolver(Direccion.DERECHA);
        assertEquals(17, j2.getEnergia());
    }

    @Test
    void sinEnergiaDevuelveEventoSinEnergia() {
        Jugador jugador = new Jugador(new Posicion(0, 0));
        Tablero tablero = filaDePiso(3, jugador);
        jugador.consumir(20); // queda en 0
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        assertEquals(EventoJuego.SIN_ENERGIA, resolutor.resolver(Direccion.DERECHA));
        assertEquals(new Posicion(0, 0), jugador.getPosicion());
    }

    @Test
    void cajaPesadaNoSeEmpujaSiNoAlcanzaLaEnergia() {
        Jugador jugador = new Jugador(new Posicion(0, 0));
        Tablero tablero = filaDePiso(4, jugador);
        CajaPesada pesada = new CajaPesada(new Posicion(0, 1));
        tablero.agregarCaja(pesada);
        jugador.consumir(18); // energia 2: puede caminar pero no empujar (cuesta 3)
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        EventoJuego evento = resolutor.resolver(Direccion.DERECHA);

        assertEquals(EventoJuego.NADA, evento);
        assertEquals(new Posicion(0, 1), pesada.getPosicion());
        assertEquals(new Posicion(0, 0), jugador.getPosicion());
        assertEquals(2, jugador.getEnergia());
    }

    @Test
    void laBotellaReponeEnergiaAlPisarla() {
        Jugador jugador = new Jugador(new Posicion(0, 0));
        Tablero tablero = filaDePiso(3, jugador);
        tablero.agregarItem(new BotellaAgua(new Posicion(0, 1)));
        jugador.consumir(15); // energia 5
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        resolutor.resolver(Direccion.DERECHA); // -1 al mover, +12 por la botella

        assertEquals(16, jugador.getEnergia());
        assertEquals(0, tablero.getItems().size());
    }

    @Test
    void elUndoRestauraLaEnergia() {
        Jugador jugador = new Jugador(new Posicion(0, 0));
        Tablero tablero = filaDePiso(10, jugador);
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        resolutor.resolver(Direccion.DERECHA);
        resolutor.resolver(Direccion.DERECHA);
        resolutor.resolver(Direccion.DERECHA);
        assertEquals(17, jugador.getEnergia());

        estado.ejecutarUndo(); // menos de 5 movimientos: vuelve al inicio

        assertEquals(20, jugador.getEnergia());
    }

    @Test
    void quedarseSinEnergiaReiniciaElNivel() {
        Jugador jugador = new Jugador(new Posicion(0, 0));
        Tablero tablero = filaDePiso(5, jugador);
        Juego juego = new Juego(tablero);

        juego.mover(Direccion.DERECHA); // se mueve a (0,1), energia 19
        jugador.consumir(19);           // queda en 0
        juego.mover(Direccion.DERECHA); // SIN_ENERGIA: el propio evento reinicia el nivel

        assertEquals(EventoJuego.SIN_ENERGIA, juego.getUltimoEvento());
        assertEquals(new Posicion(0, 0), jugador.getPosicion());
        assertEquals(20, jugador.getEnergia());
    }
}
