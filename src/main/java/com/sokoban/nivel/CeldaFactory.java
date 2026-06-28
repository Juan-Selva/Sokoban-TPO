package com.sokoban.nivel;

import com.sokoban.dominio.Celda;
import com.sokoban.dominio.CeldaVacia;
import com.sokoban.dominio.Cerrojo;
import com.sokoban.dominio.Destino;
import com.sokoban.dominio.MuroAbiertoCerrado;
import com.sokoban.dominio.Pared;
import com.sokoban.dominio.Posicion;
import com.sokoban.dominio.TerrenoResbaladizo;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Factory Method: mapea un caracter del .txt a la celda concreta mediante un
 * registro Map<Character, creador>. Sin switch ni if-else por tipo: agregar una
 * celda nueva es registrar una entrada mas (Open/Closed).
 */
public class CeldaFactory {

    private final Map<Character, Function<Posicion, Celda>> creadores = new HashMap<>();

    public CeldaFactory() {
        creadores.put(' ', CeldaVacia::new);
        creadores.put('#', Pared::new);
        creadores.put('.', Destino::new);
        creadores.put('~', TerrenoResbaladizo::new);
        creadores.put('C', Cerrojo::new);
        creadores.put('M', MuroAbiertoCerrado::new);
    }

    /** Crea la celda para el caracter; los desconocidos se tratan como piso. */
    public Celda crear(char caracter, Posicion posicion) {
        return creadores.getOrDefault(caracter, CeldaVacia::new).apply(posicion);
    }
}
