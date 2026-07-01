package com.sokoban.nivel;

import com.sokoban.dominio.Caja;
import com.sokoban.dominio.CajaFragil;
import com.sokoban.dominio.Celda;
import com.sokoban.dominio.Cerrojo;
import com.sokoban.dominio.Direccion;
import com.sokoban.dominio.MementoTablero;
import com.sokoban.dominio.MuroAbierto;
import com.sokoban.dominio.MuroAbiertoCerrado;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.Tablero;
import com.sokoban.partida.EventoJuego;
import com.sokoban.partida.Juego;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/** Utilidad de prueba: BFS para encontrar secuencia que completa un nivel. */
final class BuscadorSolucionNivel {

    private static final Direccion[] DIRECCIONES = {
            Direccion.ARRIBA, Direccion.ABAJO, Direccion.IZQUIERDA, Direccion.DERECHA
    };

    private BuscadorSolucionNivel() {
    }

    static List<Direccion> buscar(String ruta, int limiteProfundidad) throws IOException {
        Queue<List<Direccion>> pendientes = new ArrayDeque<>();
        Set<String> visitados = new HashSet<>();
        pendientes.add(List.of());

        while (!pendientes.isEmpty()) {
            List<Direccion> pasos = pendientes.poll();
            ResultadoSimulacion simulacion = simular(ruta, pasos);
            if (simulacion.fallo()) {
                continue;
            }
            if (simulacion.juego().hayVictoria()) {
                return pasos;
            }
            if (pasos.size() >= limiteProfundidad) {
                continue;
            }
            if (!visitados.add(simulacion.firma())) {
                continue;
            }
            for (Direccion dir : DIRECCIONES) {
                List<Direccion> siguientes = new ArrayList<>(pasos);
                siguientes.add(dir);
                pendientes.add(siguientes);
            }
        }
        return List.of();
    }

    static String formatear(List<Direccion> pasos) {
        StringBuilder sb = new StringBuilder();
        for (Direccion paso : pasos) {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(switch (paso) {
                case ARRIBA -> "A";
                case ABAJO -> "B";
                case IZQUIERDA -> "I";
                case DERECHA -> "D";
            });
        }
        return sb.toString();
    }

    private static ResultadoSimulacion simular(String ruta, List<Direccion> pasos) throws IOException {
        LectorTxt lector = new LectorTxt(new CeldaFactory(), new EntidadFactory(), new ItemFactory());
        Juego juego = new Juego(lector.leer(ruta).construirTablero());
        for (Direccion paso : pasos) {
            juego.mover(paso);
            EventoJuego evento = juego.getUltimoEvento();
            if (evento == EventoJuego.SIN_ENERGIA || evento == EventoJuego.CAJA_FRAGIL_ROTA) {
                return new ResultadoSimulacion(juego, true, "");
            }
        }
        return new ResultadoSimulacion(juego, false, firma(juego.getTablero()));
    }

    private static String firma(Tablero tablero) {
        MementoTablero memento = tablero.capturarEstado();
        StringBuilder sb = new StringBuilder();
        sb.append(memento.posicionDe(tablero.getJugador())).append('|');
        sb.append(memento.getEnergiaJugador()).append('|');
        List<Caja> cajas = new ArrayList<>(memento.getCajasPresentes());
        cajas.sort(Comparator.comparing(Caja::getPosicion, Comparator.comparingInt(Posicion::getFila)
                .thenComparingInt(Posicion::getColumna)));
        for (Caja caja : cajas) {
            sb.append(caja.clavePresentacion()).append('@').append(memento.posicionDe(caja));
            if (caja instanceof CajaFragil fragil) {
                sb.append('r').append(memento.resistenciaDe(fragil));
            }
            sb.append(';');
        }
        sb.append('|').append(memento.getItemsPresentes().size()).append('|');
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int col = 0; col < tablero.getColumnas(); col++) {
                Celda celda = tablero.celdaEn(new Posicion(fila, col));
                if (celda instanceof MuroAbiertoCerrado muro) {
                    sb.append('M').append(fila).append(',').append(col).append(':');
                    sb.append(memento.estadoMuroDe(muro) instanceof MuroAbierto ? 'A' : 'C');
                } else if (celda instanceof Cerrojo cerrojo) {
                    sb.append('C').append(fila).append(',').append(col).append(':');
                    sb.append(memento.estadoCerrojoDe(cerrojo).estaActivo() ? 'A' : 'I');
                }
            }
        }
        return sb.toString();
    }

    private record ResultadoSimulacion(Juego juego, boolean fallo, String firma) {
    }
}
