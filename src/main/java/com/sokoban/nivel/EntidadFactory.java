package com.sokoban.nivel;

import com.sokoban.dominio.CajaFragil;
import com.sokoban.dominio.CajaLlave;
import com.sokoban.dominio.CajaNormal;
import com.sokoban.dominio.CajaPesada;
import com.sokoban.dominio.Entidad;
import com.sokoban.dominio.Jugador;
import com.sokoban.dominio.Posicion;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Factory Method para entidades. Igual que CeldaFactory: registro Map sin switch.
 * 'conoce' permite al lector enrutar un caracter a entidad o a celda usando el
 * registro (no inspeccionando tipos de objetos).
 */
public class EntidadFactory {

    private static final int RESISTENCIA_FRAGIL = 3;

    private final Map<Character, Function<Posicion, Entidad>> creadores = new HashMap<>();

    public EntidadFactory() {
        creadores.put('@', Jugador::new);
        creadores.put('$', CajaNormal::new);
        creadores.put('K', CajaLlave::new);
        creadores.put('P', CajaPesada::new);
        creadores.put('F', posicion -> new CajaFragil(posicion, RESISTENCIA_FRAGIL));
    }

    public boolean conoce(char caracter) {
        return creadores.containsKey(caracter);
    }

    public Entidad crear(char caracter, Posicion posicion) {
        return creadores.get(caracter).apply(posicion);
    }
}
