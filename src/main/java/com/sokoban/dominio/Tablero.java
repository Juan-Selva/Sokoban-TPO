package com.sokoban.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Agregador del estado fisico del nivel: la grilla de celdas, el jugador y las
 * cajas. Sabe donde esta cada cosa y resuelve la condicion de victoria; NO sabe
 * como se mueve cada entidad (eso lo decide la entidad) ni como se dibuja.
 */
public class Tablero {

    private final int filas;
    private final int columnas;
    private final Celda[][] celdas;
    private Jugador jugador;
    private final List<Caja> cajas = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();

    public Tablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.celdas = new Celda[filas][columnas];
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public boolean dentroDeLimites(Posicion posicion) {
        return posicion.getFila() >= 0 && posicion.getFila() < filas
                && posicion.getColumna() >= 0 && posicion.getColumna() < columnas;
    }

    public Celda celdaEn(Posicion posicion) {
        return celdas[posicion.getFila()][posicion.getColumna()];
    }

    public void setCelda(Posicion posicion, Celda celda) {
        celdas[posicion.getFila()][posicion.getColumna()] = celda;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
        jugador.vincular(this);
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void agregarCaja(Caja caja) {
        cajas.add(caja);
        caja.vincular(this);
    }

    public List<Caja> getCajas() {
        return cajas;
    }

    public void agregarItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public Item itemEn(Posicion posicion) {
        for (Item item : items) {
            if (item.getPosicion().equals(posicion)) {
                return item;
            }
        }
        return null;
    }

    public void quitarItem(Item item) {
        items.remove(item);
    }

    public Caja cajaEn(Posicion posicion) {
        for (Caja caja : cajas) {
            if (caja.getPosicion().equals(posicion)) {
                return caja;
            }
        }
        return null;
    }

    public boolean hayEntidadEn(Posicion posicion) {
        if (jugador != null && jugador.getPosicion().equals(posicion)) {
            return true;
        }
        return cajaEn(posicion) != null;
    }

    /** Si la posicion no tiene ninguna entidad encima (complemento de hayEntidadEn). */
    public boolean estaLibre(Posicion posicion) {
        return !hayEntidadEn(posicion);
    }

    public Entidad entidadEn(Posicion posicion) {
        if (jugador != null && jugador.getPosicion().equals(posicion)) {
            return jugador;
        }
        return cajaEn(posicion);
    }

    /** Una posicion puede recibir una entidad si esta dentro, es transitable y esta libre. */
    public boolean puedeRecibirEntidad(Posicion posicion) {
        return dentroDeLimites(posicion)
                && celdaEn(posicion).esTransitable()
                && !hayEntidadEn(posicion);
    }

    /**
     * Reubica una entidad disparando los efectos de salida y de entrada de las
     * celdas involucradas (doble despacho). No valida: el llamador ya decidio
     * que el movimiento es legal (via puedeRecibirEntidad o intentarEmpujar).
     */
    public void mover(Entidad entidad, Posicion destino) {
        entidad.notificarSalida(celdaEn(entidad.getPosicion()));
        entidad.setPosicion(destino);
        entidad.notificarEntrada(celdaEn(destino));
    }

    public void eliminar(Caja caja) {
        cajas.remove(caja);
    }

    /**
     * Captura el estado fisico mutable (posiciones, resistencias, estado de muros
     * y cerrojos, y que cajas estan presentes) para soportar undo/reinicio.
     * Cada celda/entidad guarda lo suyo de forma polimorfica (sin instanceof).
     */
    public MementoTablero capturarEstado() {
        MementoTablero memento = new MementoTablero(cajas, items);
        jugador.capturarEstadoEn(memento);
        for (Caja caja : cajas) {
            caja.capturarEstadoEn(memento);
        }
        for (Celda[] fila : celdas) {
            for (Celda celda : fila) {
                if (celda != null) {
                    celda.capturarEstadoEn(memento);
                }
            }
        }
        return memento;
    }

    public void restaurarEstado(MementoTablero memento) {
        cajas.clear();
        cajas.addAll(memento.getCajasPresentes());
        items.clear();
        items.addAll(memento.getItemsPresentes());
        jugador.restaurarEstadoDe(memento);
        for (Caja caja : memento.getCajasPresentes()) {
            caja.restaurarEstadoDe(memento);
        }
        for (Celda[] fila : celdas) {
            for (Celda celda : fila) {
                if (celda != null) {
                    celda.restaurarEstadoDe(memento);
                }
            }
        }
    }

    /** Victoria: toda caja que cuenta esta sobre una celda que cuenta (R17). */
    public boolean hayVictoria() {
        var objetivos = cajas.stream()
                .filter(Caja::cuentaParaVictoria)
                .toList();
        if (objetivos.isEmpty()) {
            return false;
        }
        return objetivos.stream()
                .allMatch(caja -> celdaEn(caja.getPosicion()).cuentaParaVictoria());
    }
}
