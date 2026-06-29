package com.sokoban.nivel;

import com.sokoban.dominio.Posicion;
import java.util.Arrays;

/**
 * Builder: arma un Nivel paso a paso (dimensiones -> caracteres -> modificadores)
 * y, al final, envuelve el NivelBase en los decoradores que correspondan. El
 * layout queda guardado como grilla de caracteres para poder reconstruir el
 * tablero (reset). Las factories se inyectan (las usa el NivelBase resultante).
 */
public class NivelBuilder {

    private final CeldaFactory celdaFactory;
    private final EntidadFactory entidadFactory;
    private final ItemFactory itemFactory;

    private int filas;
    private int columnas;
    private char[][] grilla;

    private Integer radioVision;

    public NivelBuilder(CeldaFactory celdaFactory, EntidadFactory entidadFactory, ItemFactory itemFactory) {
        this.celdaFactory = celdaFactory;
        this.entidadFactory = entidadFactory;
        this.itemFactory = itemFactory;
    }

    public void dimensiones(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.grilla = new char[filas][columnas];
        for (char[] fila : grilla) {
            Arrays.fill(fila, ' ');
        }
    }

    /** Registra un caracter de celda en la grilla. */
    public void agregarCelda(char caracter, Posicion posicion) {
        grilla[posicion.getFila()][posicion.getColumna()] = caracter;
    }

    /** Registra un caracter de entidad en la grilla (se resuelve al construir). */
    public void agregarEntidad(char caracter, Posicion posicion) {
        grilla[posicion.getFila()][posicion.getColumna()] = caracter;
    }

    /** Registra un caracter de item en la grilla (se resuelve al construir). */
    public void agregarItem(char caracter, Posicion posicion) {
        grilla[posicion.getFila()][posicion.getColumna()] = caracter;
    }

    public void conVisionLimitada(int radio) {
        this.radioVision = radio;
    }

    public Nivel build() {
        Nivel nivel = new NivelBase(filas, columnas, grilla, celdaFactory, entidadFactory, itemFactory);
        if (radioVision != null) {
            nivel = new VisionLimitadaDecorator(nivel, radioVision);
        }
        return nivel;
    }
}
