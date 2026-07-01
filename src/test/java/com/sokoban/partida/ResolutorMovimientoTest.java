package com.sokoban.partida;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sokoban.dominio.Caja;
import com.sokoban.dominio.CajaFragil;
import com.sokoban.dominio.CajaNormal;
import com.sokoban.dominio.CeldaVacia;
import com.sokoban.dominio.Destino;
import com.sokoban.dominio.Direccion;
import com.sokoban.dominio.Jugador;
import com.sokoban.dominio.Pared;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.Tablero;
import com.sokoban.dominio.TerrenoResbaladizo;
import org.junit.jupiter.api.Test;

class ResolutorMovimientoTest {

    /** Construye un tablero de 1 fila lleno de piso, con jugador en (0,0). */
    private Tablero filaDePiso(int columnas) {
        Tablero tablero = new Tablero(1, columnas);
        for (int c = 0; c < columnas; c++) {
            tablero.setCelda(new Posicion(0, c), new CeldaVacia(new Posicion(0, c)));
        }
        tablero.setJugador(new Jugador(new Posicion(0, 0)));
        return tablero;
    }

    private ResolutorMovimiento resolutorDe(Tablero tablero) {
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        return new ResolutorMovimiento(tablero, estado);
    }

    @Test
    void movimientoSimpleCuentaMovimientoNoEmpuje() {
        Tablero tablero = filaDePiso(3);
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        EventoJuego evento = resolutor.resolver(Direccion.DERECHA);

        assertEquals(new Posicion(0, 1), tablero.getJugador().getPosicion());
        assertEquals(1, estado.getMovimientos());
        assertEquals(0, estado.getEmpujes());
        assertEquals(EventoJuego.MOVIMIENTO, evento);
    }

    @Test
    void empujeDevuelveEventoEmpuje() {
        Tablero tablero = filaDePiso(4);
        tablero.agregarCaja(new CajaNormal(new Posicion(0, 1)));
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        assertEquals(EventoJuego.EMPUJE, resolutor.resolver(Direccion.DERECHA));
    }

    @Test
    void movimientoBloqueadoDevuelveNada() {
        Tablero tablero = new Tablero(1, 2);
        tablero.setCelda(new Posicion(0, 0), new CeldaVacia(new Posicion(0, 0)));
        tablero.setCelda(new Posicion(0, 1), new Pared(new Posicion(0, 1)));
        tablero.setJugador(new Jugador(new Posicion(0, 0)));
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        assertEquals(EventoJuego.NADA, resolutor.resolver(Direccion.DERECHA));
    }

    @Test
    void empujeMueveCajaYJugadorYCuentaEmpuje() {
        Tablero tablero = filaDePiso(4);
        Caja caja = new CajaNormal(new Posicion(0, 1));
        tablero.agregarCaja(caja);
        EstadoJuego estado = new EstadoJuego(tablero);
        estado.guardarEstadoInicial();
        ResolutorMovimiento resolutor = new ResolutorMovimiento(tablero, estado);

        resolutor.resolver(Direccion.DERECHA);

        assertEquals(new Posicion(0, 1), tablero.getJugador().getPosicion());
        assertEquals(new Posicion(0, 2), caja.getPosicion());
        assertEquals(1, estado.getMovimientos());
        assertEquals(1, estado.getEmpujes());
    }

    @Test
    void noSePuedeEmpujarContraPared() {
        Tablero tablero = new Tablero(1, 3);
        tablero.setCelda(new Posicion(0, 0), new CeldaVacia(new Posicion(0, 0)));
        tablero.setCelda(new Posicion(0, 1), new CeldaVacia(new Posicion(0, 1)));
        tablero.setCelda(new Posicion(0, 2), new Pared(new Posicion(0, 2)));
        tablero.setJugador(new Jugador(new Posicion(0, 0)));
        Caja caja = new CajaNormal(new Posicion(0, 1));
        tablero.agregarCaja(caja);
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        resolutor.resolver(Direccion.DERECHA);

        assertEquals(new Posicion(0, 0), tablero.getJugador().getPosicion());
        assertEquals(new Posicion(0, 1), caja.getPosicion());
    }

    @Test
    void noSePuedenEmpujarDosCajasJuntas() {
        Tablero tablero = filaDePiso(4);
        Caja primera = new CajaNormal(new Posicion(0, 1));
        Caja segunda = new CajaNormal(new Posicion(0, 2));
        tablero.agregarCaja(primera);
        tablero.agregarCaja(segunda);
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        resolutor.resolver(Direccion.DERECHA);

        assertEquals(new Posicion(0, 0), tablero.getJugador().getPosicion());
        assertEquals(new Posicion(0, 1), primera.getPosicion());
        assertEquals(new Posicion(0, 2), segunda.getPosicion());
    }

    @Test
    void cajaSeDeslizaEnTerrenoResbaladizoHastaElFinal() {
        // [piso][piso(resbaladizo el resto)] ... ultima celda piso normal
        Tablero tablero = new Tablero(1, 5);
        tablero.setCelda(new Posicion(0, 0), new CeldaVacia(new Posicion(0, 0)));
        tablero.setCelda(new Posicion(0, 1), new CeldaVacia(new Posicion(0, 1)));
        tablero.setCelda(new Posicion(0, 2), new TerrenoResbaladizo(new Posicion(0, 2)));
        tablero.setCelda(new Posicion(0, 3), new TerrenoResbaladizo(new Posicion(0, 3)));
        tablero.setCelda(new Posicion(0, 4), new CeldaVacia(new Posicion(0, 4)));
        tablero.setJugador(new Jugador(new Posicion(0, 0)));
        Caja caja = new CajaNormal(new Posicion(0, 1));
        tablero.agregarCaja(caja);
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        resolutor.resolver(Direccion.DERECHA);

        // La caja entra a (0,2) resbaladizo, sigue por (0,3) y se detiene en (0,4) no resbaladizo.
        assertEquals(new Posicion(0, 4), caja.getPosicion());
        assertEquals(new Posicion(0, 1), tablero.getJugador().getPosicion());
    }

    @Test
    void cajaFragilSeRompeYDesapareceTrasAgotarResistencia() {
        Tablero tablero = filaDePiso(6);
        CajaFragil caja = new CajaFragil(new Posicion(0, 1), 2);
        tablero.agregarCaja(caja);
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        resolutor.resolver(Direccion.DERECHA); // resistencia 2 -> 1
        assertEquals(1, caja.getResistencia());
        assertTrue(tablero.getCajas().contains(caja));

        // Reposiciona al jugador detras de la caja para volver a empujarla.
        resolutor.resolver(Direccion.DERECHA); // resistencia 1 -> 0, se rompe

        assertTrue(caja.estaRota());
        assertFalse(tablero.getCajas().contains(caja));
        assertNull(tablero.cajaEn(caja.getPosicion()));
    }

    @Test
    void cajaFragilRotaDisparaReinicioYNoVictoria() {
        Tablero tablero = filaDePiso(6);
        tablero.setCelda(new Posicion(0, 2), new Destino(new Posicion(0, 2)));
        CajaFragil caja = new CajaFragil(new Posicion(0, 1), 1);
        tablero.agregarCaja(caja);
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        EventoJuego evento = resolutor.resolver(Direccion.DERECHA);

        assertEquals(EventoJuego.CAJA_FRAGIL_ROTA, evento);
        assertFalse(resolutor.hayVictoria());
    }

    @Test
    void juegoReiniciaAlRomperCajaFragil() {
        Tablero tablero = filaDePiso(4);
        CajaFragil caja = new CajaFragil(new Posicion(0, 1), 1);
        tablero.agregarCaja(caja);
        Juego juego = new Juego(tablero);

        juego.mover(Direccion.DERECHA);

        assertEquals(EventoJuego.CAJA_FRAGIL_ROTA, juego.getUltimoEvento());
        assertFalse(juego.hayVictoria());
        assertEquals(1, tablero.getCajas().size());
        assertEquals(new Posicion(0, 1), tablero.getCajas().get(0).getPosicion());
        assertEquals(1, ((CajaFragil) tablero.getCajas().get(0)).getResistencia());
    }

    @Test
    void victoriaCuandoLaCajaQuedaSobreElDestino() {
        Tablero tablero = new Tablero(1, 3);
        tablero.setCelda(new Posicion(0, 0), new CeldaVacia(new Posicion(0, 0)));
        tablero.setCelda(new Posicion(0, 1), new CeldaVacia(new Posicion(0, 1)));
        tablero.setCelda(new Posicion(0, 2), new Destino(new Posicion(0, 2)));
        tablero.setJugador(new Jugador(new Posicion(0, 0)));
        tablero.agregarCaja(new CajaNormal(new Posicion(0, 1)));
        ResolutorMovimiento resolutor = resolutorDe(tablero);

        assertFalse(resolutor.hayVictoria());
        resolutor.resolver(Direccion.DERECHA);
        assertTrue(resolutor.hayVictoria());
    }
}
