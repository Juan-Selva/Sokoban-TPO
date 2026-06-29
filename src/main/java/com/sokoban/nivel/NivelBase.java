package com.sokoban.nivel;

import com.sokoban.dominio.Celda;
import com.sokoban.dominio.Entidad;
import com.sokoban.dominio.Item;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.RegistroEnlaces;
import com.sokoban.dominio.Tablero;

/**
 * Nivel sin modificadores. Guarda el layout crudo (grilla de caracteres) y, en
 * cada construirTablero(), usa las factories para instanciar celdas, entidades e
 * items.
 *
 * El armado no usa instanceof: el caracter se enruta por registro (conoce) y cada
 * entidad se agrega a si misma (agregarseA) y se auto-registra para enlaces. El
 * piso bajo una entidad o item es siempre una celda vacia.
 */
public class NivelBase implements Nivel {

    private final int filas;
    private final int columnas;
    private final char[][] grilla;
    private final CeldaFactory celdaFactory;
    private final EntidadFactory entidadFactory;
    private final ItemFactory itemFactory;

    public NivelBase(int filas, int columnas, char[][] grilla,
                     CeldaFactory celdaFactory, EntidadFactory entidadFactory, ItemFactory itemFactory) {
        this.filas = filas;
        this.columnas = columnas;
        this.grilla = grilla;
        this.celdaFactory = celdaFactory;
        this.entidadFactory = entidadFactory;
        this.itemFactory = itemFactory;
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
            ponerPiso(tablero, registro, posicion);
            Entidad entidad = entidadFactory.crear(caracter, posicion);
            entidad.agregarseA(tablero);
            entidad.registrarEnlaces(registro);
        } else if (itemFactory.conoce(caracter)) {
            ponerPiso(tablero, registro, posicion);
            Item item = itemFactory.crear(caracter, posicion);
            tablero.agregarItem(item);
        } else {
            Celda celda = celdaFactory.crear(caracter, posicion);
            tablero.setCelda(posicion, celda);
            celda.registrarEnlaces(registro);
        }
    }

    private void ponerPiso(Tablero tablero, RegistroEnlaces registro, Posicion posicion) {
        Celda piso = celdaFactory.crear(' ', posicion);
        tablero.setCelda(posicion, piso);
        piso.registrarEnlaces(registro);
    }

    @Override
    public boolean tieneVisionLimitada() {
        return false;
    }

    @Override
    public int getRadioVision() {
        return 0;
    }
}
