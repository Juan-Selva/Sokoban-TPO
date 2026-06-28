package com.sokoban.nivel;

import com.sokoban.dominio.Celda;
import com.sokoban.dominio.Entidad;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.RegistroEnlaces;
import com.sokoban.dominio.Tablero;

/**
 * Nivel sin modificadores. Guarda el layout crudo (grilla de caracteres) y, en
 * cada construirTablero(), usa las factories para instanciar celdas y entidades.
 *
 * El armado no usa instanceof: cada entidad se agrega a si misma (agregarseA) y
 * cada celda/entidad se auto-registra para enlaces (registrarEnlaces). El piso
 * bajo una entidad es siempre una celda vacia.
 */
public class NivelBase implements Nivel {

    private final int filas;
    private final int columnas;
    private final char[][] grilla;
    private final CeldaFactory celdaFactory;
    private final EntidadFactory entidadFactory;

    public NivelBase(int filas, int columnas, char[][] grilla,
                     CeldaFactory celdaFactory, EntidadFactory entidadFactory) {
        this.filas = filas;
        this.columnas = columnas;
        this.grilla = grilla;
        this.celdaFactory = celdaFactory;
        this.entidadFactory = entidadFactory;
    }

    @Override
    public Tablero construirTablero() {
        Tablero tablero = new Tablero(filas, columnas);
        RegistroEnlaces registro = new RegistroEnlaces();

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                char caracter = grilla[fila][columna];
                Posicion posicion = new Posicion(fila, columna);
                colocar(tablero, registro, caracter, posicion);
            }
        }

        registro.enlazar();
        return tablero;
    }

    private void colocar(Tablero tablero, RegistroEnlaces registro, char caracter, Posicion posicion) {
        if (entidadFactory.conoce(caracter)) {
            Celda piso = celdaFactory.crear(' ', posicion);
            tablero.setCelda(posicion, piso);
            piso.registrarEnlaces(registro);

            Entidad entidad = entidadFactory.crear(caracter, posicion);
            entidad.agregarseA(tablero);
            entidad.registrarEnlaces(registro);
        } else {
            Celda celda = celdaFactory.crear(caracter, posicion);
            tablero.setCelda(posicion, celda);
            celda.registrarEnlaces(registro);
        }
    }

    @Override
    public boolean tieneVisionLimitada() {
        return false;
    }

    @Override
    public int getRadioVision() {
        return 0;
    }

    @Override
    public boolean tieneTiempoLimite() {
        return false;
    }

    @Override
    public int getSegundosLimite() {
        return 0;
    }
}
